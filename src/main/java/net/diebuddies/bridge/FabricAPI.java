package net.diebuddies.bridge;

import net.minecraft.client.Minecraft;

public class FabricAPI {
   public static final Event<FabricAPI.ClientStopping> CLIENT_STOPPING = EventFactory.createArrayBacked(
      FabricAPI.ClientStopping.class, callbacks -> client -> {
            for (FabricAPI.ClientStopping callback : callbacks) {
               callback.onClientStopping(client);
            }
         }
   );

   @FunctionalInterface
   public interface ClientStopping {
      void onClientStopping(Minecraft var1);
   }
}
