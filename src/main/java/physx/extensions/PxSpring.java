package physx.extensions;

import physx.NativeObject;

public class PxSpring extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxSpring() {
   }

   private static native int __sizeOf();

   public static PxSpring wrapPointer(long address) {
      return address != 0L ? new PxSpring(address) : null;
   }

   public static PxSpring arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxSpring(long address) {
      super(address);
   }

   public static PxSpring createAt(long address, float stiffness, float damping) {
      __placement_new_PxSpring(address, stiffness, damping);
      PxSpring createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxSpring createAt(T allocator, NativeObject.Allocator<T> allocate, float stiffness, float damping) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxSpring(address, stiffness, damping);
      PxSpring createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxSpring(long var0, float var2, float var3);

   public PxSpring(float stiffness, float damping) {
      this.address = _PxSpring(stiffness, damping);
   }

   private static native long _PxSpring(float var0, float var1);

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

   public float getStiffness() {
      this.checkNotNull();
      return _getStiffness(this.address);
   }

   private static native float _getStiffness(long var0);

   public void setStiffness(float value) {
      this.checkNotNull();
      _setStiffness(this.address, value);
   }

   private static native void _setStiffness(long var0, float var2);

   public float getDamping() {
      this.checkNotNull();
      return _getDamping(this.address);
   }

   private static native float _getDamping(long var0);

   public void setDamping(float value) {
      this.checkNotNull();
      _setDamping(this.address, value);
   }

   private static native void _setDamping(long var0, float var2);
}
