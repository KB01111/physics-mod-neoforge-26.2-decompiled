package net.diebuddies.mixins.ocean;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.WaterDropParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos.MutableBlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({WaterDropParticle.class})
public class MixinWaterDropParticle extends MixinParticle {
   @Unique
   private double physicsOffset;
   @Unique
   private MutableBlockPos mutable;

   @Inject(
      at = {@At("TAIL")},
      method = {"<init>"}
   )
   protected void constructor(ClientLevel clientLevel, double x, double y, double z, TextureAtlasSprite textureAtlasSprite, CallbackInfo info) {
   }

   @Override
   protected void getLightCoords(float renderPercent, CallbackInfoReturnable<Integer> info) {
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"tick"},
      cancellable = true
   )
   public void tick(CallbackInfo info) {
   }
}
