package net.diebuddies.mixins.ocean;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.block.FluidRenderer.Output;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({FluidRenderer.class})
public class MixinLiquidBlockRenderer {
   @Shadow
   private static boolean isFaceOccludedByNeighbor(Direction direction, float f, BlockState blockState) {
      return false;
   }

   @Redirect(
      method = {"tesselate"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/block/FluidRenderer;isFaceOccludedByNeighbor(Lnet/minecraft/core/Direction;FLnet/minecraft/world/level/block/state/BlockState;)Z"
      )
   )
   private boolean physicsmod$isFaceOccludedByNeighbor(
      Direction direction,
      float height,
      BlockState blockState,
      BlockAndTintGetter blockAndTintGetter,
      BlockPos blockPos,
      Output output,
      BlockState blockStateAtPos,
      FluidState fluidState
   ) {
      return isFaceOccludedByNeighbor(direction, height, blockState);
   }
}
