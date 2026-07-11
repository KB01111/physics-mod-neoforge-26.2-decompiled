package physx.extensions;

import physx.NativeObject;

public class PxJointLimitCone extends PxJointLimitParameters {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxJointLimitCone() {
   }

   private static native int __sizeOf();

   public static PxJointLimitCone wrapPointer(long address) {
      return address != 0L ? new PxJointLimitCone(address) : null;
   }

   public static PxJointLimitCone arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxJointLimitCone(long address) {
      super(address);
   }

   public static PxJointLimitCone createAt(long address, float yLimitAngle, float zLimitAngle) {
      __placement_new_PxJointLimitCone(address, yLimitAngle, zLimitAngle);
      PxJointLimitCone createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxJointLimitCone createAt(T allocator, NativeObject.Allocator<T> allocate, float yLimitAngle, float zLimitAngle) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxJointLimitCone(address, yLimitAngle, zLimitAngle);
      PxJointLimitCone createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxJointLimitCone(long var0, float var2, float var3);

   public static PxJointLimitCone createAt(long address, float yLimitAngle, float zLimitAngle, PxSpring spring) {
      __placement_new_PxJointLimitCone(address, yLimitAngle, zLimitAngle, spring.getAddress());
      PxJointLimitCone createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxJointLimitCone createAt(T allocator, NativeObject.Allocator<T> allocate, float yLimitAngle, float zLimitAngle, PxSpring spring) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxJointLimitCone(address, yLimitAngle, zLimitAngle, spring.getAddress());
      PxJointLimitCone createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxJointLimitCone(long var0, float var2, float var3, long var4);

   public PxJointLimitCone(float yLimitAngle, float zLimitAngle) {
      this.address = _PxJointLimitCone(yLimitAngle, zLimitAngle);
   }

   private static native long _PxJointLimitCone(float var0, float var1);

   public PxJointLimitCone(float yLimitAngle, float zLimitAngle, PxSpring spring) {
      this.address = _PxJointLimitCone(yLimitAngle, zLimitAngle, spring.getAddress());
   }

   private static native long _PxJointLimitCone(float var0, float var1, long var2);

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

   public float getYAngle() {
      this.checkNotNull();
      return _getYAngle(this.address);
   }

   private static native float _getYAngle(long var0);

   public void setYAngle(float value) {
      this.checkNotNull();
      _setYAngle(this.address, value);
   }

   private static native void _setYAngle(long var0, float var2);

   public float getZAngle() {
      this.checkNotNull();
      return _getZAngle(this.address);
   }

   private static native float _getZAngle(long var0);

   public void setZAngle(float value) {
      this.checkNotNull();
      _setZAngle(this.address, value);
   }

   private static native void _setZAngle(long var0, float var2);
}
