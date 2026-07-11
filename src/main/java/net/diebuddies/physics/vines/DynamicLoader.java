package net.diebuddies.physics.vines;

import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.minecraft.core.BlockPos;

public interface DynamicLoader {
   void unloadAllRagdolls();

   void loadAllRagdolls();

   void unloadAllSnow();

   void loadAllSnow();

   void unloadAllOcean();

   void loadAllOcean();

   void unloadAllFire();

   void loadAllFire();

   void addVineRagdoll(DynamicRagdoll var1, BlockPos var2);

   void removeVineRagdoll(DynamicRagdoll var1);

   void chunkPosChanged();

   void setPhysicsMod(PhysicsMod var1);
}
