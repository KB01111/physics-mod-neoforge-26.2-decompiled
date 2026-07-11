package net.diebuddies.physics.ocean;

public interface EntityOcean {
   double getPhysicsYOffset();

   double getPhysicsOldYOffset();

   double getPhysicsYOffset(float var1);

   double getPhysicsPitch();

   double getPhysicsOldPitch();

   double getPhysicsPitch(float var1);

   double getPhysicsRoll();

   double getPhysicsOldRoll();

   double getPhysicsRoll(float var1);

   boolean isInPhysicsAir();
}
