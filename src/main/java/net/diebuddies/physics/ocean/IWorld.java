package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;

public abstract class IWorld<T extends IChunk> {
   public final Long2ObjectMap<T> loadedChunks = new Long2ObjectOpenHashMap(25600);
   public final int minVoxelY;
   public final int maxVoxelY;
   public final int minChunkY;
   public final int maxChunkY;

   public IWorld(int minChunkY, int maxChunkY) {
      this.minChunkY = minChunkY;
      this.maxChunkY = maxChunkY;
      this.minVoxelY = minChunkY * 16;
      this.maxVoxelY = maxChunkY * 16;
   }

   public T getChunk(int x, int y, int z) {
      return (T)(y >= this.minChunkY && y <= this.maxChunkY ? this.loadedChunks.get(Index.chunk(x, y, z)) : null);
   }

   private T removeChunk(long index) {
      T c = (T)this.loadedChunks.remove(index);
      if (c != null) {
         c.setLoadedNeighbourCount(this, (byte)0);
         this.editNeighbourCount(c.x, c.y, c.z, -1);
      }

      return c;
   }

   public void removeChunkColumn(int x, int z) {
      for (int y = this.minChunkY; y <= this.maxChunkY; y++) {
         this.removeChunk(Index.chunk(x, y, z));
      }
   }

   public void removeAll() {
      LongSet copy = new LongOpenHashSet(this.loadedChunks.keySet());
      LongIterator it = copy.longIterator();

      while (it.hasNext()) {
         long index = it.nextLong();
         this.removeChunk(index);
      }
   }

   public void addChunkColumn(List<T> chunkColumn) {
      for (T chunk : chunkColumn) {
         this.addChunk(chunk);
      }
   }

   private void addChunk(T chunk) {
      long index = Index.chunk(chunk.x, chunk.y, chunk.z);
      if (this.loadedChunks.get(index) != null) {
         IChunk var4 = this.removeChunk(index);
      }

      this.loadedChunks.put(index, chunk);
      chunk.setLoadedNeighbourCount(this, this.editNeighbourCount(chunk.x, chunk.y, chunk.z, 1));
   }

   public boolean isChunkLoaded(int x, int y, int z) {
      if (y > this.maxChunkY) {
         return true;
      } else if (y < this.minChunkY) {
         return true;
      } else {
         T c = (T)this.loadedChunks.get(Index.chunk(x, y, z));
         return c != null;
      }
   }

   public byte getData(int x, int y, int z) {
      IChunk c = this.getChunkWorldPos(x, y, z);
      return c != null ? c.getDataFast(WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosY(y), WorldUtil.calculateVoxelPosZ(z)) : 0;
   }

   public T getChunkWorldPos(int x, int y, int z) {
      if (y >= this.minVoxelY && y <= this.maxVoxelY) {
         int chunkX = WorldUtil.calculateChunkPosX(x);
         int chunkY = WorldUtil.calculateChunkPosY(this, y);
         int chunkZ = WorldUtil.calculateChunkPosZ(z);
         return this.getChunk(chunkX, chunkY, chunkZ);
      } else {
         return null;
      }
   }

   public T getChunkWorldPos(double x, double y, double z) {
      if (!(y < (double)this.minVoxelY) && !(y > (double)this.maxVoxelY)) {
         int chunkX = WorldUtil.calculateChunkPosX(x);
         int chunkY = WorldUtil.calculateChunkPosY(this, y);
         int chunkZ = WorldUtil.calculateChunkPosZ(z);
         return this.getChunk(chunkX, chunkY, chunkZ);
      } else {
         return null;
      }
   }

   public void setData(int x, int y, int z, byte data) {
      IChunk c = this.getChunkWorldPos(x, y, z);
      if (c != null) {
         c.setData(WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosY(y), WorldUtil.calculateVoxelPosZ(z), data);
      }
   }

   public byte editNeighbourCount(int chunkX, int chunkY, int chunkZ, int edit) {
      byte count = 0;

      for (int x = -1; x <= 1; x++) {
         for (int z = -1; z <= 1; z++) {
            if (x != 0 || z != 0) {
               IChunk neighbour = (IChunk)this.loadedChunks.get(Index.chunk(chunkX + x, chunkY, chunkZ + z));
               if (neighbour != null) {
                  neighbour.setLoadedNeighbourCount(this, (byte)(neighbour.getLoadedNeighbourCount() + edit));
                  count++;
               }
            }
         }
      }

      return count;
   }

   public boolean areSurroundingsLoaded(int x, int y, int z) {
      IChunk c = this.getChunk(x, y, z);
      return c != null && c.getLoadedNeighbourCount() == 8;
   }
}
