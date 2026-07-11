package net.diebuddies.mixins.vines;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockQuadOutput;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ModelBlockRenderer.class})
public class MixinBlockRenderManager {
   @Inject(
      at = {@At("HEAD")},
      method = {"tesselateBlock"},
      cancellable = true
   )
   private void physicsmod$cancelBlockRender(
      BlockQuadOutput output,
      float x,
      float y,
      float z,
      BlockAndTintGetter level,
      BlockPos pos,
      BlockState state,
      BlockStateModel model,
      long seed,
      CallbackInfo info
   ) {
   }
}
