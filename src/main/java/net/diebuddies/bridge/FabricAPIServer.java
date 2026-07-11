package net.diebuddies.bridge;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FabricAPIServer {
   public static final Event<FabricAPIServer.StartWorldTick> START_WORLD_TICK = EventFactory.createArrayBacked(
      FabricAPIServer.StartWorldTick.class, callbacks -> world -> {
            for (FabricAPIServer.StartWorldTick callback : callbacks) {
               callback.onStartTick(world);
            }
         }
   );
   public static final Event<FabricAPIServer.After> AFTER = EventFactory.createArrayBacked(
      FabricAPIServer.After.class, listeners -> (world, player, pos, state, entity) -> {
            for (FabricAPIServer.After event : listeners) {
               event.afterBlockBreak(world, player, pos, state, entity);
            }
         }
   );

   @FunctionalInterface
   public interface After {
      void afterBlockBreak(Level var1, Player var2, BlockPos var3, BlockState var4, BlockEntity var5);
   }

   @FunctionalInterface
   public interface StartWorldTick {
      void onStartTick(ServerLevel var1);
   }
}
