package net.diebuddies.mixins.flashback;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.diebuddies.physics.PhysicsMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   targets = {"com.moulberry.flashback.playback.ReplayServer"}
)
public class MixinReplayServer {
   @Shadow
   private int lastReplayTick;
   @Shadow
   private int targetTick;

   @Inject(
      at = {@At("RETURN")},
      method = {"goToReplayTick"}
   )
   private void physicsmod$resetPhysics(int tick, CallbackInfo info) {
      if (tick < this.targetTick) {
         ObjectIterator var3 = PhysicsMod.getInstances().values().iterator();

         while (var3.hasNext()) {
            PhysicsMod mod = (PhysicsMod)var3.next();
            mod.getPhysicsWorld().destroy();
         }

         PhysicsMod.getInstances().clear();
      }
   }
}
