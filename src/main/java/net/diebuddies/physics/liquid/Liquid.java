package net.diebuddies.physics.liquid;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.diebuddies.physics.PhysicsWorld;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public abstract class Liquid {
   public PhysicsWorld world;
   public LiquidController controller;
   public BlockPos blockPos;
   public boolean sourceAlive;
   public short materialID = -1;
   public short renderType = 1;

   public Liquid(LiquidController controller) {
      this.controller = controller;
   }

   public abstract boolean update(PhysicsWorld var1, double var2);

   public abstract int particleCount();

   public abstract void spawnParticle(double var1, double var3, double var5);

   public abstract int fillInstances(PhysicsWorld var1, long var2);

   public abstract void destroy(PhysicsWorld var1);

   public void invalidateBrightness(LongSet updatedLightBlocks) {
   }

   public void blockUpdate(PhysicsWorld physicsWorld, BlockPos pos, BlockState state) {
      if (pos.equals(this.blockPos) && !state.isAir()) {
         this.sourceAlive = false;
      }
   }

   public void add(PhysicsWorld world) {
      this.world = world;
      this.controller.init(world, this);
   }
}
