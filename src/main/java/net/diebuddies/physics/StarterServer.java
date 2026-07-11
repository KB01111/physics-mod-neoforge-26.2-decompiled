package net.diebuddies.physics;

import net.diebuddies.bridge.FabricAPIServer;

public class StarterServer {
   public static void onInitializeServer() {
      ServerPhysicsMod server = new ServerPhysicsMod();
      FabricAPIServer.START_WORLD_TICK.register(server);
      FabricAPIServer.AFTER.register(server);
   }
}
