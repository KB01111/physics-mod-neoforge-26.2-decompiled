package net.diebuddies.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.compat.Sodium;
import net.diebuddies.minecraft.ChunkSectionsToRenderExtension;
import net.diebuddies.minecraft.LevelRendererAccessor;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.render.MainRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher.PreparedFrame;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {LevelRenderer.class},
   priority = 1100
)
public class MixinLevelRenderer implements LevelRendererAccessor {
   @Shadow
   @Final
   private LevelTargetBundle targets;
   @Shadow
   @Final
   private LevelRenderState levelRenderState;
   @Unique
   private MainRenderer physicsmod$mainRenderer = new MainRenderer();

   @Inject(
      at = {@At("RETURN")},
      method = {"prepareChunkRenders"}
   )
   private void physicsmod$setRenderer(Matrix4fc modelViewMatrix, CallbackInfoReturnable<ChunkSectionsToRender> info) {
      ((ChunkSectionsToRenderExtension)(Object)info.getReturnValue()).physicsmod$setRenderer(this);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"render"}
   )
   private void physicsmod$grabViewMatrix(
      GraphicsResourceAllocator resourceAllocator,
      DeltaTracker deltaTracker,
      boolean renderOutline,
      CameraRenderState cameraState,
      Matrix4fc modelViewMatrix,
      GpuBufferSlice terrainFog,
      Vector4f fogColor,
      boolean shouldRenderSky,
      CallbackInfo info
   ) {
      this.physicsmod$mainRenderer.storeViewMatrix(modelViewMatrix);
      this.physicsmod$mainRenderer
         .setViewAndProjectionMatrix(this.physicsmod$mainRenderer.getStoredViewMatrix(), this.physicsmod$mainRenderer.getStoredProjectionMatrix());
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"submitEntities"}
   )
   private void physicsmod$renderEntities(PoseStack poseStack, LevelRenderState levelRenderState, SubmitNodeCollector submitNodeCollector, CallbackInfo info) {
      this.physicsmod$mainRenderer
         .renderDynamicCloth(
            Minecraft.getInstance().levelExtractor.level, MainRenderer.IDENTITY_4_BY_4, Minecraft.getInstance().levelRenderer.submitNodeStorage
         );
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"addMainPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/util/profiling/ProfilerFiller;Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;Lorg/joml/Matrix4fc;)V"}
   )
   private void physicsmod$setMatrices(
      FrameGraphBuilder frame,
      PreparedFrame featureFrame,
      GpuBufferSlice terrainFog,
      LevelRenderState levelRenderState,
      ProfilerFiller profiler,
      ChunkSectionsToRender chunkSectionsToRender,
      Matrix4fc modelViewMatrix,
      CallbackInfo info
   ) {
      this.physicsmod$mainRenderer
         .setViewAndProjectionMatrix(this.physicsmod$mainRenderer.getStoredViewMatrix(), this.physicsmod$mainRenderer.getStoredProjectionMatrix());
      if (Minecraft.getInstance().levelExtractor.level != null) {
         PhysicsMod mod = PhysicsMod.getInstance(Minecraft.getInstance().levelExtractor.level);
         mod.getPhysicsWorld().getSnowWorld().viewProjection.set(this.physicsmod$mainRenderer.getViewProjectionMatrix());
         if (StarterClient.sodium) {
            Sodium.setRenderer((LevelRenderer)(Object)this);
         }
      }
   }

   @Shadow
   private PostChain getTransparencyChain() {
      return null;
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"addWeatherPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Matrix4fc;)V"}
   )
   private void physicsmod$renderVolumetricSmokeNoOIT(
      FrameGraphBuilder frameGraphBuilder, GpuBufferSlice gpuBufferSlice, Matrix4fc modelViewMatrix, CallbackInfo info
   ) {
      if (this.getTransparencyChain() == null
         && (!StarterClient.iris() || !Iris.isExtending())
         && (!StarterClient.optifabric || !Optifine.isUsingShadersNoInternal())) {
         FramePass framePass = frameGraphBuilder.addPass("Physics Volumetric Smoke");
         this.targets.main = framePass.readsAndWrites(this.targets.main);
         framePass.executes(
            () -> this.physicsmod$mainRenderer
                  .renderVolumetricSmoke(Minecraft.getInstance().levelExtractor.level, this.levelRenderState.cameraRenderState.projectionMatrix)
         );
      }
   }

   @Inject(
      method = {"render"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/PostChain;addToFrame(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;IILnet/minecraft/client/renderer/PostChain$TargetBundle;)V",
         ordinal = 1,
         shift = Shift.AFTER
      )}
   )
   private void physicsmod$renderVolumetricSmokeOIT(
      GraphicsResourceAllocator resourceAllocator,
      DeltaTracker deltaTracker,
      boolean renderOutline,
      CameraRenderState cameraState,
      Matrix4fc modelViewMatrix,
      GpuBufferSlice terrainFog,
      Vector4f fogColor,
      boolean shouldRenderSky,
      CallbackInfo info,
      @Local FrameGraphBuilder frameGraphBuilder
   ) {
      if ((!StarterClient.iris() || !Iris.isExtending()) && (!StarterClient.optifabric || !Optifine.isUsingShadersNoInternal())) {
         FramePass framePass = frameGraphBuilder.addPass("Physics Volumetric Smoke OIT");
         this.targets.main = framePass.readsAndWrites(this.targets.main);
         framePass.executes(
            () -> this.physicsmod$mainRenderer
                  .renderVolumetricSmoke(Minecraft.getInstance().levelExtractor.level, this.levelRenderState.cameraRenderState.projectionMatrix)
         );
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"close"}
   )
   private void physicsmod$close(CallbackInfo info) {
      this.physicsmod$mainRenderer.destroy();
   }

   @Override
   public MainRenderer physicsmod$getMainRenderer() {
      return this.physicsmod$mainRenderer;
   }

   @Override
   public ClientLevel physicsmod$getLevel() {
      return Minecraft.getInstance().levelExtractor.level;
   }
}
