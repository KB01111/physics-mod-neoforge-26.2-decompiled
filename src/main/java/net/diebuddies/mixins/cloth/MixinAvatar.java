package net.diebuddies.mixins.cloth;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import net.diebuddies.physics.verlet.SimulationPlayerExtension;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({Avatar.class})
public abstract class MixinAvatar implements SimulationPlayerExtension {
   @Unique
   private VerletSimulation elytraSimulation;
   @Unique
   private VerletSimulation capeSimulation;
   @Unique
   private Map<String, VerletSimulation> otherSimulations = new Object2ObjectOpenHashMap();

   @Override
   public VerletSimulation physicsmod$getElytraSimulation() {
      return this.elytraSimulation;
   }

   @Override
   public void physicsmod$setElytraSimulation(VerletSimulation simulation) {
      this.elytraSimulation = simulation;
   }

   @Override
   public VerletSimulation physicsmod$getCapeSimulation() {
      return this.capeSimulation;
   }

   @Override
   public void physicsmod$setCapeSimulation(VerletSimulation simulation) {
      this.capeSimulation = simulation;
   }

   @Override
   public VerletSimulation physicsmod$getOtherSimulation(String key) {
      return this.otherSimulations.get(key);
   }

   @Override
   public Map<String, VerletSimulation> physicsmod$getOtherSimulations() {
      return this.otherSimulations;
   }

   @Override
   public void physicsmod$putCapeSimulation(String key, VerletSimulation simulation) {
      this.otherSimulations.put(key, simulation);
   }

   @Override
   public void physicsmod$removeCapeSimulation(String key) {
      this.otherSimulations.remove(key);
   }
}
