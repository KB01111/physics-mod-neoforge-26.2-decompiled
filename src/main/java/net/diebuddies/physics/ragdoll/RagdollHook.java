package net.diebuddies.physics.ragdoll;

import java.util.List;
import net.diebuddies.physics.PhysicsEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;

public interface RagdollHook {
   void map(Ragdoll var1, Entity var2, EntityModel var3, EntityRenderState var4);

   void filterCuboidsFromEntities(List<PhysicsEntity> var1, Entity var2, EntityModel var3);
}
