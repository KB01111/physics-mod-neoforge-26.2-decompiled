package net.diebuddies.render;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView;
import com.mojang.blaze3d.systems.DeviceFeatures;
import com.mojang.blaze3d.systems.DeviceInfo;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.opengl.Data;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.snow.ChunkEntity;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.math.AABB3D;
import net.diebuddies.render.util.DynamicUniformsExtension;
import net.diebuddies.util.Pool;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MappableRingBuffer;
import net.minecraft.client.renderer.DynamicUniforms.Transform;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.system.MemoryUtil;

public class SnowRenderer {
   public static final int SNOW_ENTITY_ID = 829925;
   private static final int INDEX_TYPE_BYTES = 4;
   private static final int INTERLEAVED_MULTI_DRAW_INTS_PER_COMMAND = 3;
   private static final int INDIRECT_INDEXED_DRAW_COMMAND_BYTES = 20;
   private static final int INITIAL_INDIRECT_DRAW_COMMAND_BUFFER_BYTES = 5120;
   private final MainRenderer mainRenderer;
   private final List<SnowRenderer.SnowDrawCall> drawCalls = new ObjectArrayList();
   private final Pool<SnowRenderer.SnowDrawCall> pool = new Pool<>(100, SnowRenderer.SnowDrawCall::new, SnowRenderer.SnowDrawCall::reset);
   private final Matrix4f transformation = new Matrix4f();
   private final Matrix4f currentPose = new Matrix4f();
   private SnowRenderer.MultiDrawMode multiDrawMode = SnowRenderer.MultiDrawMode.UNKNOWN;
   private int maxInterleavedMultiDrawCount = 1;
   private IntBuffer interleavedDrawParameters;
   private PointerBuffer separateFirstIndexOffsets;
   private IntBuffer separateIndexCounts;
   private IntBuffer separateVertexOffsets;
   private final List<MappableRingBuffer> oldIndirectDrawCommandBuffers = new ObjectArrayList();
   private MappableRingBuffer indirectDrawCommandBuffer;
   private int indirectDrawCommandBufferCapacity;
   private int indirectDrawCommandWriteOffset;

