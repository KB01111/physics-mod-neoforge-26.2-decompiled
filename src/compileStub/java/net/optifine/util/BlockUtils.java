package net.optifine.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BlockUtils {
   public static boolean shouldSideBeRendered(BlockState blockStateIn, BlockGetter blockReaderIn, BlockPos blockPosIn, Direction facingIn, Object renderEnv) {
      return true;
   }
}
