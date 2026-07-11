package net.diebuddies.minecraft;

import net.minecraft.client.multiplayer.ClientChunkCache.Storage;

public interface ClientChunkCacheAccessor {
   Storage getStorage();
}
