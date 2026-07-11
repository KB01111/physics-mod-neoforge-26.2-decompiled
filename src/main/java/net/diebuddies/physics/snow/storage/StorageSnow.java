package net.diebuddies.physics.snow.storage;

import net.diebuddies.math.Math;
import net.diebuddies.physics.ocean.storage.AdaptiveStorageType;
import net.diebuddies.physics.ocean.storage.EqualStorageType;
import net.diebuddies.physics.ocean.storage.FullStorageType;
import net.diebuddies.physics.ocean.storage.FullStorageType2DBit;
import net.diebuddies.physics.ocean.storage.StorageType;
import net.diebuddies.physics.snow.Index;

public class StorageSnow implements StorageContainerSnow {
   public volatile StorageType data;
   public FullStorageType2DBit invalid;
   public final int size;

   public StorageSnow(byte data, int size) {
      this.size = size;
      this.data = new EqualStorageType(data);
   }

   public StorageSnow(byte[] data, int size) {
      this.size = size;
      this.data = AdaptiveStorageType.fromFull(data);
   }

   @Override
   public byte getData(FullStorageType modulation, FullStorageType modulationRaw, int x, int y, int z) {
      int index = Index.chunkStorage(x, y, z);
      byte value = this.data.getData(index);
      boolean use = this.invalid != null && this.invalid.getData(index) > 0;
      if (use) {
         return value;
      } else if (value < 0) {
         return value == -127 ? modulation.getData(index) : value;
      } else {
         return Math.clamp(value + modulationRaw.getData(index), (byte)-127, (byte)127);
      }
   }

   @Override
   public void setData(int x, int y, int z, byte value) {
      this.setData(Index.chunkStorage(x, y, z), value);
   }

   @Override
   public boolean setAndCompareData(int x, int y, int z, byte value) {
      return this.setAndCompareData(Index.chunkStorage(x, y, z), value);
   }

   @Override
   public byte getData(int index) {
      return this.data.getData(index);
   }

   @Override
   public void setData(int index, byte value) {
      this.data.setData(this, index, value);
      if (this.invalid != null) {
         this.invalid.unsetData(index);
      }
   }

   @Override
   public boolean setAndCompareData(int index, byte value) {
      return this.data.setAndCompareData(this, index, value);
   }

   @Override
   public void setAndInvalidate(int x, int y, int z, byte value) {
      this.data.setData(this, Index.chunkStorage(x, y, z), value);
      if (this.invalid == null) {
         this.invalid = new FullStorageType2DBit(this.size);
      }

      this.invalid.setData(Index.chunkStorage(x, y, z));
   }

   @Override
   public StorageType getStorageType() {
      return this.data;
   }

   @Override
   public int getSize() {
      return this.size;
   }

   @Override
   public void setData(StorageType data) {
      this.data = data;
   }
}
