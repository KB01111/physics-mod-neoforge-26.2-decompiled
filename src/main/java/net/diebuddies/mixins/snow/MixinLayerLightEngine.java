package net.diebuddies.mixins.snow;

import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LayerLightSectionStorage.class})
public class MixinLayerLightEngine {
   @Shadow
   @Final
   protected LightChunkGetter chunkSource;

   @Inject(
      at = {@At("HEAD")},
      method = {"setStoredLevel"}
   )
   private void physicsmod$getLightingChangesForEntities(long blockIndex, int light, CallbackInfo info) {
      if (this.chunkSource.getLevel() instanceof ClientLevel clientLevel) {
         PhysicsMod mod = PhysicsMod.getInstance(clientLevel);
         mod.updatedLightBlocks.add(blockIndex);
      }
   }
}
