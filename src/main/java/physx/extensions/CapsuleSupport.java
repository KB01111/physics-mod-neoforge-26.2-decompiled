package physx.extensions;

import physx.NativeObject;

public class CapsuleSupport extends Support {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected CapsuleSupport() {
   }

   private static native int __sizeOf();

   public static CapsuleSupport wrapPointer(long address) {
      return address != 0L ? new CapsuleSupport(address) : null;
   }

   public static CapsuleSupport arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected CapsuleSupport(long address) {
      super(address);
   }

   public static CapsuleSupport createAt(long address, float radius, float halfHeight) {
      __placement_new_CapsuleSupport(address, radius, halfHeight);
      CapsuleSupport createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> CapsuleSupport createAt(T allocator, NativeObject.Allocator<T> allocate, float radius, float halfHeight) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_CapsuleSupport(address, radius, halfHeight);
      CapsuleSupport createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_CapsuleSupport(long var0, float var2, float var3);

   public CapsuleSupport(float radius, float halfHeight) {
      this.address = _CapsuleSupport(radius, halfHeight);
   }

   private static native long _CapsuleSupport(float var0, float var1);

   @Override
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

   public float getRadius() {
      this.checkNotNull();
      return _getRadius(this.address);
   }

   private static native float _getRadius(long var0);

   public void setRadius(float value) {
      this.checkNotNull();
      _setRadius(this.address, value);
   }

   private static native void _setRadius(long var0, float var2);

   public float getHalfHeight() {
      this.checkNotNull();
      return _getHalfHeight(this.address);
   }

   private static native float _getHalfHeight(long var0);

   public void setHalfHeight(float value) {
      this.checkNotNull();
      _setHalfHeight(this.address, value);
   }

   private static native void _setHalfHeight(long var0, float var2);
}
