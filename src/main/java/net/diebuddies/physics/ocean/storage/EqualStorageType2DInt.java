package net.diebuddies.physics.ocean.storage;

public class EqualStorageType2DInt implements ImmutableStorageTypeInt {
   private int data;

   public EqualStorageType2DInt(int data) {
      this.data = data;
   }

   @Override
   public int getData(int x, int z) {
      return this.data;
   }
}
