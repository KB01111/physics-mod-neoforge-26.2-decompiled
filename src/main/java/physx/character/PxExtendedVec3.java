package physx.character;

import physx.NativeObject;

public class PxExtendedVec3 extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxExtendedVec3 wrapPointer(long address) {
      return address != 0L ? new PxExtendedVec3(address) : null;
   }

   public static PxExtendedVec3 arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxExtendedVec3(long address) {
      super(address);
   }

   public static PxExtendedVec3 createAt(long address) {
      __placement_new_PxExtendedVec3(address);
      PxExtendedVec3 createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxExtendedVec3 createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxExtendedVec3(address);
      PxExtendedVec3 createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxExtendedVec3(long var0);

   public static PxExtendedVec3 createAt(long address, double x, double y, double z) {
      __placement_new_PxExtendedVec3(address, x, y, z);
      PxExtendedVec3 createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxExtendedVec3 createAt(T allocator, NativeObject.Allocator<T> allocate, double x, double y, double z) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxExtendedVec3(address, x, y, z);
      PxExtendedVec3 createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxExtendedVec3(long var0, double var2, double var4, double var6);

   public PxExtendedVec3() {
      this.address = _PxExtendedVec3();
   }

   private static native long _PxExtendedVec3();

   public PxExtendedVec3(double x, double y, double z) {
      this.address = _PxExtendedVec3(x, y, z);
   }

   private static native long _PxExtendedVec3(double var0, double var2, double var4);

   public void destroy() {
      if (this.address == 0L) {
         throw new IllegalStateException(this + " is already deleted");
      } else if (this.isExternallyAllocated) {
         throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
      } else {
         _delete_native_instance(this.address);
         this.address = 0L;
      }
   }

   private static native long _delete_native_instance(long var0);

   public double getX() {
      this.checkNotNull();
      return _getX(this.address);
   }

   private static native double _getX(long var0);

   public void setX(double value) {
      this.checkNotNull();
      _setX(this.address, value);
   }

   private static native void _setX(long var0, double var2);

   public double getY() {
      this.checkNotNull();
      return _getY(this.address);
   }

   private static native double _getY(long var0);

   public void setY(double value) {
      this.checkNotNull();
      _setY(this.address, value);
   }

   private static native void _setY(long var0, double var2);

   public double getZ() {
      this.checkNotNull();
      return _getZ(this.address);
   }

   private static native double _getZ(long var0);

   public void setZ(double value) {
      this.checkNotNull();
      _setZ(this.address, value);
   }

   private static native void _setZ(long var0, double var2);
}
