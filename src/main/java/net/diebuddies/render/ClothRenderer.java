package net.diebuddies.render;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderPass.Draw;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.Objects;
import java.util.Map.Entry;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.LevelRendererAccessor;
import net.diebuddies.model.ClothMesh;
import net.diebuddies.opengl.Data;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.verlet.ClothRenderCommand;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.constraints.ModelPartConstraint;
import net.diebuddies.render.util.BrightnessUniform;
import net.diebuddies.render.util.DynamicUniformsExtension;
import net.diebuddies.render.util.LightUniform;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.DynamicUniforms.Transform;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL33C;

public class ClothRenderer {
   private final MainRenderer mainRenderer;
   private final List<ClothRenderer.ClothDrawCall> drawCalls = new ObjectArrayList();
   private double lastRenderPercent;
   private final PoseStack tmpStack = new PoseStack();
   private int lastBrightness = -1;

   public ClothRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
   }

   public void renderDynamicCloth(ClientLevel level, Matrix4f viewMatrix, Matrix4f projectionMatrix, SubmitNodeStorage submitNodeStorage) {
      if (level != null) {
         List<VerletSimulation> dynamicCloth = PhysicsMod.dynamicCloth.get(PhysicsMod.getRenderPass());
         if (dynamicCloth != null && !dynamicCloth.isEmpty()) {
            for (int i = 0; i < dynamicCloth.size(); i++) {
               dynamicCloth.get(i).renderSlow(level, viewMatrix, projectionMatrix, submitNodeStorage);
            }

            dynamicCloth.clear();
         }
      }
   }

   public void renderStaticCloth(ClientLevel level, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
      if (level != null) {
         List<ClothRenderCommand> staticCloth = PhysicsMod.clothRenderFast.get(PhysicsMod.getRenderPass());
         if (staticCloth != null && !staticCloth.isEmpty()) {
            PhysicsMod.storeShaderLightDirections();
            PerformanceTracker.startNoFlush("cloth_rendering");
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(
               ((LevelRendererAccessor)Minecraft.getInstance().levelRenderer)
                  .physicsmod$getMainRenderer()
                  .levelProjectionMatrixBuffer
                  .getBuffer(projectionMatrix),
               ProjectionType.PERSPECTIVE
            );
            Minecraft.getInstance().gameRenderer.lighting().setupFor(com.mojang.blaze3d.platform.Lighting.Entry.LEVEL);

            try {
               for (int i = 0; i < staticCloth.size(); i++) {
                  this.queueDrawCall(level, staticCloth.get(i), viewMatrix);
               }

               this.uploadDrawCallUniforms();
               RenderPass renderPass = this.mainRenderer
                  .bindProperShader(() -> "Physics Mod Static Cloth", this.mainRenderer.getProperSolidRenderPipeline(false));
               this.executeDrawCalls(renderPass);
               renderPass.close();
            } finally {
               this.clearDrawCalls();
               PhysicsMod.restoreShaderLightDirections();
               RenderSystem.restoreProjectionMatrix();
               PerformanceTracker.end("cloth_rendering");
               staticCloth.clear();
            }
         }
      }
   }

   private void queueDrawCall(ClientLevel level, ClothRenderCommand renderCommand, Matrix4f viewMatrix) {
      Vec3 view = Minecraft.getInstance().gameRenderer.mainCamera().position();
      double renderPercent = (double)Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
      if (Minecraft.getInstance().isPaused()) {
         renderPercent = this.lastRenderPercent;
      } else {
         this.lastRenderPercent = renderPercent;
      }

      Matrix4fStack matrixStack = RenderSystem.getModelViewStack();
      matrixStack.pushMatrix();

      try {
         LivingEntity entity = renderCommand.entity;
         double px = Mth.lerp(renderPercent, entity.xOld, entity.getX());
         double py = Mth.lerp(renderPercent, entity.yOld, entity.getY());
         double pz = Mth.lerp(renderPercent, entity.zOld, entity.getZ());
         matrixStack.translation((float)(-view.x + px), (float)(-view.y + py), (float)(-view.z + pz));
         renderCommand.modelPart.loadPose(renderCommand.modelPose);
         this.tmpStack.last().pose().set(matrixStack);
         ModelPartConstraint.entityTransformation(this.tmpStack, entity, (float)renderPercent);
         ModelPartConstraint.modelPartTransformation(renderCommand.modelPart, this.tmpStack);
         Matrix4f modelTransform = new Matrix4f(this.tmpStack.last().pose());
         LightUniform light = this.mainRenderer.setupLighting(modelTransform, level, true);
         Matrix4f renderTransform = new Matrix4f();
         viewMatrix.mul(modelTransform, renderTransform);
         if (!renderCommand.onlyRenderPlayer) {
            ClothMesh clothMesh = renderCommand.cloth.getRenderMesh(!ConfigClient.clothSmoothShading);
            this.addDrawCall(renderTransform, light, renderCommand.brightness, clothMesh, renderCommand.textureID, renderCommand.cloth.getSampler());
         }

         if (renderCommand.cloth.playerMesh != null && entity instanceof AbstractClientPlayer player) {
            ClothMesh playerMesh = renderCommand.cloth.getPlayerRenderMesh(renderCommand.brightness);
            if (playerMesh != null) {
               GpuTextureView playerTexture = Minecraft.getInstance().getTextureManager().getTexture(player.getSkin().body().texturePath()).getTextureView();
               this.addDrawCall(renderTransform, light, renderCommand.brightness, playerMesh, playerTexture, renderCommand.cloth.getSampler());
            }
         }
      } finally {
         matrixStack.popMatrix();
      }
   }

   private void addDrawCall(Matrix4f renderTransform, LightUniform light, int brightness, ClothMesh mesh, @Nullable GpuTextureView texture, GpuSampler sampler) {
      if (mesh != null && texture != null) {
         ClothRenderer.ClothDrawCall drawCall = new ClothRenderer.ClothDrawCall();
         drawCall.light = light;
         drawCall.brightness = brightness;
         drawCall.mesh = mesh;
         drawCall.texture = texture;
         drawCall.sampler = sampler;
         drawCall.transform = MainRenderer.createTransformUniform(new Matrix4f(renderTransform));
         this.drawCalls.add(drawCall);
      }
   }

   private void uploadDrawCallUniforms() {
      if (!this.drawCalls.isEmpty()) {
         DynamicUniformsExtension dynamicUniforms = (DynamicUniformsExtension)RenderSystem.getDynamicUniforms();
         Transform[] transforms = new Transform[this.drawCalls.size()];
         LightUniform[] lights = new LightUniform[this.drawCalls.size()];
         BrightnessUniform[] brightnessUniforms = new BrightnessUniform[this.drawCalls.size()];

         for (int i = 0; i < this.drawCalls.size(); i++) {
            ClothRenderer.ClothDrawCall drawCall = this.drawCalls.get(i);
            transforms[i] = drawCall.transform;
            lights[i] = drawCall.light;
            brightnessUniforms[i] = MainRenderer.createBrightnessUniform(drawCall.brightness);
         }

         GpuBufferSlice[] transformSlices = dynamicUniforms.physicsmod$getDynamicUniformStorage().writeUniforms(transforms);
         GpuBufferSlice[] lightSlices = dynamicUniforms.physicsmod$writeLightUniforms(lights);
         GpuBufferSlice[] brightnessSlices = dynamicUniforms.physicsmod$writeBrightnessUniforms(brightnessUniforms);

         for (int i = 0; i < this.drawCalls.size(); i++) {
            ClothRenderer.ClothDrawCall drawCall = this.drawCalls.get(i);
            drawCall.transformBuffer = transformSlices[i];
            drawCall.lightBuffer = lightSlices[i];
            drawCall.brightnessBuffer = brightnessSlices[i];
         }
      }
   }

   private void executeDrawCalls(RenderPass renderPass) {
      if (!this.drawCalls.isEmpty()) {
         this.lastBrightness = -1;
         Map<ClothRenderer.TextureKey, List<ClothRenderer.ClothDrawCall>> groupedDraws = new LinkedHashMap<>();

         for (ClothRenderer.ClothDrawCall drawCall : this.drawCalls) {
            ClothRenderer.TextureKey key = new ClothRenderer.TextureKey(drawCall.texture, drawCall.sampler);
            groupedDraws.computeIfAbsent(key, ignored -> new ObjectArrayList()).add(drawCall);
         }

         for (Entry<ClothRenderer.TextureKey, List<ClothRenderer.ClothDrawCall>> entry : groupedDraws.entrySet()) {
            ClothRenderer.TextureKey textureKey = entry.getKey();
            List<ClothRenderer.ClothDrawCall> batch = entry.getValue();
            renderPass.bindTexture("Sampler0", textureKey.texture, textureKey.sampler);
            List<Draw<Collection<ClothRenderer.ClothDrawCall>>> draws = new ObjectArrayList();

            for (ClothRenderer.ClothDrawCall drawCall : batch) {
               ClothMesh mesh = drawCall.mesh;
               draws.add(new Draw(0, mesh.vertexBuffer(), mesh.indexBuffer(), mesh.indexType(), 0, mesh.indexCount(), 0, (BiConsumer<Collection<ClothRenderer.ClothDrawCall>, RenderPass.UniformUploader>) (context, uploader) -> {
                  boolean iris = StarterClient.iris() && Iris.isExtending();
                  boolean optifine = StarterClient.optifabric && Optifine.isUsingShadersNoInternal();
                  boolean shaderMod = iris || optifine;
                  if (shaderMod) {
                     int brightness = drawCall.brightness;
                     if (this.lastBrightness != brightness) {
                        GL33C.glVertexAttribI2ui(Data.LIGHT.getAttribute(), brightness & 240, brightness >> 16 & 240);
                     }

                     this.lastBrightness = brightness;
                  }

                  if (iris) {
                     Iris.setNormalMatrix(renderPass, drawCall.transform.modelView());
                  } else if (optifine) {
                     Optifine.setDynamicTransforms(drawCall.transformBuffer);
                  }

                  uploader.upload("DynamicTransforms", drawCall.transformBuffer);
                  uploader.upload("Lighting", drawCall.lightBuffer);
                  uploader.upload("PhysicsBrightness", drawCall.brightnessBuffer);
               }));
            }

            if (!draws.isEmpty()) {
               renderPass.drawMultipleIndexed(draws, null, null, List.of("DynamicTransforms", "Lighting", "PhysicsBrightness"), batch);
            }
         }
      }
   }

   private void clearDrawCalls() {
      for (ClothRenderer.ClothDrawCall drawCall : this.drawCalls) {
         MainRenderer.freeTransform(drawCall.transform);
         drawCall.transform = null;
         drawCall.transformBuffer = null;
         drawCall.light = null;
         drawCall.lightBuffer = null;
         drawCall.brightnessBuffer = null;
         drawCall.mesh = null;
         drawCall.texture = null;
         drawCall.sampler = null;
      }

      this.drawCalls.clear();
   }

   private static final class ClothDrawCall extends BasicDrawCall {
      public Transform transform;
      public GpuBufferSlice transformBuffer;
      public LightUniform light;
      public GpuBufferSlice lightBuffer;
      public GpuBufferSlice brightnessBuffer;
      public ClothMesh mesh;
      public GpuSampler sampler;
      public int brightness;
   }

   private static final class TextureKey {
      private final GpuTextureView texture;
      private final GpuSampler sampler;

      private TextureKey(GpuTextureView texture, GpuSampler sampler) {
         this.texture = texture;
         this.sampler = sampler;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else {
            return !(o instanceof ClothRenderer.TextureKey other)
               ? false
               : Objects.equals(this.texture, other.texture) && Objects.equals(this.sampler, other.sampler);
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.texture, this.sampler);
      }
   }
}
