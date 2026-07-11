package net.diebuddies.physics.snow.storage;

import net.diebuddies.physics.ocean.storage.AdaptiveStorageType;
import net.diebuddies.physics.ocean.storage.EqualStorageType;
import net.diebuddies.physics.ocean.storage.StorageType;
import net.diebuddies.physics.snow.Index;

public class StorageLight implements StorageContainerLight {
   private static final int VANILLA_SIZE = 4096;
   public volatile StorageType data;
   public final byte divider;
   public final byte offset;

   public StorageLight(byte data, byte divider) {
      this.divider = divider;
      this.offset = (byte)(32 - Integer.numberOfLeadingZeros(divider - 1));
      this.data = new EqualStorageType(data);
   }

   public StorageLight(byte[] data, byte divider) {
      this.divider = divider;
      this.offset = (byte)(32 - Integer.numberOfLeadingZeros(divider - 1));
      this.data = AdaptiveStorageType.fromFull(data);
   }

   @Override
   public byte getData(int x, int y, int z) {
      return this.getData(Index.vanillaChunkStorage(x >> this.offset, y >> this.offset, z >> this.offset));
   }

   @Override
   public void setData(int x, int y, int z, byte value) {
      this.setData(Index.vanillaChunkStorage(x >> this.offset, y >> this.offset, z >> this.offset), value);
   }

   @Override
   public boolean setAndCompareData(int x, int y, int z, byte value) {
      return this.setAndCompareData(Index.vanillaChunkStorage(x >> this.offset, y >> this.offset, z >> this.offset), value);
   }

   @Override
   public void setAndInvalidate(int x, int y, int z, byte value) {
      this.setData(Index.vanillaChunkStorage(x >> this.offset, y >> this.offset, z >> this.offset), value);
   }

   @Override
   public byte getData(int index) {
      return this.data.getData(index);
   }

   @Override
   public void setData(int index, byte value) {
      this.data.setData(this, index, value);
   }

   @Override
   public boolean setAndCompareData(int index, byte value) {
      return this.data.setAndCompareData(this, index, value);
   }

   @Override
   public StorageType getStorageType() {
      return this.data;
   }

   @Override
   public int getSize() {
      return 4096;
   }

   @Override
   public void setData(StorageType data) {
      this.data = data;
   }
}
