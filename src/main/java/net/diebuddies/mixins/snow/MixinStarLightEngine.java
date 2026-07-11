package net.diebuddies.mixins.snow;

import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
   targets = {"ca.spottedleaf.starlight.common.light.StarLightEngine"}
)
public class MixinStarLightEngine {
   @Shadow
   @Final
   private Level world;
   @Shadow
   @Final
   private boolean isClientSide;

   @Inject(
      at = {@At("HEAD")},
      method = {"setLightLevel(IIIIII)V"}
   )
   protected final void setLightLevel(int sectionIndex, int localIndex, int worldX, int worldY, int worldZ, int level, CallbackInfo info) {
      this.causeSnowLightUpdate(worldX, worldY, worldZ);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"setLightLevel(IIII)V"}
   )
   protected final void setLightLevel(int worldX, int worldY, int worldZ, int level, CallbackInfo info) {
      this.causeSnowLightUpdate(worldX, worldY, worldZ);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"postLightUpdate(III)V"}
   )
   protected final void postLightUpdate(int worldX, int worldY, int worldZ, CallbackInfo info) {
      this.causeSnowLightUpdate(worldX, worldY, worldZ);
   }

   @Unique
   private void causeSnowLightUpdate(int worldX, int worldY, int worldZ) {
      if (this.isClientSide && this.world instanceof ClientLevel clientLevel) {
         PhysicsMod mod = PhysicsMod.getInstance(clientLevel);
         mod.updatedLightBlocks.add(BlockPos.asLong(worldX, worldY, worldZ));
      }
   }
}
