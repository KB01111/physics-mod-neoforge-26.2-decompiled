package physx.extensions;

import physx.NativeObject;

public class PxJointLinearLimitPair extends PxJointLimitParameters {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxJointLinearLimitPair() {
   }

   private static native int __sizeOf();

   public static PxJointLinearLimitPair wrapPointer(long address) {
      return address != 0L ? new PxJointLinearLimitPair(address) : null;
   }

   public static PxJointLinearLimitPair arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxJointLinearLimitPair(long address) {
      super(address);
   }

   public static PxJointLinearLimitPair createAt(long address, float lowerLimit, float upperLimit, PxSpring spring) {
      __placement_new_PxJointLinearLimitPair(address, lowerLimit, upperLimit, spring.getAddress());
      PxJointLinearLimitPair createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxJointLinearLimitPair createAt(T allocator, NativeObject.Allocator<T> allocate, float lowerLimit, float upperLimit, PxSpring spring) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxJointLinearLimitPair(address, lowerLimit, upperLimit, spring.getAddress());
      PxJointLinearLimitPair createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxJointLinearLimitPair(long var0, float var2, float var3, long var4);

   public PxJointLinearLimitPair(float lowerLimit, float upperLimit, PxSpring spring) {
      this.address = _PxJointLinearLimitPair(lowerLimit, upperLimit, spring.getAddress());
   }

   private static native long _PxJointLinearLimitPair(float var0, float var1, long var2);

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

   public float getUpper() {
      this.checkNotNull();
      return _getUpper(this.address);
   }

   private static native float _getUpper(long var0);

   public void setUpper(float value) {
      this.checkNotNull();
      _setUpper(this.address, value);
   }

   private static native void _setUpper(long var0, float var2);

   public float getLower() {
      this.checkNotNull();
      return _getLower(this.address);
   }

   private static native float _getLower(long var0);

   public void setLower(float value) {
      this.checkNotNull();
      _setLower(this.address, value);
   }

   private static native void _setLower(long var0, float var2);
}
