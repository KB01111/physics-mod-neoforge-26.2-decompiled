package net.diebuddies.mixins.fabricapi;

import java.util.function.BooleanSupplier;
import net.diebuddies.bridge.FabricAPIServer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerLevel.class})
public class MixinServerWorld {
   @Inject(
      at = {@At("HEAD")},
      method = {"tick"}
   )
   private void startWorldTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
      FabricAPIServer.START_WORLD_TICK.invoker().onStartTick((ServerLevel)(Object)this);
   }
}
