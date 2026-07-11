package net.diebuddies.mixins.cloth;

import net.diebuddies.physics.verlet.SimulationExtension;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({BannerBlockEntity.class})
public abstract class MixinBannerBlockEntity implements SimulationExtension {
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
