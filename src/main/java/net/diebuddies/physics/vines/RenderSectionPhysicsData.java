package net.diebuddies.physics.vines;

import java.util.List;
import java.util.Set;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import org.joml.Vector3i;

public class RenderSectionPhysicsData {
   public PhysicsWorld world;
   public Set<Vector3i> affectedChunks;
   public List<DynamicRagdoll> unloadedRagdolls;

   public RenderSectionPhysicsData(PhysicsWorld world, Set<Vector3i> affectedChunks, List<DynamicRagdoll> unloadedRagdolls) {
      this.world = world;
      this.affectedChunks = affectedChunks;
      this.unloadedRagdolls = unloadedRagdolls;
   }

   public void remove(Vector3i chunk) {
      this.affectedChunks.remove(chunk);
      if (this.affectedChunks.isEmpty()) {
         for (DynamicRagdoll ragdoll : this.unloadedRagdolls) {
            this.world.removeRagdoll(ragdoll);
         }
      }
   }
}
