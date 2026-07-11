package net.diebuddies.mixins.vines;

import java.util.List;
import java.util.Set;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.diebuddies.physics.vines.RenderSectionExtension;
import net.diebuddies.physics.vines.RenderSectionPhysicsData;
import org.joml.Vector3i;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin({RenderSection.class})
public class MixinRenderSectionSodium implements RenderSectionExtension {
   @Shadow
   @Final
   private int chunkX;
   @Shadow
   @Final
   private int chunkY;
   @Shadow
   @Final
   private int chunkZ;
   @Unique
   private RenderSectionPhysicsData physicsmod$data;

   @Override
   public void setUnloadDynamicBlocks(PhysicsWorld world, Set<Vector3i> affectedChunks, List<DynamicRagdoll> unloadedRagdolls) {
      this.physicsmod$data = new RenderSectionPhysicsData(world, affectedChunks, unloadedRagdolls);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"delete"},
      cancellable = true
   )
   private void physicsmod$unloadChunkMesh(CallbackInfo info) {
      this.chunkUpdated();
   }

   @Override
   public void chunkUpdated() {
      if (this.physicsmod$data != null) {
         this.physicsmod$data.remove(new Vector3i(this.chunkX, this.chunkY, this.chunkZ));
         this.physicsmod$data = null;
      }
   }
}
