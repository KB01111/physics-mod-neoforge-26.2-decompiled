package net.diebuddies.mixins.vines;

import java.util.List;
import java.util.Set;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.diebuddies.physics.vines.RenderSectionExtension;
import net.diebuddies.physics.vines.RenderSectionPhysicsData;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher.RenderSection;
import net.minecraft.core.SectionPos;
import org.joml.Vector3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RenderSection.class})
public class MixinRenderSection implements RenderSectionExtension {
   @Unique
   private RenderSectionPhysicsData physicsmod$data;
   @Shadow
   private volatile long sectionNode;

   @Override
   public void setUnloadDynamicBlocks(PhysicsWorld world, Set<Vector3i> affectedChunks, List<DynamicRagdoll> unloadedRagdolls) {
      this.physicsmod$data = new RenderSectionPhysicsData(world, affectedChunks, unloadedRagdolls);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"reset"},
      cancellable = true
   )
   private void physicsmod$unloadChunkMesh(CallbackInfo info) {
      this.chunkUpdated();
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"setSectionMesh"},
      cancellable = true
   )
   private void physicsmod$loadChunkMesh(SectionMesh sectionMesh, CallbackInfoReturnable<SectionMesh> info) {
      this.chunkUpdated();
   }

   @Override
   public void chunkUpdated() {
      if (this.physicsmod$data != null) {
         this.physicsmod$data.remove(new Vector3i(SectionPos.x(this.sectionNode), SectionPos.y(this.sectionNode), SectionPos.z(this.sectionNode)));
         this.physicsmod$data = null;
      }
   }
}
