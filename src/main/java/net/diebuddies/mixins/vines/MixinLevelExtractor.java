package net.diebuddies.mixins.vines;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.extract.LevelExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelExtractor.class})
public class MixinLevelExtractor {
   @Shadow
   private ClientLevel level;

   @Inject(
      at = {@At("TAIL")},
      method = {"allChanged"}
   )
   public void allChanged(CallbackInfo info) {
   }
}
