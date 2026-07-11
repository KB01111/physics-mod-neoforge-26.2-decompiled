package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleAntiRollForceParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleAntiRollForceParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleAntiRollForceParams(address) : null;
   }

   public static PxVehicleAntiRollForceParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleAntiRollForceParams(long address) {
      super(address);
   }

   public PxVehicleAntiRollForceParams() {
      this.address = _PxVehicleAntiRollForceParams();
   }

   private static native long _PxVehicleAntiRollForceParams();

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

   public int getWheel0() {
      this.checkNotNull();
      return _getWheel0(this.address);
   }

   private static native int _getWheel0(long var0);

   public void setWheel0(int value) {
      this.checkNotNull();
      _setWheel0(this.address, value);
   }

   private static native void _setWheel0(long var0, int var2);

   public int getWheel1() {
      this.checkNotNull();
      return _getWheel1(this.address);
   }

   private static native int _getWheel1(long var0);

   public void setWheel1(int value) {
      this.checkNotNull();
      _setWheel1(this.address, value);
   }

   private static native void _setWheel1(long var0, int var2);

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

   public PxVehicleAntiRollForceParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
      this.checkNotNull();
      return wrapPointer(_transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
   }

   private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

   public boolean isValid(PxVehicleAxleDescription axleDesc) {
      this.checkNotNull();
      return _isValid(this.address, axleDesc.getAddress());
   }

   private static native boolean _isValid(long var0, long var2);
}
