package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import net.diebuddies.physics.PlayerRenderStateExtended;
import net.diebuddies.physics.verlet.ClothRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HumanoidArmorLayer.class})
public class MixinHumanoidArmorLayer {
   @Inject(
      at = {@At("HEAD")},
      method = {"renderArmorPiece"},
      cancellable = true
   )
   private void physicsmod$skipArmorPiece(
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      ItemStack itemStack,
      EquipmentSlot equipmentSlot,
      int light,
      HumanoidRenderState humanoidRenderState,
      CallbackInfo info
   ) {
      if (humanoidRenderState instanceof PlayerRenderStateExtended extended && extended.physicsmod$getClothRenderState() != null) {
         ClothRenderState cloth = extended.physicsmod$getClothRenderState();
         switch (equipmentSlot) {
            case HEAD:
               if (cloth.skipHeadEquipment) {
                  info.cancel();
               }

               return;
            case CHEST:
               if (cloth.skipChestEquipment) {
                  info.cancel();
               }

               return;
            case LEGS:
               if (cloth.skipLegsEquipment) {
                  info.cancel();
               }

               return;
            case FEET:
               if (cloth.skipFeetEquipment) {
                  info.cancel();
               }

               return;
         }
      }
   }
}
