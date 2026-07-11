package net.diebuddies.physics.vines;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.diebuddies.minecraft.ChunkHelper;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.diebuddies.physics.ragdoll.VineRagdoll;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class VineSetting extends DynamicSetting {
   @Adjustable(
      id = "Can link",
      translationId = "physicsmod.prop.vine.link"
   )
   public Block link;
   @Adjustable(
      id = "Fixed on bottom",
      translationId = "physicsmod.prop.vine.bottomfixed"
   )
   public boolean bottomFixed;
   @Adjustable(
      id = "No gravity",
      translationId = "physicsmod.prop.vine.waterphysics"
   )
   public boolean waterPhysics;
   @Adjustable(
      id = "Side connection",
      translationId = "physicsmod.prop.vine.sideconnection"
   )
   public boolean sideConnection;
   @Adjustable(
      id = "Hitbox scale",
      min = 0.1,
      max = 10.0,
      step = 0.01,
      translationId = "physicsmod.prop.vine.hitboxscale"
   )
   public Vector3f hitboxScale;
   @Adjustable(
      id = "Stiffness",
      min = 0.1,
      max = 5000.0,
      step = 0.1,
      translationId = "physicsmod.prop.vine.stiffness"
   )
   public float stiffness;
   @Adjustable(
      id = "Damping",
      min = 0.1,
      max = 100.0,
      step = 0.1,
      translationId = "physicsmod.prop.vine.damping"
   )
   public float damping;

   public VineSetting(
      boolean bottomFixed,
      boolean waterPhysics,
      boolean sideConnection,
      Vector3f hitboxScale,
      float stiffness,
      float damping,
      boolean linkedPhysics,
      Block link
   ) {
      this.bottomFixed = bottomFixed;
      this.waterPhysics = waterPhysics;
      this.sideConnection = sideConnection;
      this.hitboxScale = hitboxScale;
      this.stiffness = stiffness;
      this.damping = damping;
      this.linkedPhysics = linkedPhysics;
      this.link = link;
   }

   public VineSetting() {
      this.bottomFixed = false;
      this.waterPhysics = false;
      this.sideConnection = false;
      this.hitboxScale = new Vector3f(1.0F);
      this.linkedPhysics = true;
      this.stiffness = 45.0F;
      this.damping = 45.0F;
   }

   @Override
   public DynamicRagdoll createRagdoll(PhysicsMod mod, BlockState current, BlockPos pos, Long2ObjectMap<BlockState> availableBlocks) {
      VineRagdoll ragdoll = new VineRagdoll();
      ragdoll.hitboxScale.set(this.hitboxScale);
      ragdoll.bottomFixed = this.bottomFixed;
      ragdoll.hookedEntity = this.createPart(mod, ragdoll, current, pos.getY(), pos.getX(), pos.getY(), pos.getZ());
      ragdoll.stiffness = this.stiffness;
      ragdoll.damping = this.damping;
      ragdoll.linkedPhysics = this.linkedPhysics;
      int count = 1;
      long indexNext = ChunkHelper.calcRelativeIndex(pos.getX(), pos.getY() + count, pos.getZ());

      BlockState state;
      for (state = null;
         (state = (BlockState)availableBlocks.get(indexNext)) != null && this.canLink(state, current);
         indexNext = ChunkHelper.calcRelativeIndex(pos.getX(), pos.getY() + ++count, pos.getZ())
      ) {
         PhysicsEntity vineEntity = this.createPart(mod, ragdoll, state, pos.getY(), pos.getX(), pos.getY() + count, pos.getZ());
         if (!ragdoll.bottomFixed) {
            ragdoll.hookedEntity = vineEntity;
         }

         ragdoll.addConnection(ragdoll.bodies.size() - 2, ragdoll.bodies.size() - 1);
         availableBlocks.remove(indexNext);
      }

      ragdoll.hookedEntity.physicsGroup = 8;
      ragdoll.hookedEntity.physicsMask = 0;
      Vector3f offset = ragdoll.hookedEntity.models.get(0).mesh.offset;
      ragdoll.hook = new Vector3d(0.0, (ragdoll.bottomFixed ? -1.0 : 1.0) * (1.0 - (double)offset.y % 1.0), 0.0);
      ragdoll.setAlwaysInWater(this.waterPhysics);
      availableBlocks.remove(ChunkHelper.calcRelativeIndex(pos.getX(), pos.getY(), pos.getZ()));
      return ragdoll;
   }

   public boolean canLink(BlockState first, BlockState second) {
      Block a = first.getBlock();
      Block b = second.getBlock();
      if (a == b) {
         return true;
      } else {
         return this.link == b ? true : this.link == a;
      }
   }

   @Override
   public boolean isValid(BlockState state) {
      return true;
   }

   @Override
   public Block defaultBlock() {
      return Blocks.VINE;
   }
}
