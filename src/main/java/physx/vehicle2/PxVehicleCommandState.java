package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleCommandState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleCommandState wrapPointer(long address) {
      return address != 0L ? new PxVehicleCommandState(address) : null;
   }

   public static PxVehicleCommandState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleCommandState(long address) {
      super(address);
   }

   public PxVehicleCommandState() {
      this.address = _PxVehicleCommandState();
   }

   private static native long _PxVehicleCommandState();

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

   public float getBrakes(int index) {
      this.checkNotNull();
      return _getBrakes(this.address, index);
   }

   private static native float _getBrakes(long var0, int var2);

   public void setBrakes(int index, float value) {
      this.checkNotNull();
      _setBrakes(this.address, index, value);
   }

   private static native void _setBrakes(long var0, int var2, float var3);

   public int getNbBrakes() {
      this.checkNotNull();
      return _getNbBrakes(this.address);
   }

   private static native int _getNbBrakes(long var0);

   public void setNbBrakes(int value) {
      this.checkNotNull();
      _setNbBrakes(this.address, value);
   }

   private static native void _setNbBrakes(long var0, int var2);

   public float getThrottle() {
      this.checkNotNull();
      return _getThrottle(this.address);
   }

   private static native float _getThrottle(long var0);

   public void setThrottle(float value) {
      this.checkNotNull();
      _setThrottle(this.address, value);
   }

   private static native void _setThrottle(long var0, float var2);

   public float getSteer() {
      this.checkNotNull();
      return _getSteer(this.address);
   }

   private static native float _getSteer(long var0);

   public void setSteer(float value) {
      this.checkNotNull();
      _setSteer(this.address, value);
   }

   private static native void _setSteer(long var0, float var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
