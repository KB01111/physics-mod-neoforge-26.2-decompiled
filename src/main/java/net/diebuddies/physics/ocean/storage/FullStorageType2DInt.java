package net.diebuddies.physics.ocean.storage;

import net.diebuddies.physics.ocean.Index;

public class FullStorageType2DInt implements ImmutableStorageTypeInt {
   private int[] storage;

   public FullStorageType2DInt(int[] data) {
      this.storage = data;
   }

   public FullStorageType2DInt(int size) {
      this.storage = new int[size];
   }

   @Override
   public int getData(int x, int z) {
      return this.storage[Index.chunkStorage(x, z)];
   }
}
