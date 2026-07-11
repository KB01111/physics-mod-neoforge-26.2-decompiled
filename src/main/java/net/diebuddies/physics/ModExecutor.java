package net.diebuddies.physics;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("physicsmod")
public class ModExecutor {
   public ModExecutor(IEventBus modEventBus, ModContainer modContainer) {
      if (FMLEnvironment.getDist().isClient()) {
         StarterClient.onInitializeClient(modEventBus, modContainer);
      }

      if (FMLEnvironment.getDist().isDedicatedServer()) {
         StarterServer.onInitializeServer();
      }
   }
}
