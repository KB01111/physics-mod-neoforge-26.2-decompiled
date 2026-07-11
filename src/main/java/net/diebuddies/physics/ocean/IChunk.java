package net.diebuddies.physics.ocean;

import net.diebuddies.physics.ocean.storage.StorageContainer;
import org.joml.Vector3i;

public abstract class IChunk<T extends IWorld> {
   public static final int ALL_NEIGHBOURS_LOADED = 8;
   public static final byte MIN_VALUE = -127;
   public static final byte MAX_VALUE = 127;
   public static final int CHUNK_SIZE = 16;
   public static final int CHUNK_SIZE_HALF = 8;
   public static final int CHUNK_SIZE_BITS = 15;
   public static final int CHUNK_SIZE_MINUS_ONE = 15;
   public static final int CHUNK_SIZE_USED_BITS = 32 - Integer.numberOfLeadingZeros(15);
   public static final int CHUNK_VOLUME = 4096;
   public static final byte AIR = 0;
   public static final byte SOLID = -1;
   public static final byte WATERLOGGED = -2;
   public final StorageContainer dataStorage;
   public final int x;
   public final int y;
   public final int z;
   public final int hashCode;
   protected byte loadedNeighbourCount;

   public IChunk(int x, int y, int z, StorageContainer dataStorage) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.dataStorage = dataStorage;
      int prime = 31;
      int chashCode = 1;
      chashCode = 31 * chashCode + x;
      chashCode = 31 * chashCode + y;
      chashCode = 31 * chashCode + z;
      this.hashCode = chashCode;
   }

   public int xVoxel() {
      return this.x << CHUNK_SIZE_USED_BITS;
   }

   public int yVoxel() {
      return this.y << CHUNK_SIZE_USED_BITS;
   }

   public int zVoxel() {
      return this.z << CHUNK_SIZE_USED_BITS;
   }

   public byte getLoadedNeighbourCount() {
      return this.loadedNeighbourCount;
   }

   public void setLoadedNeighbourCount(IWorld<?> world, byte loadedNeighbourCount) {
      this.loadedNeighbourCount = loadedNeighbourCount;
   }

   public byte getData(IWorld world, int x, int y, int z) {
      return this.outOfBounds(x, y, z)
         ? world.getData(this.xVoxel() + x, this.yVoxel() + y, this.zVoxel() + z)
         : this.dataStorage.getData(Index.chunkStorage(x, y, z));
   }

   public byte getDataFast(int x, int y, int z) {
      return this.dataStorage.getData(Index.chunkStorage(x, y, z));
   }

   public void setData(int x, int y, int z, byte data) {
      this.dataStorage.setData(Index.chunkStorage(x, y, z), data);
   }

   public boolean outOfBounds(int x, int y, int z) {
      return x >= 16 || y >= 16 || z >= 16 || x < 0 || y < 0 || z < 0;
   }

   public boolean isSolid(IWorld world, Vector3i pos) {
      return this.isSolid(world, pos.x, pos.y, pos.z);
   }

   public boolean isSolid(IWorld world, int x, int y, int z) {
      return this.getData(world, x, y, z) <= -1;
   }

   public IChunk getNeighbourChunk(IWorld world, int xOffset, int yOffset, int zOffset) {
      return world.getChunk(this.x + xOffset, this.y + yOffset, this.z + zOffset);
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         IChunk other = (IChunk)obj;
         if (this.x != other.x) {
            return false;
         } else {
            return this.y != other.y ? false : this.z == other.z;
         }
      }
   }
}
