package net.diebuddies.physics.smoke;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.diebuddies.physics.vines.FastBlockSearcherConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.Palette;

public class FireSearcher implements FastBlockSearcherConsumer {
   private Palette<BlockState> palette;
   private LongSet fire;
   private int bottomBlockY;
   private int count;
   public boolean affected;

   public FireSearcher(LongSet fire, Palette<BlockState> data, int bottomBlockY) {
      this.palette = data;
      this.fire = fire;
      this.bottomBlockY = bottomBlockY;
   }

   @Override
   public void accept(int value, int amount) {
      this.accept((BlockState)this.palette.valueFor(value), amount);
   }

   @Override
   public void accept(BlockState state, int amount) {
      if (SmokeDomain.isFire(state)) {
         for (int i = 0; i < amount; i++) {
            int x = this.count & 15;
            int y = this.count >> 8 & 15;
            int z = this.count >> 4 & 15;
            this.fire.add(BlockPos.asLong(x, y + this.bottomBlockY, z));
            this.affected = true;
            this.count++;
         }
      } else {
         this.count += amount;
      }
   }
}
