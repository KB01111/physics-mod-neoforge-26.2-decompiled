package physx.physics;

import physx.NativeObject;

public class PxBroadPhaseCaps extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxBroadPhaseCaps wrapPointer(long address) {
      return address != 0L ? new PxBroadPhaseCaps(address) : null;
   }

   public static PxBroadPhaseCaps arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxBroadPhaseCaps(long address) {
      super(address);
   }

   public PxBroadPhaseCaps() {
      this.address = _PxBroadPhaseCaps();
   }

   private static native long _PxBroadPhaseCaps();

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

   public int getMMaxNbRegions() {
      this.checkNotNull();
      return _getMMaxNbRegions(this.address);
   }

   private static native int _getMMaxNbRegions(long var0);

   public void setMMaxNbRegions(int value) {
      this.checkNotNull();
      _setMMaxNbRegions(this.address, value);
   }

   private static native void _setMMaxNbRegions(long var0, int var2);
}
