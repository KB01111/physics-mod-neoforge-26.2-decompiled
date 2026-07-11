package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxVehicleSimulationContext extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleSimulationContext wrapPointer(long address) {
      return address != 0L ? new PxVehicleSimulationContext(address) : null;
   }

   public static PxVehicleSimulationContext arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleSimulationContext(long address) {
      super(address);
   }

   public PxVehicleSimulationContext() {
      this.address = _PxVehicleSimulationContext();
   }

   private static native long _PxVehicleSimulationContext();

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

   public PxVec3 getGravity() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getGravity(this.address));
   }

   private static native long _getGravity(long var0);

   public void setGravity(PxVec3 value) {
      this.checkNotNull();
      _setGravity(this.address, value.getAddress());
   }

   private static native void _setGravity(long var0, long var2);

   public PxVehicleFrame getFrame() {
      this.checkNotNull();
      return PxVehicleFrame.wrapPointer(_getFrame(this.address));
   }

   private static native long _getFrame(long var0);

   public void setFrame(PxVehicleFrame value) {
      this.checkNotNull();
      _setFrame(this.address, value.getAddress());
   }

   private static native void _setFrame(long var0, long var2);

   public PxVehicleScale getScale() {
      this.checkNotNull();
      return PxVehicleScale.wrapPointer(_getScale(this.address));
   }

   private static native long _getScale(long var0);

   public void setScale(PxVehicleScale value) {
      this.checkNotNull();
      _setScale(this.address, value.getAddress());
   }

   private static native void _setScale(long var0, long var2);

   public PxVehicleTireSlipParams getTireSlipParams() {
      this.checkNotNull();
      return PxVehicleTireSlipParams.wrapPointer(_getTireSlipParams(this.address));
   }

   private static native long _getTireSlipParams(long var0);

   public void setTireSlipParams(PxVehicleTireSlipParams value) {
      this.checkNotNull();
      _setTireSlipParams(this.address, value.getAddress());
   }

   private static native void _setTireSlipParams(long var0, long var2);

   public PxVehicleTireStickyParams getTireStickyParams() {
      this.checkNotNull();
      return PxVehicleTireStickyParams.wrapPointer(_getTireStickyParams(this.address));
   }

   private static native long _getTireStickyParams(long var0);

   public void setTireStickyParams(PxVehicleTireStickyParams value) {
      this.checkNotNull();
      _setTireStickyParams(this.address, value.getAddress());
   }

   private static native void _setTireStickyParams(long var0, long var2);

   public float getThresholdForwardSpeedForWheelAngleIntegration() {
      this.checkNotNull();
      return _getThresholdForwardSpeedForWheelAngleIntegration(this.address);
   }

   private static native float _getThresholdForwardSpeedForWheelAngleIntegration(long var0);

   public void setThresholdForwardSpeedForWheelAngleIntegration(float value) {
      this.checkNotNull();
      _setThresholdForwardSpeedForWheelAngleIntegration(this.address, value);
   }

   private static native void _setThresholdForwardSpeedForWheelAngleIntegration(long var0, float var2);

   public PxVehiclePvdContext getPvdContext() {
      this.checkNotNull();
      return PxVehiclePvdContext.wrapPointer(_getPvdContext(this.address));
   }

   private static native long _getPvdContext(long var0);

   public void setPvdContext(PxVehiclePvdContext value) {
      this.checkNotNull();
      _setPvdContext(this.address, value.getAddress());
   }

   private static native void _setPvdContext(long var0, long var2);

   public PxVehicleSimulationContextTypeEnum getType() {
      this.checkNotNull();
      return PxVehicleSimulationContextTypeEnum.forValue(_getType(this.address));
   }

   private static native int _getType(long var0);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);

   public PxVehicleSimulationContext transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
      this.checkNotNull();
      return wrapPointer(_transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
   }

   private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);
}
