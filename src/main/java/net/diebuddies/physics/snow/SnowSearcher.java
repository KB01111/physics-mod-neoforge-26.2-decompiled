package net.diebuddies.physics.snow;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigSnow;
import net.diebuddies.physics.vines.FastBlockSearcherConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.Palette;

public class SnowSearcher implements FastBlockSearcherConsumer {
   private Palette<BlockState> palette;
   private int xc;
   private int yc;
   private int zc;
   private SnowWorld snowWorld;
   private Long2ObjectMap<BlockState> snow;
   private ShortSet lightUpdates;
   private int count;

   public static boolean isPhysicsSnow(BlockState state) {
      Block block = state.getBlock();
      return ConfigSnow.activeBlocks.contains(block)
         ? true
         : ConfigClient.grassSnowy && block instanceof SnowyBlock && (Boolean)state.getValue(SnowyBlock.SNOWY);
   }

   public SnowSearcher(SnowWorld snowWorld, Long2ObjectMap<BlockState> snow, int x, int y, int z, Palette<BlockState> data) {
      this.snowWorld = snowWorld;
      this.xc = x;
      this.yc = y;
      this.zc = z;
      this.snow = snow;
      this.palette = data;
      this.lightUpdates = this.snowWorld
         .getLightUpdates(SectionPos.asLong(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(y), SectionPos.blockToSectionCoord(z)));
   }

   @Override
   public void accept(int value, int amount) {
      this.accept((BlockState)this.palette.valueFor(value), amount);
   }

   @Override
   public void accept(BlockState state, int amount) {
      if (isPhysicsSnow(state)) {
         for (int i = 0; i < amount; i++) {
            int x = this.count & 15;
            int y = this.count >> 8 & 15;
            int z = this.count >> 4 & 15;
            int rx = x * IChunk.CHUNK_MULTIPLE;
            int ry = y * IChunk.CHUNK_MULTIPLE;
            int rz = z * IChunk.CHUNK_MULTIPLE;
            this.snow.put(BlockPos.asLong(rx, ry, rz), state);
            queueLightUpdates(this.snowWorld, this.lightUpdates, x + this.xc, y + this.yc, z + this.zc);
            this.count++;
         }
      } else {
         this.count += amount;
      }
   }

   public static void queueLightUpdates(SnowWorld snowWorld, ShortSet lightUpdates, int x, int y, int z) {
      int ax = x & 15;
      int ay = y & 15;
      int az = z & 15;

      for (int xo = -1; xo <= 1; xo++) {
         for (int yo = -1; yo <= 1; yo++) {
            for (int zo = -1; zo <= 1; zo++) {
               int lx = ax + xo;
               int ly = ay + yo;
               int lz = az + zo;
               if (outOfBounds(lx, ly, lz)) {
                  ShortSet updates = snowWorld.getLightUpdates(
                     SectionPos.asLong(SectionPos.blockToSectionCoord(x + xo), SectionPos.blockToSectionCoord(y + yo), SectionPos.blockToSectionCoord(z + zo))
                  );
                  updates.add((short)((lx & 15) << 8 | (ly & 15) << 4 | lz & 15));
               } else {
                  lightUpdates.add((short)(lx << 8 | ly << 4 | lz));
               }
            }
         }
      }
   }

   private static boolean outOfBounds(int x, int y, int z) {
      return x >= 16 || y >= 16 || z >= 16 || x < 0 || y < 0 || z < 0;
   }
}
