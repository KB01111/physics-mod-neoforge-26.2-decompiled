package net.diebuddies.physics.vines;

import net.minecraft.world.level.block.state.BlockState;

public interface FastBlockSearcherConsumer {
   void accept(int var1, int var2);

   void accept(BlockState var1, int var2);
}
