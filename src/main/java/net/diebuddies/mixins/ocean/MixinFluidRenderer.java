package net.diebuddies.mixins.ocean;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.DefaultFluidRenderer;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({DefaultFluidRenderer.class})
public class MixinFluidRenderer {
   @Inject(
      at = {@At("HEAD")},
      method = {"isSideExposedOffset"},
      cancellable = true
   )
   public void isSideExposed(
      BlockAndTintGetter world, BlockState ownBlockState, BlockPos originPos, Direction dir, float height, CallbackInfoReturnable<Boolean> info
   ) {
   }
}
