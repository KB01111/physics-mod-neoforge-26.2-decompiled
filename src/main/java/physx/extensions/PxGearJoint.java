package physx.extensions;

import physx.common.PxBase;

public class PxGearJoint extends PxJoint {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxGearJoint() {
   }

   private static native int __sizeOf();

   public static PxGearJoint wrapPointer(long address) {
      return address != 0L ? new PxGearJoint(address) : null;
   }

   public static PxGearJoint arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxGearJoint(long address) {
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

   public boolean setHinges(PxBase hinge0, PxBase hinge1) {
      this.checkNotNull();
      return _setHinges(this.address, hinge0.getAddress(), hinge1.getAddress());
   }

   private static native boolean _setHinges(long var0, long var2, long var4);

   public void setGearRatio(float ratio) {
      this.checkNotNull();
      _setGearRatio(this.address, ratio);
   }

   private static native void _setGearRatio(long var0, float var2);

   public float getGearRatio() {
      this.checkNotNull();
      return _getGearRatio(this.address);
   }

   private static native float _getGearRatio(long var0);
}
