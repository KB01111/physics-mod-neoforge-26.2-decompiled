package physx.vehicle2;

import physx.geometry.PxConvexMesh;
import physx.physics.PxScene;

public class PxVehiclePhysXSimulationContext extends PxVehicleSimulationContext {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehiclePhysXSimulationContext wrapPointer(long address) {
      return address != 0L ? new PxVehiclePhysXSimulationContext(address) : null;
   }

   public static PxVehiclePhysXSimulationContext arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehiclePhysXSimulationContext(long address) {
      super(address);
   }

   public PxVehiclePhysXSimulationContext() {
      this.address = _PxVehiclePhysXSimulationContext();
   }

   private static native long _PxVehiclePhysXSimulationContext();

   public PxConvexMesh getPhysxUnitCylinderSweepMesh() {
      this.checkNotNull();
      return PxConvexMesh.wrapPointer(_getPhysxUnitCylinderSweepMesh(this.address));
   }

   private static native long _getPhysxUnitCylinderSweepMesh(long var0);

   public void setPhysxUnitCylinderSweepMesh(PxConvexMesh value) {
      this.checkNotNull();
      _setPhysxUnitCylinderSweepMesh(this.address, value.getAddress());
   }

   private static native void _setPhysxUnitCylinderSweepMesh(long var0, long var2);

   public PxScene getPhysxScene() {
      this.checkNotNull();
      return PxScene.wrapPointer(_getPhysxScene(this.address));
   }

   private static native long _getPhysxScene(long var0);

   public void setPhysxScene(PxScene value) {
      this.checkNotNull();
      _setPhysxScene(this.address, value.getAddress());
   }

   private static native void _setPhysxScene(long var0, long var2);

   public PxVehiclePhysXActorUpdateModeEnum getPhysxActorUpdateMode() {
      this.checkNotNull();
      return PxVehiclePhysXActorUpdateModeEnum.forValue(_getPhysxActorUpdateMode(this.address));
   }

   private static native int _getPhysxActorUpdateMode(long var0);

   public void setPhysxActorUpdateMode(PxVehiclePhysXActorUpdateModeEnum value) {
      this.checkNotNull();
      _setPhysxActorUpdateMode(this.address, value.value);
   }

   private static native void _setPhysxActorUpdateMode(long var0, int var2);

   public float getPhysxActorWakeCounterResetValue() {
      this.checkNotNull();
      return _getPhysxActorWakeCounterResetValue(this.address);
   }

   private static native float _getPhysxActorWakeCounterResetValue(long var0);

   public void setPhysxActorWakeCounterResetValue(float value) {
      this.checkNotNull();
      _setPhysxActorWakeCounterResetValue(this.address, value);
   }

   private static native void _setPhysxActorWakeCounterResetValue(long var0, float var2);

   public float getPhysxActorWakeCounterThreshold() {
      this.checkNotNull();
      return _getPhysxActorWakeCounterThreshold(this.address);
   }

   private static native float _getPhysxActorWakeCounterThreshold(long var0);

   public void setPhysxActorWakeCounterThreshold(float value) {
      this.checkNotNull();
      _setPhysxActorWakeCounterThreshold(this.address, value);
   }

   private static native void _setPhysxActorWakeCounterThreshold(long var0, float var2);

   public PxVehiclePhysXSimulationContext transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
      this.checkNotNull();
      return wrapPointer(_transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
   }

   private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);
}
