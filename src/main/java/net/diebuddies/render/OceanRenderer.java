package net.diebuddies.render;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.DeviceFeatures;
import com.mojang.blaze3d.systems.DeviceInfo;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.List;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.opengl.Data;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.OceanMesh;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.ocean.ProxyOceanLayer;
import net.diebuddies.render.shader.PhysicsShaders;
import net.diebuddies.render.util.DynamicUniformsExtension;
import net.diebuddies.render.util.OceanChunkUniform;
import net.diebuddies.render.util.OceanUniform;
import net.diebuddies.util.Pool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.DynamicUniforms.Transform;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector2f;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.system.MemoryUtil;

public class OceanRenderer {
   private static final int INDEX_TYPE_BYTES = 2;
   private static final int INTERLEAVED_MULTI_DRAW_INTS_PER_COMMAND = 3;
   private static final int INDIRECT_INDEXED_DRAW_COMMAND_BYTES = 20;
   private static final int INITIAL_INDIRECT_DRAW_COMMAND_BUFFER_BYTES = 5120;
   private MainRenderer mainRenderer;
   private OceanRippleRenderer rippleRenderer;
   private Long2ObjectMap<OceanRenderer.OceanDrawCall> drawCalls = new Long2ObjectOpenHashMap();
   private Long2ObjectMap<OceanRenderer.OceanDrawCall> rippleDrawCalls = new Long2ObjectOpenHashMap();
   private Short2ObjectMap<List<OceanMesh>> visibleMeshes = new Short2ObjectOpenHashMap();
   private ShortList meshLayers = new ShortArrayList();
   private Pool<OceanRenderer.OceanDrawCall> pool = new Pool<>(100, OceanRenderer.OceanDrawCall::new, drawCall -> drawCall.reset());
   private Pool<List<OceanMesh>> meshPool = new Pool<>(100, () -> new ObjectArrayList(), meshList -> meshList.clear());
   private Matrix4f currentPose = new Matrix4f();
   private Matrix3f tmp = new Matrix3f();
   private OceanRenderer.MultiDrawMode multiDrawMode = OceanRenderer.MultiDrawMode.UNKNOWN;
   private int maxInterleavedMultiDrawCount = 1;
   private IntBuffer interleavedDrawParameters;
   private PointerBuffer separateFirstIndexOffsets;
   private IntBuffer separateIndexCounts;
   private IntBuffer separateVertexOffsets;
   private final List<MappableRingBuffer> oldIndirectDrawCommandBuffers = new ObjectArrayList();
   private MappableRingBuffer indirectDrawCommandBuffer;
   private int indirectDrawCommandBufferCapacity;
   private int indirectDrawCommandWriteOffset;

