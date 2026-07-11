package net.diebuddies.physics.snow;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.diebuddies.physics.ocean.storage.FullStorageType;
import net.diebuddies.physics.snow.math.Ray;
import net.diebuddies.physics.snow.math.RayHit;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;

public abstract class IWorld<T extends IChunk> {
   public static final byte VOXEL_ADD = 0;
   public static final byte VOXEL_SUBTRACT = 1;
   public static final byte VOXEL_SET = 2;
   public final Long2ObjectMap<T> loadedChunks = new Long2ObjectOpenHashMap(25600);
   public final int minVoxelY;
   public final int maxVoxelY;
   public final int minChunkY;
   public final int maxChunkY;
   public final int heightChunks;
   protected FullStorageType modulationLayer;
   protected FullStorageType modulationLayerRaw;

   public IWorld(int minChunkY, int maxChunkY) {
      this.minChunkY = minChunkY;
      this.maxChunkY = maxChunkY;
      this.minVoxelY = minChunkY * IChunk.CHUNK_SIZE;
      this.maxVoxelY = maxChunkY * IChunk.CHUNK_SIZE;
      this.heightChunks = maxChunkY - minChunkY + 1;
   }

   public T getChunk(int x, int y, int z) {
      return (T)(y >= this.minChunkY && y <= this.maxChunkY ? this.loadedChunks.get(Index.chunk(x, y, z)) : null);
   }

   public T removeChunk(long index) {
      T c = (T)this.loadedChunks.remove(index);
      if (c != null) {
         c.setLoadedNeighbourCount(this, (byte)0);
         this.editNeighbourCount(c.x, c.y, c.z, -1);
      }

      return c;
   }

   public void addChunk(T chunk) {
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

   public float getDensity(double x, double y, double z) {
      IChunk c = this.getChunkWorldPos(x, y, z);
      return c != null ? c.getDensity(this, WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosY(y), WorldUtil.calculateVoxelPosZ(z)) : -1.0F;
   }

   public byte getData(int x, int y, int z) {
      IChunk c = this.getChunkWorldPos(x, y, z);
      return c != null ? c.getDataByte(this, WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosY(y), WorldUtil.calculateVoxelPosZ(z)) : -127;
   }

   public byte getLightData(int x, int y, int z) {
      IChunk c = this.getChunkWorldPos(x, y, z);
      return c != null ? c.getLightDataByte(this, WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosY(y), WorldUtil.calculateVoxelPosZ(z)) : -16;
   }

   public Vector3f calculateNormal(double x, double y, double z) {
      IChunk c = this.getChunkWorldPos(x, y, z);
      return c != null
         ? c.calculateNormal(this, WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosY(y), WorldUtil.calculateVoxelPosZ(z), 1.0F)
         : new Vector3f(0.0F, -1.0F, 0.0F);
   }

   public boolean isSolid(int x, int y, int z) {
      return this.getData(x, y, z) >= 0;
   }

   public boolean isSolid(double x, double y, double z) {
      return this.getDensity(x, y, z) >= 0.0F;
   }

   public boolean isSolid(Vector3i pos) {
      return this.isSolid(pos.x, pos.y, pos.z);
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
         c.setData(this, WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosY(y), WorldUtil.calculateVoxelPosZ(z), data);
      }
   }

   public void setLightData(int x, int y, int z, byte data) {
      IChunk c = this.getChunkWorldPos(x, y, z);
      if (c != null) {
         c.setLightData(this, WorldUtil.calculateVoxelPosX(x), WorldUtil.calculateVoxelPosY(y), WorldUtil.calculateVoxelPosZ(z), data);
      }
   }

   public RayHit castFastLevelRay(Ray ray, double maxLength, double stepSize, double binarySearchStepSize, int maxSteps) {
      ray.getDirection().normalize();
      Vector3d currentPosition = new Vector3d(ray.getStart());
      boolean hit = false;
      int steps = 0;
      double length = 0.0;

      while (!hit && steps < maxSteps) {
         hit = (double)this.getDensity(currentPosition.x, currentPosition.y, currentPosition.z) >= 0.0;
         if (hit) {
            currentPosition.sub(ray.getDirection().x * stepSize, ray.getDirection().y * stepSize, ray.getDirection().z * stepSize);

            for (int i = 0; (double)i <= stepSize / binarySearchStepSize; i++) {
               hit = (double)this.getDensity(currentPosition.x, currentPosition.y, currentPosition.z) >= 0.0;
               if (hit) {
                  return new RayHit(new Vector3d(this.calculateNormal(currentPosition.x, currentPosition.y, currentPosition.z)), currentPosition);
               }

               currentPosition.add(
                  ray.getDirection().x * binarySearchStepSize, ray.getDirection().y * binarySearchStepSize, ray.getDirection().z * binarySearchStepSize
               );
            }

            return new RayHit(new Vector3d(this.calculateNormal(currentPosition.x, currentPosition.y, currentPosition.z)), currentPosition);
         }

         length += stepSize;
         currentPosition.add(ray.getDirection().x * stepSize, ray.getDirection().y * stepSize, ray.getDirection().z * stepSize);
         steps++;
         if (length >= maxLength) {
            return null;
         }
      }

      return null;
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
