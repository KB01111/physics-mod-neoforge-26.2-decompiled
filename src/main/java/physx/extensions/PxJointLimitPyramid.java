package physx.extensions;

import physx.NativeObject;

public class PxJointLimitPyramid extends PxJointLimitParameters {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxJointLimitPyramid() {
   }

   private static native int __sizeOf();

   public static PxJointLimitPyramid wrapPointer(long address) {
      return address != 0L ? new PxJointLimitPyramid(address) : null;
   }

   public static PxJointLimitPyramid arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxJointLimitPyramid(long address) {
      super(address);
   }

   public static PxJointLimitPyramid createAt(long address, float yLimitAngleMin, float yLimitAngleMax, float zLimitAngleMin, float zLimitAngleMax) {
      __placement_new_PxJointLimitPyramid(address, yLimitAngleMin, yLimitAngleMax, zLimitAngleMin, zLimitAngleMax);
      PxJointLimitPyramid createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxJointLimitPyramid createAt(
      T allocator, NativeObject.Allocator<T> allocate, float yLimitAngleMin, float yLimitAngleMax, float zLimitAngleMin, float zLimitAngleMax
   ) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxJointLimitPyramid(address, yLimitAngleMin, yLimitAngleMax, zLimitAngleMin, zLimitAngleMax);
      PxJointLimitPyramid createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxJointLimitPyramid(long var0, float var2, float var3, float var4, float var5);

   public static PxJointLimitPyramid createAt(
      long address, float yLimitAngleMin, float yLimitAngleMax, float zLimitAngleMin, float zLimitAngleMax, PxSpring spring
   ) {
      __placement_new_PxJointLimitPyramid(address, yLimitAngleMin, yLimitAngleMax, zLimitAngleMin, zLimitAngleMax, spring.getAddress());
      PxJointLimitPyramid createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxJointLimitPyramid createAt(
      T allocator, NativeObject.Allocator<T> allocate, float yLimitAngleMin, float yLimitAngleMax, float zLimitAngleMin, float zLimitAngleMax, PxSpring spring
   ) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxJointLimitPyramid(address, yLimitAngleMin, yLimitAngleMax, zLimitAngleMin, zLimitAngleMax, spring.getAddress());
      PxJointLimitPyramid createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxJointLimitPyramid(long var0, float var2, float var3, float var4, float var5, long var6);

   public PxJointLimitPyramid(float yLimitAngleMin, float yLimitAngleMax, float zLimitAngleMin, float zLimitAngleMax) {
      this.address = _PxJointLimitPyramid(yLimitAngleMin, yLimitAngleMax, zLimitAngleMin, zLimitAngleMax);
   }

   private static native long _PxJointLimitPyramid(float var0, float var1, float var2, float var3);

   public PxJointLimitPyramid(float yLimitAngleMin, float yLimitAngleMax, float zLimitAngleMin, float zLimitAngleMax, PxSpring spring) {
      this.address = _PxJointLimitPyramid(yLimitAngleMin, yLimitAngleMax, zLimitAngleMin, zLimitAngleMax, spring.getAddress());
   }

   private static native long _PxJointLimitPyramid(float var0, float var1, float var2, float var3, long var4);

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

   public float getYAngleMin() {
      this.checkNotNull();
      return _getYAngleMin(this.address);
   }

   private static native float _getYAngleMin(long var0);

   public void setYAngleMin(float value) {
      this.checkNotNull();
      _setYAngleMin(this.address, value);
   }

   private static native void _setYAngleMin(long var0, float var2);

   public float getYAngleMax() {
      this.checkNotNull();
      return _getYAngleMax(this.address);
   }

   private static native float _getYAngleMax(long var0);

   public void setYAngleMax(float value) {
      this.checkNotNull();
      _setYAngleMax(this.address, value);
   }

   private static native void _setYAngleMax(long var0, float var2);

   public float getZAngleMin() {
      this.checkNotNull();
      return _getZAngleMin(this.address);
   }

   private static native float _getZAngleMin(long var0);

   public void setZAngleMin(float value) {
      this.checkNotNull();
      _setZAngleMin(this.address, value);
   }

   private static native void _setZAngleMin(long var0, float var2);

   public float getZAngleMax() {
      this.checkNotNull();
      return _getZAngleMax(this.address);
   }

   private static native float _getZAngleMax(long var0);

   public void setZAngleMax(float value) {
      this.checkNotNull();
      _setZAngleMax(this.address, value);
   }

   private static native void _setZAngleMax(long var0, float var2);
}
