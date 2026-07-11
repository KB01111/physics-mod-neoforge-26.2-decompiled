package net.diebuddies.mixins.emf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import java.util.List;
import java.util.Map;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import traben.entity_model_features.models.parts.EMFModelPart;

@Pseudo
@Mixin({EMFModelPart.class})
public class MixinEMFModelPart extends ModelPart {
   public MixinEMFModelPart(List<Cube> list, Map<String, ModelPart> map) {
      super(list, map);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"compile"},
      cancellable = true
   )
   private void physicsmod$renderCuboids(Pose matrices, VertexConsumer vertexConsumer, int light, int overlay, int color, CallbackInfo ci) {
      PhysicsMod mod = PhysicsMod.getCurrentInstance();
      if (mod != null && mod.blockify && this.visible) {
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

   @Inject(
      at = {@At("HEAD")},
      method = {"renderLikeVanilla"}
   )
   private void physicsmod$renderHead(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, CallbackInfo ci) {
      PhysicsMod mod = PhysicsMod.getCurrentInstance();
      if (mod != null && mod.blockify && this.visible) {
         mod.localPivotMatrix.pushPose();
         this.translateAndRotate(mod.localPivotMatrix);
      }
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"renderLikeVanilla"}
   )
   private void physicsmod$renderTail(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color, CallbackInfo ci) {
      PhysicsMod mod = PhysicsMod.getCurrentInstance();
      if (mod != null && mod.blockify && this.visible) {
         mod.localPivotMatrix.popPose();
      }
   }
}
