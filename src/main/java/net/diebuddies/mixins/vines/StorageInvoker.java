package net.diebuddies.mixins.vines;

import net.minecraft.client.multiplayer.ClientChunkCache.Storage;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Storage.class})
public interface StorageInvoker {
   @Invoker("getChunk")
   LevelChunk invokeGetChunk(int var1);

   @Invoker("getIndex")
   int invokeGetIndex(int var1, int var2);

   @Invoker("inRange")
   boolean invokeInRange(int var1, int var2);
}
