package physx.physics;

import physx.NativeObject;

public class PxQueryHit extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxQueryHit() {
   }

   private static native int __sizeOf();

   public static PxQueryHit wrapPointer(long address) {
      return address != 0L ? new PxQueryHit(address) : null;
   }

   public static PxQueryHit arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxQueryHit(long address) {
      super(address);
   }

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

   public int getFaceIndex() {
      this.checkNotNull();
      return _getFaceIndex(this.address);
   }

   private static native int _getFaceIndex(long var0);

   public void setFaceIndex(int value) {
      this.checkNotNull();
      _setFaceIndex(this.address, value);
   }

   private static native void _setFaceIndex(long var0, int var2);
}
