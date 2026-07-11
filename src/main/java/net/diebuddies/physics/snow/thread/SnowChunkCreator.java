package net.diebuddies.physics.snow.thread;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.diebuddies.math.Math;
import net.diebuddies.physics.snow.ChunkContouring;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.SnowSearcher;
import net.diebuddies.physics.snow.SnowWorld;
import net.diebuddies.physics.snow.WorldContouring;
import net.diebuddies.physics.snow.storage.StorageContainerLight;
import net.diebuddies.physics.snow.storage.StorageContainerSnow;
import net.diebuddies.physics.snow.storage.StorageLight;
import net.diebuddies.physics.snow.storage.StorageSnow;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SnowChunkCreator implements ChunkCreator {
   private SnowWorld snowWorld;
   private Long2ObjectMap<BlockState> snow;
   private int chunkX;
   private int chunkY;
   private int chunkZ;

   public SnowChunkCreator(SnowWorld snowWorld, Long2ObjectMap<BlockState> snow, int chunkX, int chunkY, int chunkZ) {
      this.snowWorld = snowWorld;
      this.snow = snow;
      this.chunkX = chunkX;
      this.chunkY = chunkY;
      this.chunkZ = chunkZ;
   }

   @Override
   public ChunkContouring create() {
      WorldContouring contouring = this.snowWorld.contouring;
      StorageContainerLight lightStorage = new StorageLight((byte)0, IChunk.CHUNK_MULTIPLE);
      StorageContainerSnow storage = new StorageSnow((byte)-127, IChunk.CHUNK_VOLUME);
      ObjectIterator var4 = this.snow.long2ObjectEntrySet().iterator();

      while (var4.hasNext()) {
         Entry<BlockState> entry = (Entry<BlockState>)var4.next();
         long pos = entry.getLongKey();
         BlockState state = (BlockState)entry.getValue();
         this.updateBlock(storage, BlockPos.getX(pos), BlockPos.getY(pos), BlockPos.getZ(pos), state);
      }

      return new ChunkContouring(contouring.getPlayerPosition(), contouring, this.chunkX, this.chunkY, this.chunkZ, storage, lightStorage);
   }

   private void updateBlock(StorageContainerSnow storage, int rx, int ry, int rz, BlockState state) {
      if (state.getBlock() == Blocks.SNOW) {
         int snowLayers = (Integer)state.getValue(SnowLayerBlock.LAYERS);

         for (int yo = 0; yo < IChunk.CHUNK_MULTIPLE; yo++) {
            int currentMaxLayer = yo * (8 / IChunk.CHUNK_MULTIPLE);
            double perc = Math.remapClamp(
               java.lang.Math.ceil((double)(snowLayers - currentMaxLayer) / (double)(8 / IChunk.CHUNK_MULTIPLE)), 0.0, 1.0, -1.0, 1.0
            );
            byte snow = (byte)((int)(perc * 127.0));

            for (int xo = 0; xo < IChunk.CHUNK_MULTIPLE; xo++) {
               for (int zo = 0; zo < IChunk.CHUNK_MULTIPLE; zo++) {
                  storage.setData(rx + xo, ry + yo, rz + zo, Math.clamp(snow, (byte)-127, (byte)127));
               }
            }
         }
      } else {
         for (int xo = 0; xo < IChunk.CHUNK_MULTIPLE; xo++) {
            for (int yo = 0; yo < IChunk.CHUNK_MULTIPLE; yo++) {
               for (int zo = 0; zo < IChunk.CHUNK_MULTIPLE; zo++) {
                  storage.setData(rx + xo, ry + yo, rz + zo, (byte)127);
               }
            }
         }
      }
   }

   public static void updateBlock(WorldContouring contouring, ChunkContouring storage, int rx, int ry, int rz, BlockState state) {
      boolean isPhysicsSnow = SnowSearcher.isPhysicsSnow(state);
      if (!isPhysicsSnow) {
         for (int xo = 0; xo < IChunk.CHUNK_MULTIPLE; xo++) {
            for (int yo = 0; yo < IChunk.CHUNK_MULTIPLE; yo++) {
               for (int zo = 0; zo < IChunk.CHUNK_MULTIPLE; zo++) {
                  storage.setData(contouring, rx + xo, ry + yo, rz + zo, (byte)-127);
               }
            }
         }
      } else if (state.hasProperty(SnowLayerBlock.LAYERS)) {
         int snowLayers = (Integer)state.getValue(SnowLayerBlock.LAYERS);

         for (int yo = 0; yo < IChunk.CHUNK_MULTIPLE; yo++) {
            int currentMaxLayer = yo * (8 / IChunk.CHUNK_MULTIPLE);
            double perc = Math.remapClamp(
               java.lang.Math.ceil((double)(snowLayers - currentMaxLayer) / (double)(8 / IChunk.CHUNK_MULTIPLE)), 0.0, 1.0, -1.0, 1.0
            );
            byte snow = (byte)((int)(perc * 127.0));

            for (int xo = 0; xo < IChunk.CHUNK_MULTIPLE; xo++) {
               for (int zo = 0; zo < IChunk.CHUNK_MULTIPLE; zo++) {
                  storage.setData(contouring, rx + xo, ry + yo, rz + zo, Math.clamp(snow, (byte)-127, (byte)127));
               }
            }
         }
      } else {
         for (int xo = 0; xo < IChunk.CHUNK_MULTIPLE; xo++) {
            for (int yo = 0; yo < IChunk.CHUNK_MULTIPLE; yo++) {
               for (int zo = 0; zo < IChunk.CHUNK_MULTIPLE; zo++) {
                  storage.setData(contouring, rx + xo, ry + yo, rz + zo, (byte)127);
               }
            }
         }
      }
   }

   @Override
   public int getX() {
      return this.chunkX;
   }

   @Override
   public int getY() {
      return this.chunkY;
   }

   @Override
   public int getZ() {
      return this.chunkZ;
   }
}
