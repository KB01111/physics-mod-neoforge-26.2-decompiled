package net.diebuddies.mixins.optifine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ModelPart.class})
public class MixinModelPart {
   @Inject(
      at = {@At("HEAD")},
      method = {"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIIZ)V"}
   )
   public void physicsmod$optifineHead(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, boolean updateModel, CallbackInfo ci) {
      PhysicsMod mod = PhysicsMod.getCurrentInstance();
      if (mod != null && mod.blockify && (((ModelPart)(Object)this)).visible) {
         mod.localPivotMatrix.pushPose();
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIIZ)V"}
   )
   public void physicsmod$optifineTail(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, boolean updateModel, CallbackInfo ci) {
      PhysicsMod mod = PhysicsMod.getCurrentInstance();
      if (mod != null && mod.blockify && (((ModelPart)(Object)this)).visible) {
         mod.localPivotMatrix.popPose();
      }
   }
}
