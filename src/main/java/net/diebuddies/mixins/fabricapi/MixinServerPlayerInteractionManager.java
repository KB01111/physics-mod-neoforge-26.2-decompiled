package net.diebuddies.mixins.fabricapi;

import net.diebuddies.bridge.FabricAPIServer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({ServerPlayerGameMode.class})
public class MixinServerPlayerInteractionManager {
   @Shadow
   public ServerLevel level;
   @Shadow
   public ServerPlayer player;

   @Inject(
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/Block;destroy(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"
      )},
      method = {"removeBlock"},
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   private void onBlockBroken(BlockPos pos, BlockState state, boolean harvest, ItemStack toolStack, CallbackInfoReturnable<Boolean> cir, boolean b1) {
      FabricAPIServer.AFTER.invoker().afterBlockBreak(this.level, this.player, pos, state, null);
   }
}
