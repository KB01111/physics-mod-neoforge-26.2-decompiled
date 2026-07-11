package net.diebuddies.mixins.cloth;

import net.diebuddies.physics.verlet.SimulationExtension;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({FishingHook.class})
public abstract class MixinFishingHook implements SimulationExtension {
   @Unique
   private VerletSimulation simulation;

   @Override
   public VerletSimulation physicsmod$getSimulation() {
      return this.simulation;
   }

   @Override
   public void physicsmod$setSimulation(VerletSimulation simulation) {
      this.simulation = simulation;
   }
}
