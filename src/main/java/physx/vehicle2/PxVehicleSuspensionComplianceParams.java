package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleSuspensionComplianceParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleSuspensionComplianceParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleSuspensionComplianceParams(address) : null;
   }

   public static PxVehicleSuspensionComplianceParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleSuspensionComplianceParams(long address) {
      super(address);
   }

   public PxVehicleSuspensionComplianceParams() {
      this.address = _PxVehicleSuspensionComplianceParams();
   }

   private static native long _PxVehicleSuspensionComplianceParams();

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

   public PxVehicleFixedSizeLookupTableFloat_3 getWheelToeAngle() {
      this.checkNotNull();
      return PxVehicleFixedSizeLookupTableFloat_3.wrapPointer(_getWheelToeAngle(this.address));
   }

   private static native long _getWheelToeAngle(long var0);

   public void setWheelToeAngle(PxVehicleFixedSizeLookupTableFloat_3 value) {
      this.checkNotNull();
      _setWheelToeAngle(this.address, value.getAddress());
   }

   private static native void _setWheelToeAngle(long var0, long var2);

   public PxVehicleFixedSizeLookupTableFloat_3 getWheelCamberAngle() {
      this.checkNotNull();
      return PxVehicleFixedSizeLookupTableFloat_3.wrapPointer(_getWheelCamberAngle(this.address));
   }

   private static native long _getWheelCamberAngle(long var0);

   public void setWheelCamberAngle(PxVehicleFixedSizeLookupTableFloat_3 value) {
      this.checkNotNull();
      _setWheelCamberAngle(this.address, value.getAddress());
   }

   private static native void _setWheelCamberAngle(long var0, long var2);

   public PxVehicleFixedSizeLookupTableVec3_3 getSuspForceAppPoint() {
      this.checkNotNull();
      return PxVehicleFixedSizeLookupTableVec3_3.wrapPointer(_getSuspForceAppPoint(this.address));
   }

   private static native long _getSuspForceAppPoint(long var0);

   public void setSuspForceAppPoint(PxVehicleFixedSizeLookupTableVec3_3 value) {
      this.checkNotNull();
      _setSuspForceAppPoint(this.address, value.getAddress());
   }

   private static native void _setSuspForceAppPoint(long var0, long var2);

   public PxVehicleFixedSizeLookupTableVec3_3 getTireForceAppPoint() {
      this.checkNotNull();
      return PxVehicleFixedSizeLookupTableVec3_3.wrapPointer(_getTireForceAppPoint(this.address));
   }

   private static native long _getTireForceAppPoint(long var0);

   public void setTireForceAppPoint(PxVehicleFixedSizeLookupTableVec3_3 value) {
      this.checkNotNull();
      _setTireForceAppPoint(this.address, value.getAddress());
   }

   private static native void _setTireForceAppPoint(long var0, long var2);

   public PxVehicleSuspensionComplianceParams transformAndScale(
      PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale
   ) {
      this.checkNotNull();
      return wrapPointer(_transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
   }

   private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