   public OceanRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
      this.rippleRenderer = new OceanRippleRenderer(mainRenderer);
   }

   public void render(PhysicsWorld physics, ClientLevel level, Matrix4fStack modelView, Vec3 view) {
   }

   private void sendRippleDrawCalls(
      PhysicsWorld physics, short layerPos, ClientLevel level, GpuTextureView waterTexture, Matrix3f normal, Matrix4fStack modelView, Vec3 view
   ) {
      if (!this.rippleDrawCalls.isEmpty()) {
         OceanWorld oceanWorld = physics.getOceanWorld();
         ProxyOceanLayer layer = oceanWorld.getOceanLayer(layerPos);
         boolean renderRipples = false;
         if (layer != null && (layer.needsRippleUpdate() || this.rippleRenderer.hasActiveSimulation(layer))) {
            renderRipples = this.rippleRenderer.renderSmallWaves(physics, layer, level, modelView, view);
         }

         if (renderRipples && layer != null) {
            this.sendDrawCalls(oceanWorld, waterTexture, normal, null, this.drawCalls.values());
            layer.setNeedsRippleUpdate(layer.hasRippleImpulses());
            this.sendDrawCalls(oceanWorld, waterTexture, normal, layer, this.rippleDrawCalls.values());
         } else {
            ObjectIterator var11 = this.rippleDrawCalls.long2ObjectEntrySet().iterator();

            while (var11.hasNext()) {
               Entry<OceanRenderer.OceanDrawCall> entry = (Entry<OceanRenderer.OceanDrawCall>)var11.next();
               long meshIndex = entry.getLongKey();
               OceanRenderer.OceanDrawCall rippleDrawCall = (OceanRenderer.OceanDrawCall)entry.getValue();
               OceanRenderer.OceanDrawCall drawCall = (OceanRenderer.OceanDrawCall)this.drawCalls.get(meshIndex);
               if (drawCall == null) {
                  this.drawCalls.put(meshIndex, rippleDrawCall);
               } else {
                  drawCall.addCommands(rippleDrawCall);
                  this.pool.free(rippleDrawCall);
               }
            }

            this.rippleDrawCalls.clear();
         }
      }
   }

   private void sendDrawCalls(
      OceanWorld oceanWorld,
      GpuTextureView waterTexture,
      Matrix3f normalMatrix,
      @Nullable ProxyOceanLayer layer,
      Collection<OceanRenderer.OceanDrawCall> drawCalls
   ) {
      if (!drawCalls.isEmpty()) {
         this.uploadDrawCallTransforms(drawCalls);
         this.uploadDrawCallOceanChunkUniforms(drawCalls);
         GpuBufferSlice oceanUniformBuffer = this.uploadOceanUniform(oceanWorld);
         BasicDrawCall waterTextures = new BasicDrawCall();
         waterTextures.texture = waterTexture;
         RenderPass renderPass = this.bindOceanShader();
         this.setupOceanRendering(renderPass, waterTextures, oceanWorld, layer, oceanUniformBuffer);
         this.executeDrawCalls(renderPass, oceanWorld.getVertexFormat(), normalMatrix, drawCalls);
         renderPass.close();
      }
   }

   private RenderPass bindOceanShader() {
      RenderPipeline renderPipeline = PhysicsShaders.PHYSICS_OCEAN_PIPELINE;
      GlRenderPipeline customPipeline = null;
      if (StarterClient.iris() && Iris.isExtending()) {
         if (Iris.isShadowPass()) {
            if (Iris.getOceanShadowProgram() != null) {
               customPipeline = new GlRenderPipeline(renderPipeline, Iris.getOceanShadowProgram());
            }
         } else if (Iris.getOceanProgram() != null) {
            customPipeline = new GlRenderPipeline(renderPipeline, Iris.getOceanProgram());
         }
      } else if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
         if (Optifine.isShadowPass()) {
            if (!Optifine.useOceanShadowShader()) {
               renderPipeline = RenderPipelines.SOLID_TERRAIN;
            }
         } else if (!Optifine.useOceanShader()) {
            renderPipeline = PhysicsShaders.PHYSICS_OCEAN_PIPELINE;
         }
      } else if (StarterClient.optifabric) {
         renderPipeline = Optifine.getPhysicsVanillaOceanPipeline();
      }

      return this.mainRenderer.bindProperShader(() -> "Physics Mod Ocean", renderPipeline, customPipeline);
   }

   private void setupOceanRendering(
      RenderPass renderPass, BasicDrawCall waterTextures, OceanWorld oceanWorld, @Nullable ProxyOceanLayer layer, @Nullable GpuBufferSlice oceanUniformBuffer
   ) {
      if (StarterClient.iris() && Iris.isExtending()) {
         GL32C.glVertexAttrib3f(Data.NORMAL_SHADER.getAttribute(), 0.0F, 1.0F, 0.0F);
         Vector2f midCoord = oceanWorld.getWaterMidCoord();
         GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_TERRAIN_SHADER.getAttribute(), midCoord.x, midCoord.y);
         GL32C.glVertexAttrib4f(Data.TANGENT_TERRAIN_SHADER.getAttribute(), 0.0F, 0.0F, 1.0F, 1.0F);
         int mcEntityLocation = 5;
         GL32C.glVertexAttrib2s(mcEntityLocation, Iris.getMaterialID(Blocks.WATER.defaultBlockState()), (short)1);
         int mcMidBlockLocation = 8;
         GL32C.glVertexAttrib4Nub(mcMidBlockLocation, (byte)32, (byte)32, (byte)32, (byte)-1);
      } else if (StarterClient.optifabric) {
         GL32C.glVertexAttrib3f(Data.NORMAL.getAttribute(), 0.0F, 1.0F, 0.0F);
         Vector2f midCoord = oceanWorld.getWaterMidCoord();
         GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_OPTIFINE.getAttribute(), midCoord.x, midCoord.y);
         GL32C.glVertexAttrib4f(Data.TANGENT_OPTIFINE.getAttribute(), 0.0F, 0.0F, 1.0F, 1.0F);
         if (Optifine.isUsingShadersNoInternal()) {
            int mcEntityLocation = 11;
            GL32C.glVertexAttrib4f(
               mcEntityLocation,
               (float)Optifine.getMaterialID(Blocks.WATER.defaultBlockState()),
               (float)Optifine.getRenderType(Blocks.WATER.defaultBlockState()),
               -1.0F,
               -1.0F
            );
            int mcMidBlockLocation = 15;
            GL32C.glVertexAttrib4Nub(mcMidBlockLocation, (byte)32, (byte)32, (byte)32, (byte)-1);
         }
      }

      renderPass.setUniform("PhysicsOcean", oceanUniformBuffer);
      renderPass.bindTexture("Sampler0", waterTextures.texture, RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST, false));
      renderPass.bindTexture(
         "physics_ripples", this.rippleRenderer.getRippleTextureView(layer), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR, false)
      );
      renderPass.bindTexture("physics_foam", PhysicsMod.foamTexture, RenderSystem.getSamplerCache().getRepeat(FilterMode.LINEAR, false));
      renderPass.bindTexture(
         "physics_lightmap", Minecraft.getInstance().gameRenderer.levelLightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR, true)
      );
   }

   public void queueOceanDrawCall(
      Matrix4f cameraRotationMatrix,
      VertexFormat format,
      Vec3 view,
      OceanWorld world,
      OceanMesh oceanMesh,
      Long2ObjectMap<OceanRenderer.OceanDrawCall> drawCalls
   ) {
      if (oceanMesh.hasRenderBuffers()) {
         OceanRenderer.OceanDrawCall drawCall = (OceanRenderer.OceanDrawCall)drawCalls.get(oceanMesh.meshIndex);
         if (drawCall == null) {
            drawCall = this.pool.obtain();
            drawCalls.put(oceanMesh.meshIndex, drawCall);
            Matrix4d oceanTransformation = oceanMesh.transformation;
            float xo = (float)(oceanTransformation.m30() - view.x);
            float yo = (float)(oceanTransformation.m31() - view.y);
            float zo = (float)(oceanTransformation.m32() - view.z);
            cameraRotationMatrix.translate(xo, yo, zo, this.currentPose);
            drawCall.transform = MainRenderer.createTransformUniform(this.currentPose);
            drawCall.modelOffsetX = xo;
            drawCall.modelOffsetY = yo;
            drawCall.modelOffsetZ = zo;
            drawCall.offsetX = world.getWaveAnchorX() + oceanMesh.offsetX;
            drawCall.offsetZ = world.getWaveAnchorZ() + oceanMesh.offsetZ;
         }

         GpuBuffer vertexBuffer = oceanMesh.vertexBuffer;
         GpuBuffer indexBuffer = oceanMesh.indexBuffer;
         if (vertexBuffer != null && indexBuffer != null && oceanMesh.uploadedIndexCount > 0) {
            int baseVertex = (int)(oceanMesh.vertexBufferOffset / (long)format.getVertexSize());
            drawCall.getOrCreateBatch(vertexBuffer, indexBuffer).add(oceanMesh.uploadedIndexCount, oceanMesh.indexBufferOffset, baseVertex);
         }
      }
   }

   private void uploadDrawCallTransforms(Collection<OceanRenderer.OceanDrawCall> drawCalls) {
      if (!drawCalls.isEmpty()) {
         DynamicUniformsExtension dynamicUniforms = (DynamicUniformsExtension)RenderSystem.getDynamicUniforms();
         OceanRenderer.OceanDrawCall[] drawCallArray = drawCalls.toArray(new OceanRenderer.OceanDrawCall[0]);
         Transform[] transforms = new Transform[drawCallArray.length];

         for (int i = 0; i < drawCallArray.length; i++) {
            transforms[i] = drawCallArray[i].transform;
         }

         GpuBufferSlice[] transformSlices = dynamicUniforms.physicsmod$getDynamicUniformStorage().writeUniforms(transforms);

         for (int i = 0; i < drawCallArray.length; i++) {
            drawCallArray[i].transformBuffer = transformSlices[i];
         }
      }
   }

   private void uploadDrawCallOceanChunkUniforms(Collection<OceanRenderer.OceanDrawCall> drawCalls) {
      if (!drawCalls.isEmpty()) {
         DynamicUniformsExtension dynamicUniforms = (DynamicUniformsExtension)RenderSystem.getDynamicUniforms();
         OceanRenderer.OceanDrawCall[] drawCallArray = drawCalls.toArray(new OceanRenderer.OceanDrawCall[0]);
         OceanChunkUniform[] chunkUniforms = new OceanChunkUniform[drawCallArray.length];

         for (int i = 0; i < drawCallArray.length; i++) {
            OceanRenderer.OceanDrawCall drawCall = drawCallArray[i];
            chunkUniforms[i] = new OceanChunkUniform(
               drawCall.modelOffsetX, drawCall.modelOffsetY, drawCall.modelOffsetZ, (float)drawCall.offsetX, (float)drawCall.offsetZ
            );
         }

         GpuBufferSlice[] chunkSlices = dynamicUniforms.physicsmod$writeOceanChunkUniforms(chunkUniforms);

         for (int i = 0; i < drawCallArray.length; i++) {
            drawCallArray[i].oceanChunkBuffer = chunkSlices[i];
         }
      }
   }

   @Nullable
   private GpuBufferSlice uploadOceanUniform(OceanWorld oceanWorld) {
      DynamicUniformsExtension dynamicUniforms = (DynamicUniformsExtension)RenderSystem.getDynamicUniforms();
      OceanUniform oceanUniform = oceanWorld.createRenderUniform((float)this.rippleRenderer.getRippleRange());
      GpuBufferSlice[] slices = dynamicUniforms.physicsmod$writeOceanUniforms(oceanUniform);
      return slices.length > 0 ? slices[0] : null;
   }

   public void executeDrawCalls(RenderPass renderPass, VertexFormat format, Matrix3f normalMatrix, Collection<OceanRenderer.OceanDrawCall> drawCalls) {
      RenderSystem.getModelViewStack().pushMatrix();

      try {
         OceanRenderer.MultiDrawMode mode = this.getMultiDrawMode();

         for (OceanRenderer.OceanDrawCall drawCall : drawCalls) {
            if (drawCall.hasCommands()) {
               if (StarterClient.iris()) {
                  RenderSystem.getModelViewStack().set(drawCall.transform.modelView());
               } else if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
                  Optifine.setDynamicTransforms(drawCall.transformBuffer);
               }

               renderPass.setUniform("DynamicTransforms", drawCall.transformBuffer);
               renderPass.setUniform("PhysicsOceanChunk", drawCall.oceanChunkBuffer);

               for (OceanRenderer.OceanMultiDrawIndexedBatch batch : drawCall.indexedBatches) {
                  try {
                     this.executeMultiDrawIndexedBatch(renderPass, mode, batch);
                  } catch (IllegalArgumentException | IllegalStateException | UnsupportedOperationException var14) {
                     if (!this.canUseIndirectMultiDraw()) {
                        throw var14;
                     }

                     this.multiDrawMode = OceanRenderer.MultiDrawMode.INDIRECT;
                     mode = OceanRenderer.MultiDrawMode.INDIRECT;
                     this.executeIndirectMultiDrawIndexedBatch(renderPass, batch, batch.size());
                  }
               }
            }
         }
      } finally {
         this.pool.freeAll(drawCalls);
         drawCalls.clear();
      }

      RenderSystem.getModelViewStack().popMatrix();
   }

   private OceanRenderer.MultiDrawMode getMultiDrawMode() {
      if (this.multiDrawMode != OceanRenderer.MultiDrawMode.UNKNOWN) {
         return this.multiDrawMode;
      } else {
         DeviceInfo info = RenderSystem.getDevice().getDeviceInfo();
         DeviceFeatures features = info.features();
         if (features.multiDrawDirectInterleaved()) {
            int limit = info.limits().maxMultiDrawDirectInterleavedDrawCount();
            if (limit > 0) {
               this.maxInterleavedMultiDrawCount = limit;
               this.multiDrawMode = OceanRenderer.MultiDrawMode.INTERLEAVED;
            } else if (features.multiDrawDirectSeparate()) {
               this.multiDrawMode = OceanRenderer.MultiDrawMode.SEPARATE;
            } else {
               if (!this.canUseIndirectMultiDraw()) {
                  throw new UnsupportedOperationException("Device does not support multi draw direct or multi draw indirect");
               }

               this.multiDrawMode = OceanRenderer.MultiDrawMode.INDIRECT;
            }
         } else if (features.multiDrawDirectSeparate()) {
            this.multiDrawMode = OceanRenderer.MultiDrawMode.SEPARATE;
         } else {
            if (!this.canUseIndirectMultiDraw()) {
               throw new UnsupportedOperationException("Device does not support multi draw direct or multi draw indirect");
            }

            this.multiDrawMode = OceanRenderer.MultiDrawMode.INDIRECT;
         }

         return this.multiDrawMode;
      }
   }

   private boolean canUseIndirectMultiDraw() {
      DeviceFeatures features = RenderSystem.getDevice().getDeviceInfo().features();
      return features.drawIndirect() && features.multiDrawIndirect();
   }

   private void executeMultiDrawIndexedBatch(RenderPass renderPass, OceanRenderer.MultiDrawMode mode, OceanRenderer.OceanMultiDrawIndexedBatch batch) {
      int drawCount = batch.size();
      if (drawCount > 0) {
         renderPass.setVertexBuffer(0, batch.vertexBuffer.slice());
         renderPass.setIndexBuffer(batch.indexBuffer, IndexType.SHORT);
         switch (mode) {
            case UNKNOWN:
               throw new IllegalStateException("Multi draw mode was not resolved");
            case INTERLEAVED:
               this.executeInterleavedMultiDrawIndexedBatch(renderPass, batch, drawCount);
               break;
            case SEPARATE:
               this.executeSeparateMultiDrawIndexedBatch(renderPass, batch, drawCount);
               break;
            case INDIRECT:
               this.executeIndirectMultiDrawIndexedBatch(renderPass, batch, drawCount);
         }
      }
   }

   private void executeInterleavedMultiDrawIndexedBatch(RenderPass renderPass, OceanRenderer.OceanMultiDrawIndexedBatch batch, int drawCount) {
      int firstDraw = 0;

      while (firstDraw < drawCount) {
         int currentDrawCount = Math.min(drawCount - firstDraw, this.maxInterleavedMultiDrawCount);
         IntBuffer drawParameters = this.getInterleavedDrawParameters(currentDrawCount);

         for (int i = 0; i < currentDrawCount; i++) {
            int drawIndex = firstDraw + i;
            int base = i * 3;
            drawParameters.put(base, batch.getFirstIndex(drawIndex));
            drawParameters.put(base + 1, batch.getIndexCount(drawIndex));
            drawParameters.put(base + 2, batch.getBaseVertex(drawIndex));
         }

         renderPass.multiDrawIndexed(drawParameters, 1, 0, currentDrawCount);
         firstDraw += currentDrawCount;
      }
   }

   private void executeSeparateMultiDrawIndexedBatch(RenderPass renderPass, OceanRenderer.OceanMultiDrawIndexedBatch batch, int drawCount) {
      PointerBuffer firstIndexOffsets = this.getSeparateFirstIndexOffsets(drawCount);
      IntBuffer indexCounts = this.getSeparateIndexCounts(drawCount);
      IntBuffer vertexOffsets = this.getSeparateVertexOffsets(drawCount);

      for (int i = 0; i < drawCount; i++) {
         firstIndexOffsets.put(i, batch.getFirstIndexOffset(i));
         indexCounts.put(i, batch.getIndexCount(i));
         vertexOffsets.put(i, batch.getBaseVertex(i));
      }

      renderPass.multiDrawIndexed(firstIndexOffsets, indexCounts, vertexOffsets, drawCount);
   }

   private IntBuffer getInterleavedDrawParameters(int drawCount) {
      int requiredInts = drawCount * 3;
      if (this.interleavedDrawParameters == null) {
         this.interleavedDrawParameters = MemoryUtil.memAllocInt(requiredInts);
      } else if (this.interleavedDrawParameters.capacity() < requiredInts) {
         this.interleavedDrawParameters = MemoryUtil.memRealloc(this.interleavedDrawParameters, requiredInts);
      }

      this.interleavedDrawParameters.clear();
      this.interleavedDrawParameters.limit(requiredInts);
      return this.interleavedDrawParameters;
   }

   private PointerBuffer getSeparateFirstIndexOffsets(int drawCount) {
      if (this.separateFirstIndexOffsets == null) {
         this.separateFirstIndexOffsets = MemoryUtil.memAllocPointer(drawCount);
      } else if (this.separateFirstIndexOffsets.capacity() < drawCount) {
         this.separateFirstIndexOffsets = MemoryUtil.memRealloc(this.separateFirstIndexOffsets, drawCount);
      }

      this.separateFirstIndexOffsets.clear();
      this.separateFirstIndexOffsets.limit(drawCount);
      return this.separateFirstIndexOffsets;
   }

   private IntBuffer getSeparateIndexCounts(int drawCount) {
      if (this.separateIndexCounts == null) {
         this.separateIndexCounts = MemoryUtil.memAllocInt(drawCount);
      } else if (this.separateIndexCounts.capacity() < drawCount) {
         this.separateIndexCounts = MemoryUtil.memRealloc(this.separateIndexCounts, drawCount);
      }

      this.separateIndexCounts.clear();
      this.separateIndexCounts.limit(drawCount);
      return this.separateIndexCounts;
   }

   private IntBuffer getSeparateVertexOffsets(int drawCount) {
      if (this.separateVertexOffsets == null) {
         this.separateVertexOffsets = MemoryUtil.memAllocInt(drawCount);
      } else if (this.separateVertexOffsets.capacity() < drawCount) {
         this.separateVertexOffsets = MemoryUtil.memRealloc(this.separateVertexOffsets, drawCount);
      }

      this.separateVertexOffsets.clear();
      this.separateVertexOffsets.limit(drawCount);
      return this.separateVertexOffsets;
   }

   private void executeIndirectMultiDrawIndexedBatch(RenderPass renderPass, OceanRenderer.OceanMultiDrawIndexedBatch batch, int drawCount) {
      GpuBufferSlice commandSlice = this.writeIndirectDrawCommands(batch, drawCount);
      renderPass.drawIndexedIndirect(commandSlice, drawCount);
   }

   private GpuBufferSlice writeIndirectDrawCommands(OceanRenderer.OceanMultiDrawIndexedBatch batch, int drawCount) {
      int requiredBytes = drawCount * 20;
      MappableRingBuffer commandBuffer = this.getIndirectDrawCommandBuffer(requiredBytes);
      int offset = this.indirectDrawCommandWriteOffset;
      MappedView view = commandBuffer.currentBuffer().slice((long)offset, (long)requiredBytes).map(false, true);

      try {
         ByteBuffer drawCommands = view.data();

         for (int i = 0; i < drawCount; i++) {
            int base = i * 20;
            drawCommands.putInt(base, batch.getIndexCount(i));
            drawCommands.putInt(base + 4, 1);
            drawCommands.putInt(base + 8, batch.getFirstIndex(i));
            drawCommands.putInt(base + 12, batch.getBaseVertex(i));
            drawCommands.putInt(base + 16, 0);
         }
      } catch (Throwable var11) {
         if (view != null) {
            try {
               view.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (view != null) {
         view.close();
      }

      this.indirectDrawCommandWriteOffset += requiredBytes;
      return commandBuffer.currentBuffer().slice((long)offset, (long)requiredBytes);
   }

   private MappableRingBuffer getIndirectDrawCommandBuffer(int requiredBytes) {
      int requiredCapacity = this.indirectDrawCommandWriteOffset + requiredBytes;
      if (this.indirectDrawCommandBuffer == null) {
         this.indirectDrawCommandBufferCapacity = smallestEncompassingPowerOfTwo(Math.max(5120, requiredCapacity));
         this.indirectDrawCommandBuffer = this.createIndirectDrawCommandBuffer(this.indirectDrawCommandBufferCapacity);
      } else if (this.indirectDrawCommandBufferCapacity < requiredCapacity) {
         this.oldIndirectDrawCommandBuffers.add(this.indirectDrawCommandBuffer);
         this.indirectDrawCommandBufferCapacity = smallestEncompassingPowerOfTwo(Math.max(5120, requiredCapacity));
         this.indirectDrawCommandBuffer = this.createIndirectDrawCommandBuffer(this.indirectDrawCommandBufferCapacity);
         this.indirectDrawCommandWriteOffset = 0;
      }

      return this.indirectDrawCommandBuffer;
   }

   private MappableRingBuffer createIndirectDrawCommandBuffer(int size) {
      return new MappableRingBuffer(() -> "Physics Mod Ocean Indirect Draw Commands", 514, size);
   }

   private void beginIndirectDrawFrame() {
      this.indirectDrawCommandWriteOffset = 0;
   }

   private void endIndirectDrawFrame() {
      this.indirectDrawCommandWriteOffset = 0;
      if (this.indirectDrawCommandBuffer != null) {
         this.indirectDrawCommandBuffer.rotate();
      }

      if (!this.oldIndirectDrawCommandBuffers.isEmpty()) {
         for (MappableRingBuffer oldBuffer : this.oldIndirectDrawCommandBuffers) {
            oldBuffer.close();
         }

         this.oldIndirectDrawCommandBuffers.clear();
      }
   }

   private static int smallestEncompassingPowerOfTwo(int value) {
      return value <= 1 ? 1 : 1 << 32 - Integer.numberOfLeadingZeros(value - 1);
   }

   public void destroy() {
      if (this.interleavedDrawParameters != null) {
         MemoryUtil.memFree(this.interleavedDrawParameters);
         this.interleavedDrawParameters = null;
      }

      if (this.separateFirstIndexOffsets != null) {
         MemoryUtil.memFree(this.separateFirstIndexOffsets);
         this.separateFirstIndexOffsets = null;
      }

      if (this.separateIndexCounts != null) {
         MemoryUtil.memFree(this.separateIndexCounts);
         this.separateIndexCounts = null;
      }

      if (this.separateVertexOffsets != null) {
         MemoryUtil.memFree(this.separateVertexOffsets);
         this.separateVertexOffsets = null;
      }

      for (MappableRingBuffer oldBuffer : this.oldIndirectDrawCommandBuffers) {
         oldBuffer.close();
      }

      this.oldIndirectDrawCommandBuffers.clear();
      if (this.indirectDrawCommandBuffer != null) {
         this.indirectDrawCommandBuffer.close();
         this.indirectDrawCommandBuffer = null;
      }

      this.rippleRenderer.destroy();
   }

   private static enum MultiDrawMode {
      UNKNOWN,
      INTERLEAVED,
      SEPARATE,
      INDIRECT;
   }

   private static class OceanDrawCall {
      public Transform transform;
      public GpuBufferSlice transformBuffer;
      public GpuBufferSlice oceanChunkBuffer;
      public float modelOffsetX;
      public float modelOffsetY;
      public float modelOffsetZ;
      public int offsetX;
      public int offsetZ;
      public final List<OceanRenderer.OceanMultiDrawIndexedBatch> indexedBatches = new ObjectArrayList();

      public OceanRenderer.OceanMultiDrawIndexedBatch getOrCreateBatch(GpuBuffer vertexBuffer, GpuBuffer indexBuffer) {
         for (OceanRenderer.OceanMultiDrawIndexedBatch batch : this.indexedBatches) {
            if (batch.vertexBuffer == vertexBuffer && batch.indexBuffer == indexBuffer) {
               return batch;
            }
         }

         OceanRenderer.OceanMultiDrawIndexedBatch batchx = new OceanRenderer.OceanMultiDrawIndexedBatch(vertexBuffer, indexBuffer);
         this.indexedBatches.add(batchx);
         return batchx;
      }

      public void addCommands(OceanRenderer.OceanDrawCall other) {
         for (OceanRenderer.OceanMultiDrawIndexedBatch otherBatch : other.indexedBatches) {
            this.getOrCreateBatch(otherBatch.vertexBuffer, otherBatch.indexBuffer).add(otherBatch);
         }
      }

      public boolean hasCommands() {
         for (OceanRenderer.OceanMultiDrawIndexedBatch batch : this.indexedBatches) {
            if (batch.size() > 0) {
               return true;
            }
         }

         return false;
      }

      public void reset() {
         for (OceanRenderer.OceanMultiDrawIndexedBatch batch : this.indexedBatches) {
            batch.clear();
         }

         this.indexedBatches.clear();
         MainRenderer.freeTransform(this.transform);
         this.transform = null;
         this.transformBuffer = null;
         this.oceanChunkBuffer = null;
         this.modelOffsetX = 0.0F;
         this.modelOffsetY = 0.0F;
         this.modelOffsetZ = 0.0F;
         this.offsetX = 0;
         this.offsetZ = 0;
      }
   }

   private static class OceanMultiDrawIndexedBatch {
      public final GpuBuffer vertexBuffer;
      public final GpuBuffer indexBuffer;
      private final IntList indexCounts = new IntArrayList();
      private final LongList firstIndexOffsets = new LongArrayList();
      private final IntList baseVertices = new IntArrayList();

      public OceanMultiDrawIndexedBatch(GpuBuffer vertexBuffer, GpuBuffer indexBuffer) {
         this.vertexBuffer = vertexBuffer;
         this.indexBuffer = indexBuffer;
      }

      public void add(int indexCount, long firstIndexOffset, int baseVertex) {
         this.indexCounts.add(indexCount);
         this.firstIndexOffsets.add(firstIndexOffset);
         this.baseVertices.add(baseVertex);
      }

      public void add(OceanRenderer.OceanMultiDrawIndexedBatch batch) {
         this.indexCounts.addAll(batch.indexCounts);
         this.firstIndexOffsets.addAll(batch.firstIndexOffsets);
         this.baseVertices.addAll(batch.baseVertices);
      }

      public int getIndexCount(int index) {
         return this.indexCounts.getInt(index);
      }

      public long getFirstIndexOffset(int index) {
         return this.firstIndexOffsets.getLong(index);
      }

      public int getFirstIndex(int index) {
         return (int)(this.firstIndexOffsets.getLong(index) / 2L);
      }

      public int getBaseVertex(int index) {
         return this.baseVertices.getInt(index);
      }

      public int size() {
         return this.indexCounts.size();
      }

      public void clear() {
         this.indexCounts.clear();
         this.firstIndexOffsets.clear();
         this.baseVertices.clear();
      }
   }
}
