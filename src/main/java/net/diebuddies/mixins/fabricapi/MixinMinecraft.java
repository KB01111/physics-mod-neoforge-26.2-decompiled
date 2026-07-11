package net.diebuddies.mixins.fabricapi;

import net.diebuddies.bridge.FabricAPI;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public class MixinMinecraft {
   @Inject(
      at = {@At("HEAD")},
      method = {"exitWorldAndClose"}
   )
   private void onStopping(CallbackInfo ci) {
      FabricAPI.CLIENT_STOPPING.invoker().onClientStopping((Minecraft)(Object)this);
   }
}
