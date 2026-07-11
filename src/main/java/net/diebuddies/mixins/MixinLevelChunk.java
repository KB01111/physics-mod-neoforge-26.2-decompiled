package net.diebuddies.mixins;

import com.mojang.blaze3d.systems.RenderSystem;
import net.diebuddies.physics.BlockUpdate;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LevelChunk.class})
public class MixinLevelChunk {
   @Shadow
   private Level level;

   @Inject(
      at = {@At("HEAD")},
      method = {"setBlockState"}
   )
   private void setBlockState(BlockPos pos, BlockState state, int type, CallbackInfoReturnable<BlockState> ci) {
      if (RenderSystem.isOnRenderThread() && this.level instanceof ClientLevel clientLevel) {
         BlockState before = this.level.getBlockState(pos);
         PhysicsMod mod = PhysicsMod.getInstance(clientLevel);
         if ((type & 64) == 0) {
            mod.blockUpdates.add(pos.immutable());
            if (before != null
               && state != null
               && (before.isAir() && !state.isAir() || state.isAir() && !before.isAir() || !before.isAir() && state.getBlock() == Blocks.WATER)) {
               BlockUpdate update = new BlockUpdate(clientLevel, pos.immutable(), before);
               if (before.hasBlockEntity()) {
                  update.blockEntity = this.level.getBlockEntity(pos);
               }

               mod.updateQueue.add(update);
            }
         }
      }
   }
}
