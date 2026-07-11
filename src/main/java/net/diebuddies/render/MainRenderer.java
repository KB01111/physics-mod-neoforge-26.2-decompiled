package net.diebuddies.render;

import com.google.gson.JsonElement;
import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlRenderPass;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderPass.Draw;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.compat.Sodium;
import net.diebuddies.math.MatrixUtil;
import net.diebuddies.opengl.Data;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsRenderable;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.animation.AnimationType;
import net.diebuddies.render.shader.PhysicsShaders;
import net.diebuddies.render.util.BrightnessUniform;
import net.diebuddies.render.util.DynamicUniformsExtension;
import net.diebuddies.render.util.LightUniform;
import net.diebuddies.util.DoublyLinkedList;
import net.diebuddies.util.PerformanceTracker;
import net.diebuddies.util.Pool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.DynamicUniforms.Transform;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.world.level.CardinalLighting.Type;
import net.minecraft.world.phys.Vec3;
import org.joml.FrustumIntersection;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL33C;

public class MainRenderer {
   public static final GpuSampler NEAREST_SAMPLER = RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST, true);
   public static final GpuSampler NEAREST_CLAMP_SAMPLER = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST, true);
   public static final GpuSampler LINEAR_SAMPLER = RenderSystem.getSamplerCache().getRepeat(FilterMode.LINEAR, true);
   public static final GpuSampler LINEAR_CLAMP_SAMPLER = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR, true);
   public static final GpuSampler NEAREST_CLAMP_SAMPLER_NO_MIPMAP = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST, false);
   public static final Matrix4f IDENTITY_4_BY_4 = new Matrix4f();
   public static final Vector4f WHITE = new Vector4f(1.0F);
   public static final Vector3f ZERO = new Vector3f(0.0F);
   public static final Vector3f DIFFUSE_LIGHT_0 = new Vector3f(0.2F, 1.0F, -0.7F).normalize();
   public static final Vector3f DIFFUSE_LIGHT_1 = new Vector3f(-0.2F, 1.0F, 0.7F).normalize();
   public static final Vector3f NETHER_DIFFUSE_LIGHT_0 = new Vector3f(0.2F, 1.0F, -0.7F).normalize();
   public static final Vector3f NETHER_DIFFUSE_LIGHT_1 = new Vector3f(-0.2F, -1.0F, 0.7F).normalize();
   private static final LightUniform OVERWORLD_LIGHT = new LightUniform(new Vector3f(DIFFUSE_LIGHT_0), new Vector3f(DIFFUSE_LIGHT_1));
   private static final LightUniform NETHER_LIGHT = new LightUniform(new Vector3f(NETHER_DIFFUSE_LIGHT_0), new Vector3f(NETHER_DIFFUSE_LIGHT_1));
   public Vector3f lightDirection0 = new Vector3f();
   public Vector3f lightDirection1 = new Vector3f();
   public ProjectionMatrixBuffer levelProjectionMatrixBuffer;
   public FrustumIntersection frustumInt = new FrustumIntersection();
   private Matrix4f viewProjectionMatrix = new Matrix4f();
   public Matrix4f projectionMatrix = new Matrix4f();
   public Matrix4f viewMatrix = new Matrix4f();
   private Matrix4f storedProjectionMatrix = new Matrix4f();
   private Matrix4f storedViewMatrix = new Matrix4f();
   private Matrix4f transformation = new Matrix4f();
   private Matrix3f normalMatrix = new Matrix3f();
   private MutableBlockPos blockPos = new MutableBlockPos();
   private Matrix4f currentPose = new Matrix4f();
   private List<MainRenderer.PhysicsDrawCall> drawCalls;
   private boolean texturesInitialized = false;
   private PhysicsUpdater physicsUpdater;
   private SmokeRenderer smokeRenderer;
   private SmokeVolumeRenderer smokeVolumeRenderer;
   private SnowRenderer snowRenderer;
   public OceanRenderer oceanRenderer;
   public LiquidDeferredRenderer liquidDeferredRenderer;
   private LiquidRenderer liquidRenderer;
   private ClothRenderer clothRenderer;
   private TransparencyRenderer transparencyRenderer;
   private Pool<MainRenderer.PhysicsDrawCall> pool = new Pool<>(100, MainRenderer.PhysicsDrawCall::new, drawCall -> drawCall.reset());
   private final List<MainRenderer.PhysicsDrawCall> lightUploads = new ObjectArrayList();
   private int lastBrightness = -1;
   private static Pool<Transform> transformPool = new Pool<>(
      100, () -> new Transform(new Matrix4f(), new Vector4f(), new Vector3f(), new Matrix4f()), drawCall -> {
      }
   );

   public MainRenderer() {
      this.physicsUpdater = new PhysicsUpdater();
      this.smokeRenderer = new SmokeRenderer(this);
      this.smokeVolumeRenderer = new SmokeVolumeRenderer(this);
      this.snowRenderer = new SnowRenderer(this);
      this.oceanRenderer = new OceanRenderer(this);
      this.liquidRenderer = new LiquidRenderer(this);
      this.liquidDeferredRenderer = new LiquidDeferredRenderer(this);
      this.transparencyRenderer = new TransparencyRenderer(this);
      this.clothRenderer = new ClothRenderer(this);
      this.drawCalls = new ObjectArrayList();
      this.levelProjectionMatrixBuffer = new ProjectionMatrixBuffer("Physics Mod Projection");
   }

   public void setViewAndProjectionMatrix(Matrix4fc viewMatrix, Matrix4fc projectionMatrix) {
      this.viewMatrix.set(viewMatrix);
      this.projectionMatrix.set(projectionMatrix);
      this.projectionMatrix.mul(this.viewMatrix, this.viewProjectionMatrix);
      this.frustumInt.set(this.viewProjectionMatrix, true);
   }

   public void storeProjectionMatrix(Matrix4f projectionMatrix) {
      this.storedProjectionMatrix.set(projectionMatrix);
   }

   public Matrix4f getStoredProjectionMatrix() {
      return this.storedProjectionMatrix;
   }

   public void storeViewMatrix(Matrix4fc storedViewMatrix) {
      this.storedViewMatrix.set(storedViewMatrix);
   }

   public Matrix4f getStoredViewMatrix() {
      return this.storedViewMatrix;
   }

   public Matrix4f getViewProjectionMatrix() {
      return this.viewProjectionMatrix;
   }

   public void renderAll(ClientLevel level, ChunkSectionLayer chunkSectionLayer) {
      RenderSystem.assertOnRenderThread();
      if (!this.texturesInitialized) {
         TextureManager textureManager = Minecraft.getInstance().getTextureManager();
         textureManager.getTexture(PhysicsMod.WHITE_TEXTURE);
         textureManager.getTexture(PhysicsMod.BLACK_TEXTURE);
         textureManager.getTexture(PhysicsMod.SNOWBALL_TEXTURE);
         textureManager.getTexture(PhysicsMod.ENDERPEARL_TEXTURE);
         textureManager.getTexture(PhysicsMod.EGG_TEXTURE);
         textureManager.getTexture(PhysicsMod.SMOKE_TEXTURE);
         textureManager.getTexture(PhysicsMod.PUDDLE_TEXTURE);
         this.texturesInitialized = true;
      }

      this.verifyAndUtility();
      if (level != null) {
         Vec3 view = Minecraft.getInstance().gameRenderer.mainCamera().position();
         PhysicsMod mod = PhysicsMod.getInstance(level);
         PhysicsWorld physics = mod.getPhysicsWorld();
         physics.updateLastSeen();
         physics.getDynamicsWorld().getDebugRenderer().renderDebugGizmos();
         this.physicsUpdater.updatePhysics(mod, level, view, physics);
         boolean requiresRender = physics.getBodies().size() > 0
            || physics.getRagdolls().size() > 0
            || physics.getSnowWorld().getChunks().size() > 0
            || physics.getSmokeDomain().particleCount() > 0;
         if (requiresRender) {
            PerformanceTracker.startNoFlush("blocks_mobs_particles_rendering");
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(this.levelProjectionMatrixBuffer.getBuffer(this.projectionMatrix), ProjectionType.PERSPECTIVE);
            Matrix4fStack viewMatrixStack = RenderSystem.getModelViewStack();
            viewMatrixStack.pushMatrix();
            viewMatrixStack.set(this.viewMatrix);
            Minecraft.getInstance().gameRenderer.lighting().setupFor(com.mojang.blaze3d.platform.Lighting.Entry.LEVEL);
            Vector3d physicsOffset = physics.getOffset();
            double offsetX = physicsOffset.x - view.x;
            double offsetY = physicsOffset.y - view.y;
            double offsetZ = physicsOffset.z - view.z;
            boolean isShadowPass = StarterClient.iris() && Iris.isExtending() && Iris.isShadowPass() || StarterClient.optifabric && Optifine.isShadowPass();
            this.createPhysicsModels(physics);
            DoublyLinkedList<IRigidBody> bodies = physics.getBodies();
            Iterator<IRigidBody> it = bodies.iterator();
            int size = bodies.size();

            for (int i = 0; i < size; i++) {
               IRigidBody body = it.next();
               PhysicsRenderable entity = body.getEntity();
               if (entity.models != null) {
                  if (!isShadowPass) {
                     this.setTransformation(physics, body, entity);
                  }

                  this.queueDrawCall(physics, level, viewMatrixStack, view, offsetX, offsetY, offsetZ, body, entity);
               }
            }

            this.uploadDrawCallUniforms(this.drawCalls);
            this.executeDrawCalls(this.drawCalls, false);
            this.pool.freeAll(this.drawCalls);
            this.drawCalls.clear();
            PerformanceTracker.end("blocks_mobs_particles_rendering");
            this.snowRenderer.render(physics, level, viewMatrixStack, view);
            this.smokeRenderer.render(physics, level, viewMatrixStack, view);
            this.transparencyRenderer.render(physics, level, viewMatrixStack, view);
            viewMatrixStack.popMatrix();
            RenderSystem.restoreProjectionMatrix();
            if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
               for (int ix = 0; ix < 32; ix++) {
                  GL33C.glBindSampler(ix, 0);
               }
            }
         }
      }
   }

   public RenderPipeline getProperSolidRenderPipeline(boolean cull) {
      return cull ? PhysicsShaders.PHYSICS_ENTITY_CULL_PIPELINE : PhysicsShaders.PHYSICS_ENTITY_PIPELINE;
   }

   public RenderPipeline getProperTransparentRenderPipeline(boolean cull) {
      return cull ? PhysicsShaders.PHYSICS_ENTITY_TRANSPARENT_CULL_PIPELINE : PhysicsShaders.PHYSICS_ENTITY_TRANSPARENT_PIPELINE;
   }

   public RenderPipeline getProperSnowRenderPipeline(boolean cull) {
      return cull ? PhysicsShaders.PHYSICS_SNOW_CULL_PIPELINE : PhysicsShaders.PHYSICS_SNOW_PIPELINE;
   }

   public RenderPass bindProperShader(Supplier<String> passName, RenderPipeline pipeline, @Nullable GlRenderPipeline customPipeline) {
      RenderTarget renderTarget = Minecraft.getInstance().gameRenderer.mainRenderTarget();
      RenderPass renderPass = RenderSystem.getDevice()
         .createCommandEncoder()
         .createRenderPass(passName, renderTarget.getColorTextureView(), Optional.empty(), renderTarget.getDepthTextureView(), OptionalDouble.empty());
      if (customPipeline == null) {
         renderPass.setPipeline(pipeline);
      } else {
         ((GlRenderPass)renderPass.backend).pipeline = customPipeline;
      }

      RenderSystem.bindDefaultUniforms(renderPass);
      renderPass.bindTexture(
         "Sampler1", Minecraft.getInstance().gameRenderer.overlayTexture().getTextureView(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)
      );
      renderPass.bindTexture("Sampler2", Minecraft.getInstance().gameRenderer.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
      this.lastBrightness = -1;
      return renderPass;
   }

   public RenderPass bindProperShader(Supplier<String> passName, RenderPipeline pipeline) {
      return this.bindProperShader(passName, pipeline, null);
   }

   private void createPhysicsModels(PhysicsWorld physics) {
      Set<PhysicsRenderable> bodies = physics.getQueueForModelCreation();
      Iterator<PhysicsRenderable> it = bodies.iterator();
      int size = bodies.size();

      for (int i = 0; i < size; i++) {
         PhysicsRenderable entity = it.next();
         List<Model> models = entity.models;
         if (entity.models != null) {
            int modelsSize = models.size();

            for (int j = 0; j < modelsSize; j++) {
               Model model = models.get(j);
               Mesh mesh = model.mesh;
               if (mesh == null || mesh.indices.size() < 3) {
                  break;
               }

               if (!model.hasRenderData()) {
                  entity.getBoundingSphereRadius();
                  model.createModelMemorySegment(physics);
               }
            }
         }
      }

      bodies.clear();
   }

   public void setTransformation(PhysicsWorld physics, IRigidBody body, PhysicsRenderable entity) {
      if (body.hasTransformationChanged()) {
         MatrixUtil.slerp(entity, (float)physics.getRenderPercent(), entity.getRenderTransformation());
      }
   }

   public void queueDrawCall(
      PhysicsWorld physics,
      ClientLevel level,
      Matrix4fStack viewMatrix,
      Vec3 view,
      double offsetX,
      double offsetY,
      double offsetZ,
      IRigidBody body,
      PhysicsRenderable particle
   ) {
      List<Model> models = particle.models;
      Model modelZero = models.get(0);
      if (modelZero.hasRenderData()) {
         Matrix4f renderTransformation = particle.getRenderTransformation();
         this.transformation.set3x3(particle.getRenderTransformation());
         float posx = (float)((double)renderTransformation.m30() + offsetX);
         float posy = (float)((double)renderTransformation.m31() + offsetY);
         float posz = (float)((double)renderTransformation.m32() + offsetZ);
         this.transformation.setTranslation(posx, posy, posz);
         if (this.frustumInt.testSphere(posx, posy, posz, particle.getBoundingSphereRadius())) {
            double animationScale = (double)particle.getDespawnScale(level);
            float alpha = 1.0F;
            if (particle.getAnimationType() == AnimationType.Vanish) {
               alpha = Math.min(1.0F, (float)animationScale);
               animationScale = 1.0;
            } else if (particle.getAnimationType() == AnimationType.Shrink_and_Vanish) {
               alpha = Math.min(1.0F, (float)animationScale);
            }

            this.transformation.scale((float)animationScale);
            this.blockPos.set((double)posx + view.x, (double)posy + view.y, (double)posz + view.z);
            viewMatrix.mulAffine(this.transformation, this.currentPose);
            int size = models.size();
            int brightness = particle.getLight(level, this.blockPos);

            for (int j = 0; j < size; j++) {
               Model model = models.get(j);
               PhysicsWorld.ModelRenderBufferSlice slice = physics.getRenderSlice(model);
               if (slice != null) {
                  Transform transform = createTransformUniform(
                     this.currentPose, particle.getRed(), particle.getGreen(), particle.getBlue(), alpha, model.textureMatrix
                  );
                  if (StarterClient.sodium && model.animationSprite != null) {
                     Sodium.markSpriteActive(model.animationSprite);
                  }

                  boolean isTranslucent = alpha < 1.0F || model.translucent;
                  MainRenderer.PhysicsDrawCall drawCall;
                  if (isTranslucent) {
                     TransparencyRenderer.TranslucentPhysicsDrawCall translucentDrawCall = (TransparencyRenderer.TranslucentPhysicsDrawCall)this.transparencyRenderer
                        .pool
                        .obtain();
                     translucentDrawCall.distanceToCamera = (double)Vector3f.lengthSquared(posx, posy, posz);
                     drawCall = translucentDrawCall;
                     this.transparencyRenderer.drawCalls.add(translucentDrawCall);
                  } else {
                     drawCall = this.pool.obtain();
                     this.drawCalls.add(drawCall);
                  }

                  drawCall.transform = transform;
                  drawCall.light = this.setupLighting(this.transformation, level, model.shade);
                  drawCall.texture = model.textureID;
                  drawCall.brightness = brightness;
                  drawCall.backfaceCulling = model.backfaceCulling;
                  drawCall.shade = model.shade;
                  drawCall.vertexBuffer = slice.vertexBuffer();
                  drawCall.indexBuffer = slice.indexBuffer();
                  drawCall.firstIndex = (int)(slice.indexBufferOffset() / 4L);
                  drawCall.indexCount = slice.indexCount();
                  drawCall.baseVertex = (int)(slice.vertexBufferOffset() / (long)physics.getModelVertexFormat().getVertexSize());
               }
            }
         }
      }
   }

   public void uploadDrawCallUniforms(List<MainRenderer.PhysicsDrawCall> drawCalls) {
      if (!drawCalls.isEmpty()) {
         DynamicUniformsExtension dynamicUniforms = (DynamicUniformsExtension)RenderSystem.getDynamicUniforms();
         Transform[] transforms = new Transform[drawCalls.size()];
         BrightnessUniform[] brightnessUniforms = new BrightnessUniform[drawCalls.size()];

         for (int i = 0; i < drawCalls.size(); i++) {
            MainRenderer.PhysicsDrawCall drawCall = drawCalls.get(i);
            transforms[i] = drawCall.transform;
            int brightness = drawCall.brightness;
            brightnessUniforms[i] = createBrightnessUniform(brightness);
         }

         GpuBufferSlice[] transformSlices = dynamicUniforms.physicsmod$getDynamicUniformStorage().writeUniforms(transforms);
         GpuBufferSlice[] brightnessSlices = dynamicUniforms.physicsmod$writeBrightnessUniforms(brightnessUniforms);
         GpuBufferSlice overworldSlice = dynamicUniforms.physicsmod$getLightUniformStorage().writeUniform(OVERWORLD_LIGHT);
         GpuBufferSlice netherSlice = dynamicUniforms.physicsmod$getLightUniformStorage().writeUniform(NETHER_LIGHT);

         for (int i = 0; i < drawCalls.size(); i++) {
            MainRenderer.PhysicsDrawCall drawCall = drawCalls.get(i);
            drawCall.transformBuffer = transformSlices[i];
            drawCall.brightnessBuffer = brightnessSlices[i];
            if (drawCall.light == OVERWORLD_LIGHT) {
               drawCall.lightBuffer = overworldSlice;
            } else if (drawCall.light == NETHER_LIGHT) {
               drawCall.lightBuffer = netherSlice;
            } else {
               this.lightUploads.add(drawCall);
            }
         }

         if (!this.lightUploads.isEmpty()) {
            LightUniform[] lights = new LightUniform[this.lightUploads.size()];

            for (int ix = 0; ix < this.lightUploads.size(); ix++) {
               lights[ix] = this.lightUploads.get(ix).light;
            }

            GpuBufferSlice[] lightSlices = dynamicUniforms.physicsmod$writeLightUniforms(lights);

            for (int ix = 0; ix < this.lightUploads.size(); ix++) {
               this.lightUploads.get(ix).lightBuffer = lightSlices[ix];
            }

            this.lightUploads.clear();
         }
      }
   }

   public void executeDrawCalls(List<MainRenderer.PhysicsDrawCall> drawCalls, boolean translucent) {
      if (!drawCalls.isEmpty()) {
         Matrix3f cameraNormalMatrix = this.viewMatrix.normal(new Matrix3f());
         boolean shaderMod = StarterClient.iris() && Iris.isExtending() || StarterClient.optifabric && Optifine.isUsingShadersNoInternal();
         if (shaderMod) {
            GL33C.glVertexAttribI2ui(Data.OVERLAY.getAttribute(), 0, 10);
         }

         if (translucent) {
            this.executeOrderedDrawCalls(drawCalls, cameraNormalMatrix, true);
         } else {
            this.executeGroupedDrawCalls(drawCalls, cameraNormalMatrix);
         }
      }
   }

   private void executeOrderedDrawCalls(List<MainRenderer.PhysicsDrawCall> drawCalls, Matrix3f cameraNormalMatrix, boolean translucent) {
      int start = 0;

      while (start < drawCalls.size()) {
         MainRenderer.PhysicsDrawCall first = drawCalls.get(start);
         GpuTextureView texture = first.texture;
         boolean cull = first.backfaceCulling;

         int end;
         for (end = start + 1; end < drawCalls.size(); end++) {
            MainRenderer.PhysicsDrawCall next = drawCalls.get(end);
            if (next.texture != texture || next.backfaceCulling != cull) {
               break;
            }
         }

         RenderPipeline pipeline = this.getProperTransparentRenderPipeline(cull);
         RenderPass renderPass = this.bindProperShader(() -> "Physics Mod Translucent", pipeline);
         renderPass.bindTexture("Sampler0", texture, NEAREST_CLAMP_SAMPLER_NO_MIPMAP);
         List<Draw<List<MainRenderer.PhysicsDrawCall>>> draws = new ObjectArrayList();

         for (int i = start; i < end; i++) {
            MainRenderer.PhysicsDrawCall drawCall = drawCalls.get(i);
            if (drawCall.indexCount > 0) {
               draws.add(this.createIndexedDraw(renderPass, drawCall, cameraNormalMatrix));
            }
         }

         if (!draws.isEmpty()) {
            renderPass.drawMultipleIndexed(draws, null, null, List.of("DynamicTransforms", "Lighting", "PhysicsBrightness"), drawCalls);
         }

         renderPass.close();
         start = end;
      }
   }

   private void executeGroupedDrawCalls(List<MainRenderer.PhysicsDrawCall> drawCalls, Matrix3f cameraNormalMatrix) {
      Map<MainRenderer.DrawGroupKey, List<MainRenderer.PhysicsDrawCall>> groupedDraws = new LinkedHashMap<>();

      for (MainRenderer.PhysicsDrawCall drawCall : drawCalls) {
         MainRenderer.DrawGroupKey key = new MainRenderer.DrawGroupKey(drawCall.texture, drawCall.backfaceCulling);
         groupedDraws.computeIfAbsent(key, ignored -> new ObjectArrayList()).add(drawCall);
      }

      for (Entry<MainRenderer.DrawGroupKey, List<MainRenderer.PhysicsDrawCall>> entry : groupedDraws.entrySet()) {
         MainRenderer.DrawGroupKey key = entry.getKey();
         List<MainRenderer.PhysicsDrawCall> batch = entry.getValue();
         RenderPass renderPass = this.bindProperShader(() -> "Physics Mod Solid", this.getProperSolidRenderPipeline(key.backfaceCulling));
         renderPass.bindTexture("Sampler0", key.texture, NEAREST_CLAMP_SAMPLER_NO_MIPMAP);
         List<Draw<List<MainRenderer.PhysicsDrawCall>>> draws = new ObjectArrayList();

         for (MainRenderer.PhysicsDrawCall drawCall : batch) {
            if (drawCall.indexCount > 0) {
               draws.add(this.createIndexedDraw(renderPass, drawCall, cameraNormalMatrix));
            }
         }

         if (!draws.isEmpty()) {
            renderPass.drawMultipleIndexed(draws, null, null, List.of("DynamicTransforms", "Lighting", "PhysicsBrightness"), batch);
         }

         renderPass.close();
      }
   }

   private Draw<List<MainRenderer.PhysicsDrawCall>> createIndexedDraw(RenderPass renderPass, MainRenderer.PhysicsDrawCall drawCall, Matrix3f cameraNormalMatrix) {
      return new Draw(
         0,
         drawCall.vertexBuffer,
         drawCall.indexBuffer,
         IndexType.INT,
         drawCall.firstIndex,
         drawCall.indexCount,
         drawCall.baseVertex,
         (BiConsumer<List<MainRenderer.PhysicsDrawCall>, RenderPass.UniformUploader>) (sectionUbos, uploader) -> {
            boolean iris = StarterClient.iris() && Iris.isExtending();
            boolean optifine = StarterClient.optifabric && Optifine.isUsingShadersNoInternal();
            boolean shaderMod = iris || optifine;
            if (shaderMod) {
               if (drawCall.backfaceCulling) {
                  GlStateManager._enableCull();
               } else {
                  GlStateManager._disableCull();
               }

               int brightness = drawCall.brightness;
               if (this.lastBrightness != brightness) {
                  GL33C.glVertexAttribI2ui(Data.LIGHT.getAttribute(), brightness & 240, brightness >> 16 & 240);
               }

               this.lastBrightness = brightness;
            }

            if (iris) {
               if (drawCall.shade) {
                  Iris.setNormalMatrix(renderPass, drawCall.transform.modelView());
               } else {
                  Iris.setNormalMatrix(renderPass, drawCall.transform.modelView(), cameraNormalMatrix);
               }
            } else if (optifine) {
               Optifine.setDynamicTransforms(drawCall.transformBuffer);
               if (!drawCall.shade) {
                  Optifine.setNormalMatrix(cameraNormalMatrix);
               }
            }

            uploader.upload("DynamicTransforms", drawCall.transformBuffer);
            uploader.upload("Lighting", drawCall.lightBuffer);
            uploader.upload("PhysicsBrightness", drawCall.brightnessBuffer);
         }
      );
   }

   public LightUniform setupLighting(Matrix4f transformation, ClientLevel level, boolean shade) {
      if (shade) {
         Matrix3f lightMatrix = transformation.normal(this.normalMatrix).invert();
         if (level.dimensionType().cardinalLightType() == Type.NETHER) {
            lightMatrix.transform(NETHER_DIFFUSE_LIGHT_0, this.lightDirection0);
            lightMatrix.transform(NETHER_DIFFUSE_LIGHT_1, this.lightDirection1);
         } else {
            lightMatrix.transform(DIFFUSE_LIGHT_0, this.lightDirection0);
            lightMatrix.transform(DIFFUSE_LIGHT_1, this.lightDirection1);
         }

         return new LightUniform(new Vector3f(this.lightDirection0), new Vector3f(this.lightDirection1));
      } else {
         return level.dimensionType().cardinalLightType() == Type.NETHER ? NETHER_LIGHT : OVERWORLD_LIGHT;
      }
   }

   public static Transform createTransformUniform(Matrix4f transformation, float r, float g, float b, float a, @Nullable Matrix4f textureMatrix) {
      Transform transform = transformPool.obtain();
      ((Matrix4f)transform.modelView()).set(transformation);
      ((Vector4f)transform.colorModulator()).set(r, g, b, a);
      ((Matrix4f)transform.textureMatrix()).set(textureMatrix == null ? IDENTITY_4_BY_4 : textureMatrix);
      return transform;
   }

   public static Transform createTransformUniform(Matrix4f transformation) {
      return createTransformUniform(transformation, 1.0F, 1.0F, 1.0F, 1.0F, null);
   }

   public static BrightnessUniform createBrightnessUniform(int brightness) {
      return new BrightnessUniform(brightness & 240, brightness >> 16 & 240, 0, 10);
   }

   public static void freeTransform(Transform transform) {
      transformPool.free(transform);
   }

   public void renderDynamicCloth(ClientLevel level, Matrix4f viewMatrix, SubmitNodeStorage submitNodeStorage) {
      this.clothRenderer.renderDynamicCloth(level, viewMatrix, this.projectionMatrix, submitNodeStorage);
   }

   public void renderStaticCloth(ClientLevel level) {
      this.clothRenderer.renderStaticCloth(level, this.viewMatrix, this.projectionMatrix);
   }

   public void renderLiquid(ClientLevel level, ChunkSectionLayer chunkSectionLayer) {
      this.liquidRenderer.render(level, chunkSectionLayer, this.viewMatrix, this.projectionMatrix);
   }

   public void renderVolumetricSmoke(ClientLevel level) {
      this.smokeVolumeRenderer.render(level, this.viewMatrix, this.projectionMatrix);
   }

   public void renderVolumetricSmoke(ClientLevel level, Matrix4f projectionMatrix) {
      this.smokeVolumeRenderer.render(level, this.viewMatrix, projectionMatrix);
   }

   public void verifyAndUtility() {
      if (StarterClient.updateMessage != null && !StarterClient.updateMessage.isBlank() && Minecraft.getInstance().player != null) {
         JsonElement jsonElement = StrictJsonParser.parse(StarterClient.updateMessage);
         MutableComponent comp = (MutableComponent)ComponentSerialization.CODEC
            .parse(Minecraft.getInstance().player.registryAccess().createSerializationContext(JsonOps.INSTANCE), jsonElement)
            .resultOrPartial(string2 -> StarterClient.logger.warn("Failed to parse custom message '{}': {}", StarterClient.updateMessage, string2))
            .orElse(null);
         Minecraft.getInstance().gui.chatListener().handleSystemMessage(comp, false);
         StarterClient.updateMessage = "";
      }

      if (StarterClient.customMessage != null && !StarterClient.customMessage.isBlank() && Minecraft.getInstance().player != null) {
         JsonElement jsonElement = StrictJsonParser.parse(StarterClient.customMessage);
         MutableComponent comp = (MutableComponent)ComponentSerialization.CODEC
            .parse(Minecraft.getInstance().player.registryAccess().createSerializationContext(JsonOps.INSTANCE), jsonElement)
            .resultOrPartial(string2 -> StarterClient.logger.warn("Failed to parse custom message '{}': {}", StarterClient.customMessage, string2))
            .orElse(null);
         Minecraft.getInstance().gui.chatListener().handleSystemMessage(comp, false);
         StarterClient.customMessage = "";
      }
   }

   public void destroy() {
      this.snowRenderer.destroy();
      this.smokeRenderer.destroy();
      this.smokeVolumeRenderer.destroy();
      this.transparencyRenderer.destroy();
      this.oceanRenderer.destroy();
      this.liquidDeferredRenderer.destroy();
      this.levelProjectionMatrixBuffer.close();
   }

   private static final class DrawGroupKey {
      private final GpuTextureView texture;
      private final boolean backfaceCulling;

      private DrawGroupKey(GpuTextureView texture, boolean backfaceCulling) {
         this.texture = texture;
         this.backfaceCulling = backfaceCulling;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else {
            return !(o instanceof MainRenderer.DrawGroupKey other)
               ? false
               : this.backfaceCulling == other.backfaceCulling && Objects.equals(this.texture, other.texture);
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.texture, this.backfaceCulling);
      }
   }

   public static class PhysicsDrawCall extends BasicDrawCall {
      public Transform transform;
      public LightUniform light;
      public int brightness;
      public boolean backfaceCulling;
      public boolean shade;
      public GpuBuffer vertexBuffer;
      public GpuBuffer indexBuffer;
      public int firstIndex;
      public int indexCount;
      public int baseVertex;
      public GpuBufferSlice transformBuffer;
      public GpuBufferSlice lightBuffer;
      public GpuBufferSlice brightnessBuffer;

      public void reset() {
         this.texture = null;
         this.vertexBuffer = null;
         this.indexBuffer = null;
         this.transformBuffer = null;
         this.lightBuffer = null;
         this.brightnessBuffer = null;
         MainRenderer.freeTransform(this.transform);
      }
   }
}
