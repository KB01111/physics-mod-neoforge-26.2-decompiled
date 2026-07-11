package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleTireStickyState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleTireStickyState wrapPointer(long address) {
      return address != 0L ? new PxVehicleTireStickyState(address) : null;
   }

   public static PxVehicleTireStickyState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleTireStickyState(long address) {
      super(address);
   }

   public PxVehicleTireStickyState() {
      this.address = _PxVehicleTireStickyState();
   }

   private static native long _PxVehicleTireStickyState();

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

   public float getLowSpeedTime(int index) {
      this.checkNotNull();
      return _getLowSpeedTime(this.address, index);
   }

   private static native float _getLowSpeedTime(long var0, int var2);

   public void setLowSpeedTime(int index, float value) {
      this.checkNotNull();
      _setLowSpeedTime(this.address, index, value);
   }

   private static native void _setLowSpeedTime(long var0, int var2, float var3);

   public boolean getActiveStatus(int index) {
      this.checkNotNull();
      return _getActiveStatus(this.address, index);
   }

   private static native boolean _getActiveStatus(long var0, int var2);

   public void setActiveStatus(int index, boolean value) {
      this.checkNotNull();
      _setActiveStatus(this.address, index, value);
   }

   private static native void _setActiveStatus(long var0, int var2, boolean var3);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
