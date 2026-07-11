package net.diebuddies.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.List;
import net.diebuddies.compat.EMF;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {ModelPart.class},
   priority = 500
)
public class MixinModelPart {
   @Shadow
   @Final
   public List<Cube> cubes;

   @Inject(
      at = {@At("HEAD")},
      method = {"compile"},
      cancellable = true
   )
   private void physicsmod$renderCuboids(Pose matrices, VertexConsumer vertexConsumer, int light, int overlay, int color, CallbackInfo ci) {
      PhysicsMod mod = PhysicsMod.getCurrentInstance();
      if (mod != null && mod.blockify && (((ModelPart)(Object)this)).visible) {
         (((ModelPart)(Object)this)).translateAndRotate(mod.localPivotMatrix);
         if (!StarterClient.emf || !EMF.isEMFModel()) {
            PhysicsMod.createParticlesFromCuboids(
               matrices,
               mod.localPivotMatrix,
               this.cubes,
               mod.cubifyEntity,
               mod.cubifyEntityRenderer,
               mod.blockifyFeature,
               mod.cubifyTranslucent,
               overlay,
               (float)ARGB.red(color) / 255.0F,
               (float)ARGB.green(color) / 255.0F,
               (float)ARGB.blue(color) / 255.0F
            );
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"}
   )
   private void physicsmod$renderHead(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, CallbackInfo ci) {
      PhysicsMod mod = PhysicsMod.getCurrentInstance();
      if (mod != null && mod.blockify && (((ModelPart)(Object)this)).visible) {
         mod.localPivotMatrix.pushPose();
      }
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;III)V"}
   )
   private void physicsmod$renderTail(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, CallbackInfo ci) {
      PhysicsMod mod = PhysicsMod.getCurrentInstance();
      if (mod != null && mod.blockify && (((ModelPart)(Object)this)).visible) {
         mod.localPivotMatrix.popPose();
      }
   }
}
