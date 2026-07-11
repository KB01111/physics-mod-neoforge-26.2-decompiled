package net.diebuddies.mixins;

import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Main.class})
public class MixinMain {
   @Inject(
      at = {@At("HEAD")},
      method = {"main"}
   )
   private static void main(String[] args, CallbackInfo info) {
      System.setProperty("joml.fastmath", "true");
      System.setProperty("joml.sinLookup", "true");
      System.setProperty("iris.prettyPrintShaders", "true");
   }
}
