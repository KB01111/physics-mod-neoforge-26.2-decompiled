package net.diebuddies.physics.verlet;

import java.util.Map;

public interface SimulationPlayerExtension {
   VerletSimulation physicsmod$getElytraSimulation();

   void physicsmod$setElytraSimulation(VerletSimulation var1);

   VerletSimulation physicsmod$getCapeSimulation();

   void physicsmod$setCapeSimulation(VerletSimulation var1);

   VerletSimulation physicsmod$getOtherSimulation(String var1);

   Map<String, VerletSimulation> physicsmod$getOtherSimulations();

   void physicsmod$removeCapeSimulation(String var1);

   void physicsmod$putCapeSimulation(String var1, VerletSimulation var2);
}
