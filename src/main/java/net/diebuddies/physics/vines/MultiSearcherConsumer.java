package net.diebuddies.physics.vines;

import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.Palette;

public class MultiSearcherConsumer implements FastBlockSearcherConsumer {
   private Palette<BlockState> data;
   private FastBlockSearcherConsumer[] consumers;
   private int length;

   public MultiSearcherConsumer(Palette<BlockState> data, List<FastBlockSearcherConsumer> blockSearcherConsumers) {
      this.consumers = blockSearcherConsumers.toArray(new FastBlockSearcherConsumer[blockSearcherConsumers.size()]);
      this.data = data;
      this.length = this.consumers.length;
   }

   @Override
   public void accept(int value, int amount) {
      BlockState state = (BlockState)this.data.valueFor(value);

      for (int i = 0; i < this.length; i++) {
         this.consumers[i].accept(state, amount);
      }
   }

   @Override
   public void accept(BlockState state, int amount) {
   }
}
