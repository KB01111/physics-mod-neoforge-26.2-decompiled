package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.diebuddies.physics.vines.FastBlockSearcherConsumer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.Palette;

public class OceanSearcher implements FastBlockSearcherConsumer {
   private Palette<BlockState> palette;
   private IntList countData;
   private byte current;
   private int count;
   private int repeatingAmount;

   public OceanSearcher(Palette<BlockState> data) {
      this.palette = data;
      this.countData = new IntArrayList(256);
   }

   public static boolean isPhysicsOcean(BlockState blockState) {
      return BlockToByte.convert(blockState) != -1;
   }

   @Override
   public void accept(int value, int amount) {
      this.accept((BlockState)this.palette.valueFor(value), amount);
   }

   @Override
   public void accept(BlockState state, int amount) {
      byte blockByte = BlockToByte.convert(state);
      if (this.count != 0 && blockByte != this.current) {
         this.countData.add(this.repeatingAmount | this.current << 16);
         this.repeatingAmount = 0;
      }

      this.repeatingAmount += amount;
      this.count += amount;
      if (this.count == 4096 && this.repeatingAmount != 0) {
         this.countData.add(this.repeatingAmount | blockByte << 16);
      }

      this.current = blockByte;
   }

   public IntList getCountData() {
      return this.countData;
   }
}
