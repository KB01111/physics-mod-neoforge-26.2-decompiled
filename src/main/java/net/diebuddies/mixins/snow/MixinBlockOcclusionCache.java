package net.diebuddies.mixins.snow;

import net.caffeinemc.mods.sodium.client.render.model.AbstractBlockRenderContext;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin({AbstractBlockRenderContext.class})
public class MixinBlockOcclusionCache {
   @Shadow
   @Final
   private MutableBlockPos cachedPositionObject;
   @Shadow
   private BlockAndTintGetter level;
   @Shadow
   private BlockPos pos;

   @Inject(
      at = {@At("RETURN")},
      method = {"shouldDrawSide"},
      cancellable = true
   )
   private void physicsmod$fixSnowPhysicsOcclusion(Direction facing, CallbackInfoReturnable<Boolean> info) {
   }
}