   public SnowRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
   }

   public void render(PhysicsWorld physics, ClientLevel level, Matrix4fStack viewMatrixStack, Vec3 view) {
   }

   private boolean isVisible(AABB3D aabb, Vec3 view) {
      Vector3d start = aabb.start;
      Vector3d end = aabb.end;
      return this.mainRenderer
         .frustumInt
         .testAab(
            (float)(start.x - view.x),
            (float)(start.y - view.y),
            (float)(start.z - view.z),
            (float)(end.x - view.x),
            (float)(end.y - view.y),
            (float)(end.z - view.z)
         );
   }

   private void uploadDrawCallTransforms(List<SnowRenderer.SnowDrawCall> drawCalls) {
      if (!drawCalls.isEmpty()) {
         DynamicUniformsExtension dynamicUniforms = (DynamicUniformsExtension)RenderSystem.getDynamicUniforms();
         Transform[] transforms = new Transform[drawCalls.size()];

         for (int i = 0; i < drawCalls.size(); i++) {
            transforms[i] = drawCalls.get(i).transform;
         }

         GpuBufferSlice[] transformSlices = dynamicUniforms.physicsmod$getDynamicUniformStorage().writeUniforms(transforms);

         for (int i = 0; i < drawCalls.size(); i++) {
            drawCalls.get(i).transformBuffer = transformSlices[i];
         }
      }
   }

   private void executeDrawCalls(RenderPass renderPass, List<SnowRenderer.SnowDrawCall> drawCalls, GpuTextureView whiteTexture, Matrix3f cameraNormal) {
      renderPass.bindTexture("Sampler0", whiteTexture, MainRenderer.NEAREST_SAMPLER);
      this.setupShaderModAttributes();
      RenderSystem.getModelViewStack().pushMatrix();

      try {
         SnowRenderer.MultiDrawMode mode = this.getMultiDrawMode();

         for (SnowRenderer.SnowDrawCall drawCall : drawCalls) {
            renderPass.setUniform("DynamicTransforms", drawCall.transformBuffer);
            if (StarterClient.iris()) {
               RenderSystem.getModelViewStack().set(drawCall.transform.modelView());
            } else if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
               Optifine.setDynamicTransforms(drawCall.transformBuffer);
            }

            for (SnowRenderer.SnowMultiDrawIndexedBatch batch : drawCall.indexedBatches) {
               try {
                  this.executeMultiDrawIndexedBatch(renderPass, mode, batch);
               } catch (IllegalArgumentException | IllegalStateException | UnsupportedOperationException var14) {
                  if (!this.canUseIndirectMultiDraw()) {
                     throw var14;
                  }

                  this.multiDrawMode = SnowRenderer.MultiDrawMode.INDIRECT;
                  mode = SnowRenderer.MultiDrawMode.INDIRECT;
                  this.executeIndirectMultiDrawIndexedBatch(renderPass, batch, batch.size());
               }
            }
         }
      } finally {
         this.pool.freeAll(drawCalls);
         this.drawCalls.clear();
      }

      RenderSystem.getModelViewStack().popMatrix();
   }

   private void setupShaderModAttributes() {
      boolean iris = StarterClient.iris() && Iris.isExtending();
      boolean optifine = StarterClient.optifabric && Optifine.isUsingShadersNoInternal();
      boolean shaderMod = iris || optifine;
      if (shaderMod) {
         GL32C.glVertexAttrib4f(Data.COLOR.getAttribute(), 1.0F, 1.0F, 1.0F, 1.0F);
         GL32C.glVertexAttrib2f(Data.TEX_COORD_SHADER.getAttribute(), 0.0F, 0.0F);
         GL32C.glVertexAttribI2ui(Data.OVERLAY.getAttribute(), 0, 10);
         if (optifine) {
            GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_OPTIFINE.getAttribute(), 0.0F, 0.0F);
         } else if (iris) {
            GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_SHADER.getAttribute(), 0.0F, 0.0F);
            GL32C.glVertexAttribI3ui(Data.ENTITY_ID_SHADER.getAttribute(), 829925, 0, 0);
         }
      }
   }

   private SnowRenderer.MultiDrawMode getMultiDrawMode() {
      if (this.multiDrawMode != SnowRenderer.MultiDrawMode.UNKNOWN) {
         return this.multiDrawMode;
      } else {
         DeviceInfo info = RenderSystem.getDevice().getDeviceInfo();
         DeviceFeatures features = info.features();
         if (features.multiDrawDirectInterleaved()) {
            int limit = info.limits().maxMultiDrawDirectInterleavedDrawCount();
            if (limit > 0) {
               this.maxInterleavedMultiDrawCount = limit;
               this.multiDrawMode = SnowRenderer.MultiDrawMode.INTERLEAVED;
            } else if (features.multiDrawDirectSeparate()) {
               this.multiDrawMode = SnowRenderer.MultiDrawMode.SEPARATE;
            } else {
               if (!this.canUseIndirectMultiDraw()) {
                  throw new UnsupportedOperationException("Device does not support multi draw direct or multi draw indirect");
               }

               this.multiDrawMode = SnowRenderer.MultiDrawMode.INDIRECT;
            }
         } else if (features.multiDrawDirectSeparate()) {
            this.multiDrawMode = SnowRenderer.MultiDrawMode.SEPARATE;
         } else {
            if (!this.canUseIndirectMultiDraw()) {
               throw new UnsupportedOperationException("Device does not support multi draw direct or multi draw indirect");
            }

            this.multiDrawMode = SnowRenderer.MultiDrawMode.INDIRECT;
         }

         return this.multiDrawMode;
      }
   }

   private boolean canUseIndirectMultiDraw() {
      DeviceFeatures features = RenderSystem.getDevice().getDeviceInfo().features();
      return features.drawIndirect() && features.multiDrawIndirect();
   }

   private void executeMultiDrawIndexedBatch(RenderPass renderPass, SnowRenderer.MultiDrawMode mode, SnowRenderer.SnowMultiDrawIndexedBatch batch) {
      int drawCount = batch.size();
      if (drawCount > 0) {
         renderPass.setVertexBuffer(0, batch.vertexBuffer.slice());
         renderPass.setIndexBuffer(batch.indexBuffer, IndexType.INT);
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

   private void executeInterleavedMultiDrawIndexedBatch(RenderPass renderPass, SnowRenderer.SnowMultiDrawIndexedBatch batch, int drawCount) {
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

   private void executeSeparateMultiDrawIndexedBatch(RenderPass renderPass, SnowRenderer.SnowMultiDrawIndexedBatch batch, int drawCount) {
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

   private void executeIndirectMultiDrawIndexedBatch(RenderPass renderPass, SnowRenderer.SnowMultiDrawIndexedBatch batch, int drawCount) {
      GpuBufferSlice commandSlice = this.writeIndirectDrawCommands(batch, drawCount);
      renderPass.drawIndexedIndirect(commandSlice, drawCount);
   }

   private GpuBufferSlice writeIndirectDrawCommands(SnowRenderer.SnowMultiDrawIndexedBatch batch, int drawCount) {
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
      return new MappableRingBuffer(() -> "Physics Mod Snow Indirect Draw Commands", 514, size);
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

   private Transform setupTransform(Vector3i position, Vec3 view, Matrix4f cameraRotation) {
      float scale = 1.0F / (float)IChunk.CHUNK_MULTIPLE;
      this.transformation
         .setTranslation(
            (float)(((double)(position.x * IChunk.CHUNK_SIZE) + 0.5) * (double)scale - view.x),
            (float)(((double)(position.y * IChunk.CHUNK_SIZE) + 0.5) * (double)scale - view.y),
            (float)(((double)(position.z * IChunk.CHUNK_SIZE) + 0.5) * (double)scale - view.z)
         );
      this.transformation.m00(scale);
      this.transformation.m11(scale);
      this.transformation.m22(scale);
      cameraRotation.mul(this.transformation, this.currentPose);
      return MainRenderer.createTransformUniform(this.currentPose);
   }

   private void queueSnowDrawCommand(ChunkEntity snowChunk, int vertexStride, SnowRenderer.SnowDrawCall drawCall) {
      GpuBuffer vertexBuffer = snowChunk.vertexBuffer;
      GpuBuffer indexBuffer = snowChunk.indexBuffer;
      if (vertexBuffer != null && indexBuffer != null && snowChunk.uploadedIndexCount > 0) {
         int baseVertex = (int)(snowChunk.vertexBufferOffset / (long)vertexStride);
         long firstIndexOffset = snowChunk.indexBufferOffset;
         int indexCount = snowChunk.uploadedIndexCount;
         drawCall.getOrCreateBatch(vertexBuffer, indexBuffer).add(indexCount, firstIndexOffset, baseVertex);
      }
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
   }

   private static enum MultiDrawMode {
      UNKNOWN,
      INTERLEAVED,
      SEPARATE,
      INDIRECT;
   }

   private static class SnowDrawCall {
      public Transform transform;
      public GpuBufferSlice transformBuffer;
      public final List<SnowRenderer.SnowMultiDrawIndexedBatch> indexedBatches = new ObjectArrayList();

      public SnowRenderer.SnowMultiDrawIndexedBatch getOrCreateBatch(GpuBuffer vertexBuffer, GpuBuffer indexBuffer) {
         for (SnowRenderer.SnowMultiDrawIndexedBatch batch : this.indexedBatches) {
            if (batch.vertexBuffer == vertexBuffer && batch.indexBuffer == indexBuffer) {
               return batch;
            }
         }

         SnowRenderer.SnowMultiDrawIndexedBatch batchx = new SnowRenderer.SnowMultiDrawIndexedBatch(vertexBuffer, indexBuffer);
         this.indexedBatches.add(batchx);
         return batchx;
      }

      public boolean hasCommands() {
         for (SnowRenderer.SnowMultiDrawIndexedBatch batch : this.indexedBatches) {
            if (batch.size() > 0) {
               return true;
            }
         }

         return false;
      }

      public void reset() {
         if (this.transform != null) {
            MainRenderer.freeTransform(this.transform);
         }

         this.transform = null;
         this.transformBuffer = null;

         for (SnowRenderer.SnowMultiDrawIndexedBatch batch : this.indexedBatches) {
            batch.clear();
         }

         this.indexedBatches.clear();
      }
   }

   private static class SnowMultiDrawIndexedBatch {
      public final GpuBuffer vertexBuffer;
      public final GpuBuffer indexBuffer;
      private final IntList indexCounts = new IntArrayList();
      private final LongList firstIndexOffsets = new LongArrayList();
      private final IntList baseVertices = new IntArrayList();

      public SnowMultiDrawIndexedBatch(GpuBuffer vertexBuffer, GpuBuffer indexBuffer) {
         this.vertexBuffer = vertexBuffer;
         this.indexBuffer = indexBuffer;
      }

      public void add(int indexCount, long firstIndexOffset, int baseVertex) {
         this.indexCounts.add(indexCount);
         this.firstIndexOffsets.add(firstIndexOffset);
         this.baseVertices.add(baseVertex);
      }

      public int getIndexCount(int index) {
         return this.indexCounts.getInt(index);
      }

      public long getFirstIndexOffset(int index) {
         return this.firstIndexOffsets.getLong(index);
      }

      public int getFirstIndex(int index) {
         return (int)(this.firstIndexOffsets.getLong(index) / 4L);
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
