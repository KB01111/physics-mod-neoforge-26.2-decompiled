package net.diebuddies.physics.ocean;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class BlockToByte {
   public static byte convert(BlockState state) {
      FluidState fluidState = state.getFluidState();
      if (fluidState.is(FluidTags.WATER)) {
         byte waterAmount = (byte)fluidState.getAmount();
         return state.blocksMotion() ? -2 : waterAmount;
      } else {
         return (byte)(!state.blocksMotion() && state.getBlock() != Blocks.LILY_PAD ? 0 : -1);
      }
   }
}
