package net.diebuddies.physics;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.diebuddies.bridge.FabricAPIServer;
import net.diebuddies.config.ConfigServer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3i;

public class ServerPhysicsMod implements FabricAPIServer.StartWorldTick, FabricAPIServer.After {
   private Map<Level, List<FallingBlocks>> fallingBlocks = new ConcurrentHashMap<>();

   public ServerPhysicsMod() {
      ConfigServer.init();
   }

   private boolean canFall(Level level, BlockPos pos, BlockState b) {
      if (b.getBlock() != Blocks.OBSIDIAN
         && b.getBlock() != Blocks.BEDROCK
         && b.getBlock() != Blocks.NETHER_PORTAL
         && b.getBlock() != Blocks.END_PORTAL
         && b.getBlock() != Blocks.END_PORTAL_FRAME
         && !b.isAir()
         && b.getBlock() != Blocks.WATER
         && b.getBlock() != Blocks.LAVA) {
         BlockState toCheck = level.getBlockState(pos.below());
         Block block = toCheck.getBlock();
         if (toCheck.isAir() || block == Blocks.WATER || block == Blocks.LAVA || block == Blocks.COBWEB) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void afterBlockBreak(Level level, Player player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
      if (ConfigServer.collapse) {
         this.checkAdjacentBlocks(level, player, pos.getX(), pos.getY(), pos.getZ());
      }
   }

   @Override
   public void onStartTick(ServerLevel level) {
      List<FallingBlocks> blocks = this.fallingBlocks.computeIfAbsent(level, key -> new ObjectArrayList());
      Iterator<FallingBlocks> it = blocks.iterator();

      while (it.hasNext()) {
         FallingBlocks falling = it.next();
         if (ConfigServer.collapseSpeed == 0 || falling.ticks % ConfigServer.collapseSpeed == 0) {
            Vector3i tmp = new Vector3i();
            boolean end = false;

            do {
               Set<Vector3i> toCheck = new ObjectOpenHashSet();

               for (Vector3i check : falling.toCheck) {
                  for (int y = -1; y <= 1; y++) {
                     for (int x = -1; x <= 1; x++) {
                        for (int z = -1; z <= 1; z++) {
                           if (x != 0 || y != 0 || z != 0) {
                              tmp.set(check.x + x, check.y + y, check.z + z);
                              if (!falling.alreadyChecked.contains(tmp)) {
                                 BlockPos blockPos = new BlockPos(check.x + x, check.y + y, check.z + z);
                                 BlockState b = falling.level.getBlockState(blockPos);
                                 falling.alreadyChecked.add(new Vector3i(tmp));
                                 if (this.canFall(falling.level, blockPos, b)) {
                                    if (ConfigServer.dropBlocks) {
                                       BlockState state = falling.level.getBlockState(blockPos);
                                       BlockEntity te = falling.level.getBlockEntity(blockPos);
                                       state.getBlock().playerDestroy(falling.level, falling.player, blockPos, state, te, falling.player.getUseItem());
                                    }

                                    falling.level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
                                    falling.fallen.add(new Vector3i(tmp));
                                    toCheck.add(new Vector3i(tmp));
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (falling.fallen.size() > ConfigServer.maxCollapseObjects) {
                     end = true;
                     it.remove();
                     break;
                  }
               }

               falling.toCheck = toCheck;
               if (falling.toCheck.size() == 0) {
                  end = true;
                  it.remove();
               }
            } while (ConfigServer.collapseSpeed == 0 && !end);
         }

         falling.ticks++;
      }
   }

   private void checkAdjacentBlocks(Level level, Player player, int bx, int by, int bz) {
      FallingBlocks falling = new FallingBlocks(level, player);
      falling.toCheck.add(new Vector3i(bx, by, bz));
      List<FallingBlocks> blocks = this.fallingBlocks.computeIfAbsent(level, key -> new ObjectArrayList());
      blocks.add(falling);
   }
}
