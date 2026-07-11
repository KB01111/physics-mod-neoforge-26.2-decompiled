package net.diebuddies.physics.liquid;

import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.joml.Random;

public class WaterController implements LiquidController {
   private BlockPos pos;
   private AABB aabb;
   private boolean continous;
   private int spawnAmount;
   private Random random;

   public WaterController(BlockPos pos, boolean continous, int spawnAmount) {
      this.continous = continous;
      this.spawnAmount = spawnAmount;
      this.pos = pos;
      this.aabb = new AABB(pos);
      this.random = new Random(System.nanoTime());
   }

   @Override
   public void init(PhysicsWorld world, Liquid liquid) {
      liquid.blockPos = this.pos;
      liquid.sourceAlive = this.continous;
      if (StarterClient.iris()) {
         liquid.materialID = Iris.getMaterialID(Blocks.WATER.defaultBlockState());
      } else if (StarterClient.optifabric) {
         liquid.materialID = (short)Optifine.getMaterialID(Blocks.WATER.defaultBlockState());
      }

      for (int i = 0; i < this.spawnAmount; i++) {
         int amount = ConfigClient.liquidCudaAmount * 16;
         double width = this.aabb.maxX - this.aabb.minX;
         double height = this.aabb.maxY - this.aabb.minY;
         double depth = this.aabb.maxZ - this.aabb.minZ;

         for (int j = 0; j < amount; j++) {
            liquid.spawnParticle(
               (double)this.random.nextFloat() * width + this.aabb.minX,
               (double)this.random.nextFloat() * height + this.aabb.minY,
               (double)this.random.nextFloat() * depth + this.aabb.minZ
            );
         }
      }
   }

   @Override
   public void update(Liquid liquid, double diff) {
      if (StarterClient.iris()) {
         liquid.materialID = Iris.getMaterialID(Blocks.WATER.defaultBlockState());
      } else if (StarterClient.optifabric) {
         liquid.materialID = (short)Optifine.getMaterialID(Blocks.WATER.defaultBlockState());
      }

      if (liquid.sourceAlive) {
         int amount = 4;
         if (ConfigClient.cudaLiquids()) {
            amount *= ConfigClient.liquidCudaAmount;
         } else {
            amount *= ConfigClient.liquidAmount;
         }

         double width = this.aabb.maxX - this.aabb.minX;
         double height = this.aabb.maxY - this.aabb.minY;
         double depth = this.aabb.maxZ - this.aabb.minZ;

         for (int i = 0; i < amount; i++) {
            liquid.spawnParticle(
               (double)this.random.nextFloat() * width + this.aabb.minX,
               (double)this.random.nextFloat() * height + this.aabb.minY,
               (double)this.random.nextFloat() * depth + this.aabb.minZ
            );
         }
      }
   }
}
