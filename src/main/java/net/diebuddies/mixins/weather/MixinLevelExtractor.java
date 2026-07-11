package net.diebuddies.mixins.weather;

import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.minecraft.weather.WindSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelExtractor.class})
public class MixinLevelExtractor {
   @Unique
   private WindSoundInstance windSound;

   @Inject(
      at = {@At("HEAD")},
      method = {"setLevel"}
   )
   private void physicsmod$stopWind(@Nullable ClientLevel clientLevel, CallbackInfo info) {
      if (this.windSound != null) {
         this.windSound.stopWind();
         this.windSound = null;
      }

      if (clientLevel != null) {
         Minecraft.getInstance()
            .getSoundManager()
            .queueTickingSound(this.windSound = new WindSoundInstance(clientLevel, WeatherEffects.WIND_SOUND_EVENT, SoundSource.WEATHER));
      }
   }
}
