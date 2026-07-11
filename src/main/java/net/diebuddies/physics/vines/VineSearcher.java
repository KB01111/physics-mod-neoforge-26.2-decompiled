package net.diebuddies.physics.vines;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.diebuddies.minecraft.ChunkHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.Palette;

public class VineSearcher implements FastBlockSearcherConsumer {
   private Palette<BlockState> palette;
   private Long2ObjectMap<BlockState> vines;
   private int bottomBlockY;
   private int count;
   public boolean affected;

   public VineSearcher(Long2ObjectMap<BlockState> vines, Palette<BlockState> data, int bottomBlockY) {
      this.palette = data;
      this.vines = vines;
      this.bottomBlockY = bottomBlockY;
   }

   public static boolean isPhysicsDynamicBlock(BlockState blockState) {
      return VineHelper.getSetting(blockState) != null;
   }

   @Override
   public void accept(int value, int amount) {
      this.accept((BlockState)this.palette.valueFor(value), amount);
   }

   @Override
   public void accept(BlockState state, int amount) {
      if (VineHelper.getSetting(state) != null) {
         for (int i = 0; i < amount; i++) {
            int x = this.count & 15;
            int y = this.count >> 8 & 15;
            int z = this.count >> 4 & 15;
            this.vines.put(ChunkHelper.calcIndex(x, y + this.bottomBlockY, z), state);
            this.affected = true;
            this.count++;
         }
      } else {
         this.count += amount;
      }
   }
}
