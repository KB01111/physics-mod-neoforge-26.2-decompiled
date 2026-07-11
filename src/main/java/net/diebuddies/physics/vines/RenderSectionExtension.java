package net.diebuddies.physics.vines;

import java.util.List;
import java.util.Set;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import org.joml.Vector3i;

public interface RenderSectionExtension {
   void setUnloadDynamicBlocks(PhysicsWorld var1, Set<Vector3i> var2, List<DynamicRagdoll> var3);

   void chunkUpdated();
}
