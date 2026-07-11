package net.diebuddies.mixins.cloth;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public class MixinMinecraft {
   @Shadow
   private ClientLevel level;
   @Unique
   public boolean physicsmod$renderedFrame = true;

   @Inject(
      at = {@At("TAIL")},
      method = {"runTick"}
   )
   public void physicsmod$renderedFrame(boolean value, CallbackInfo info) {
      this.physicsmod$renderedFrame = true;
   }

   @Inject(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/multiplayer/ClientLevel;tickEntities()V",
         shift = Shift.AFTER
      )}
   )
   private void physicsmod$updateClothSimulations(CallbackInfo info) {
      double delta = 0.05 * (double)ConfigClient.playbackSpeed;
      PerformanceTracker.start("physics_tick_cloth");
      ObjectIterator var4 = PhysicsMod.getInstances().values().iterator();

      while (var4.hasNext()) {
         PhysicsMod mod = (PhysicsMod)var4.next();
         List<VerletSimulation> simulations = mod.getPhysicsWorld().getVerletSimulations();

         for (int i = 0; i < simulations.size(); i++) {
            VerletSimulation simulation = simulations.get(i);
            if ((!simulation.active || simulation.destroyed) && this.physicsmod$renderedFrame) {
               simulation.destroyed = true;
               simulations.remove(i--);
            } else {
               simulation.update(mod.getPhysicsWorld(), delta);
            }
         }
      }

      PerformanceTracker.end("physics_tick_cloth");
      this.physicsmod$renderedFrame = false;
   }
}
