package net.diebuddies.mixins.cloth;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.physics.verlet.SimulationLeashExtension;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({Entity.class})
public abstract class MixinEntity implements SimulationLeashExtension {
   @Unique
   private List<VerletSimulation> leashSimulations = new ObjectArrayList();

   @Override
   public List<VerletSimulation> physicsmod$getSimulations() {
      return this.leashSimulations;
   }
}
