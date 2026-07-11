package net.diebuddies.physics.vines;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.diebuddies.minecraft.ChunkHelper;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.diebuddies.physics.ragdoll.TrapdoorRagdoll;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class TrapdoorSetting extends DynamicSetting {
   @Adjustable(
      id = "Hitbox scale",
      min = 0.1,
      max = 10.0,
      step = 0.01,
      translationId = "physicsmod.prop.trapdoor.hitboxscale"
   )
   public Vector3f hitboxScale;
   @Adjustable(
      id = "Stiffness",
      min = 0.1,
      max = 5000.0,
      step = 0.1,
      translationId = "physicsmod.prop.trapdoor.stiffness"
   )
   public float stiffness;
   @Adjustable(
      id = "Damping",
      min = 0.1,
      max = 100.0,
      step = 0.1,
      translationId = "physicsmod.prop.trapdoor.damping"
   )
   public float damping;
   @Adjustable(
      id = "Collision",
      min = 0.1,
      max = 100.0,
      step = 0.1,
      translationId = "physicsmod.prop.trapdoor.collision"
   )
   public boolean collision;

   public TrapdoorSetting(Vector3f hitboxScale, float stiffness, float damping, boolean linkedPhysics, boolean collision) {
      this.collision = collision;
      this.hitboxScale = hitboxScale;
      this.stiffness = stiffness;
      this.damping = damping;
      this.linkedPhysics = linkedPhysics;
   }

   public TrapdoorSetting() {
      this.collision = true;
      this.hitboxScale = new Vector3f(0.85F);
      this.stiffness = 1580.0F;
      this.damping = 8.0F;
      this.linkedPhysics = true;
   }

   @Override
   public DynamicRagdoll createRagdoll(PhysicsMod mod, BlockState current, BlockPos pos, Long2ObjectMap<BlockState> availableBlocks) {
      TrapdoorRagdoll ragdoll = new TrapdoorRagdoll();
      ragdoll.hitboxScale.set(this.hitboxScale);
      ragdoll.hookedEntity = this.createPart(mod, ragdoll, current, pos.getY(), pos.getX(), pos.getY(), pos.getZ());
      ragdoll.stiffness = this.stiffness;
      ragdoll.damping = this.damping;
      ragdoll.linkedPhysics = this.linkedPhysics;
      ragdoll.collision = this.collision;
      Vector3d hookPos = new Vector3d();
      if (current.getValue(TrapDoorBlock.HALF) == Half.TOP) {
         hookPos.y = 0.90625;
      } else {
         hookPos.y = 0.09375;
      }

      switch ((Direction)current.getValue(TrapDoorBlock.FACING)) {
         case SOUTH:
            hookPos.z = 0.09375;
            break;
         case WEST:
            hookPos.x = 0.90625;
            ragdoll.west = true;
            break;
         case EAST:
            hookPos.x = 0.09375;
            ragdoll.west = true;
            break;
         default:
            hookPos.z = 0.90625;
      }

      Vector3f offset = ragdoll.hookedEntity.models.get(0).mesh.offset;
      hookPos.x = hookPos.x - (double)offset.x % 1.0;
      hookPos.y = hookPos.y - (double)offset.y % 1.0;
      hookPos.z = hookPos.z - (double)offset.z % 1.0;
      ragdoll.hook = hookPos;
      availableBlocks.remove(ChunkHelper.calcRelativeIndex(pos.getX(), pos.getY(), pos.getZ()));
      return ragdoll;
   }

   @Override
   public boolean isValid(BlockState state) {
      return state.hasProperty(TrapDoorBlock.FACING) && state.hasProperty(TrapDoorBlock.OPEN) && state.hasProperty(TrapDoorBlock.HALF);
   }

   @Override
   public Block defaultBlock() {
      return Blocks.OAK_TRAPDOOR;
   }
}
