package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxTransform;
import physx.geometry.PxGeometry;
import physx.physics.PxFilterData;
import physx.physics.PxQueryFilterCallback;
import physx.physics.PxQueryFilterData;
import physx.physics.PxShapeFlags;

public class PhysXIntegrationParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PhysXIntegrationParams wrapPointer(long address) {
      return address != 0L ? new PhysXIntegrationParams(address) : null;
   }

   public static PhysXIntegrationParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PhysXIntegrationParams(long address) {
      super(address);
   }

   public PhysXIntegrationParams() {
      this.address = _PhysXIntegrationParams();
   }

   private static native long _PhysXIntegrationParams();

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

   public PxVehiclePhysXRoadGeometryQueryParams getPhysxRoadGeometryQueryParams() {
      this.checkNotNull();
      return PxVehiclePhysXRoadGeometryQueryParams.wrapPointer(_getPhysxRoadGeometryQueryParams(this.address));
   }

   private static native long _getPhysxRoadGeometryQueryParams(long var0);

   public void setPhysxRoadGeometryQueryParams(PxVehiclePhysXRoadGeometryQueryParams value) {
      this.checkNotNull();
      _setPhysxRoadGeometryQueryParams(this.address, value.getAddress());
   }

   private static native void _setPhysxRoadGeometryQueryParams(long var0, long var2);

   public PxVehiclePhysXMaterialFrictionParams getPhysxMaterialFrictionParams(int index) {
      this.checkNotNull();
      return PxVehiclePhysXMaterialFrictionParams.wrapPointer(_getPhysxMaterialFrictionParams(this.address, index));
   }

   private static native long _getPhysxMaterialFrictionParams(long var0, int var2);

   public void setPhysxMaterialFrictionParams(int index, PxVehiclePhysXMaterialFrictionParams value) {
      this.checkNotNull();
      _setPhysxMaterialFrictionParams(this.address, index, value.getAddress());
   }

   private static native void _setPhysxMaterialFrictionParams(long var0, int var2, long var3);

   public PxVehiclePhysXSuspensionLimitConstraintParams getPhysxSuspensionLimitConstraintParams(int index) {
      this.checkNotNull();
      return PxVehiclePhysXSuspensionLimitConstraintParams.wrapPointer(_getPhysxSuspensionLimitConstraintParams(this.address, index));
   }

   private static native long _getPhysxSuspensionLimitConstraintParams(long var0, int var2);

   public void setPhysxSuspensionLimitConstraintParams(int index, PxVehiclePhysXSuspensionLimitConstraintParams value) {
      this.checkNotNull();
      _setPhysxSuspensionLimitConstraintParams(this.address, index, value.getAddress());
   }

   private static native void _setPhysxSuspensionLimitConstraintParams(long var0, int var2, long var3);

   public PxTransform getPhysxActorCMassLocalPose() {
      this.checkNotNull();
      return PxTransform.wrapPointer(_getPhysxActorCMassLocalPose(this.address));
   }

   private static native long _getPhysxActorCMassLocalPose(long var0);

   public void setPhysxActorCMassLocalPose(PxTransform value) {
      this.checkNotNull();
      _setPhysxActorCMassLocalPose(this.address, value.getAddress());
   }

   private static native void _setPhysxActorCMassLocalPose(long var0, long var2);

   public PxGeometry getPhysxActorGeometry() {
      this.checkNotNull();
      return PxGeometry.wrapPointer(_getPhysxActorGeometry(this.address));
   }

   private static native long _getPhysxActorGeometry(long var0);

   public void setPhysxActorGeometry(PxGeometry value) {
      this.checkNotNull();
      _setPhysxActorGeometry(this.address, value.getAddress());
   }

   private static native void _setPhysxActorGeometry(long var0, long var2);

   public PxTransform getPhysxActorBoxShapeLocalPose() {
      this.checkNotNull();
      return PxTransform.wrapPointer(_getPhysxActorBoxShapeLocalPose(this.address));
   }

   private static native long _getPhysxActorBoxShapeLocalPose(long var0);

   public void setPhysxActorBoxShapeLocalPose(PxTransform value) {
      this.checkNotNull();
      _setPhysxActorBoxShapeLocalPose(this.address, value.getAddress());
   }

   private static native void _setPhysxActorBoxShapeLocalPose(long var0, long var2);

   public PxTransform getPhysxWheelShapeLocalPoses(int index) {
      this.checkNotNull();
      return PxTransform.wrapPointer(_getPhysxWheelShapeLocalPoses(this.address, index));
   }

   private static native long _getPhysxWheelShapeLocalPoses(long var0, int var2);

   public void setPhysxWheelShapeLocalPoses(int index, PxTransform value) {
      this.checkNotNull();
      _setPhysxWheelShapeLocalPoses(this.address, index, value.getAddress());
   }

   private static native void _setPhysxWheelShapeLocalPoses(long var0, int var2, long var3);

   public PxShapeFlags getPhysxActorShapeFlags() {
      this.checkNotNull();
      return PxShapeFlags.wrapPointer(_getPhysxActorShapeFlags(this.address));
   }

   private static native long _getPhysxActorShapeFlags(long var0);

   public void setPhysxActorShapeFlags(PxShapeFlags value) {
      this.checkNotNull();
      _setPhysxActorShapeFlags(this.address, value.getAddress());
   }

   private static native void _setPhysxActorShapeFlags(long var0, long var2);

   public PxFilterData getPhysxActorSimulationFilterData() {
      this.checkNotNull();
      return PxFilterData.wrapPointer(_getPhysxActorSimulationFilterData(this.address));
   }

   private static native long _getPhysxActorSimulationFilterData(long var0);

   public void setPhysxActorSimulationFilterData(PxFilterData value) {
      this.checkNotNull();
      _setPhysxActorSimulationFilterData(this.address, value.getAddress());
   }

   private static native void _setPhysxActorSimulationFilterData(long var0, long var2);

   public PxFilterData getPhysxActorQueryFilterData() {
      this.checkNotNull();
      return PxFilterData.wrapPointer(_getPhysxActorQueryFilterData(this.address));
   }

   private static native long _getPhysxActorQueryFilterData(long var0);

   public void setPhysxActorQueryFilterData(PxFilterData value) {
      this.checkNotNull();
      _setPhysxActorQueryFilterData(this.address, value.getAddress());
   }

   private static native void _setPhysxActorQueryFilterData(long var0, long var2);

   public PxShapeFlags getPhysxActorWheelShapeFlags() {
      this.checkNotNull();
      return PxShapeFlags.wrapPointer(_getPhysxActorWheelShapeFlags(this.address));
   }

   private static native long _getPhysxActorWheelShapeFlags(long var0);

   public void setPhysxActorWheelShapeFlags(PxShapeFlags value) {
      this.checkNotNull();
      _setPhysxActorWheelShapeFlags(this.address, value.getAddress());
   }

   private static native void _setPhysxActorWheelShapeFlags(long var0, long var2);

   public PxFilterData getPhysxActorWheelSimulationFilterData() {
      this.checkNotNull();
      return PxFilterData.wrapPointer(_getPhysxActorWheelSimulationFilterData(this.address));
   }

   private static native long _getPhysxActorWheelSimulationFilterData(long var0);

   public void setPhysxActorWheelSimulationFilterData(PxFilterData value) {
      this.checkNotNull();
      _setPhysxActorWheelSimulationFilterData(this.address, value.getAddress());
   }

   private static native void _setPhysxActorWheelSimulationFilterData(long var0, long var2);

   public PxFilterData getPhysxActorWheelQueryFilterData() {
      this.checkNotNull();
      return PxFilterData.wrapPointer(_getPhysxActorWheelQueryFilterData(this.address));
   }

   private static native long _getPhysxActorWheelQueryFilterData(long var0);

   public void setPhysxActorWheelQueryFilterData(PxFilterData value) {
      this.checkNotNull();
      _setPhysxActorWheelQueryFilterData(this.address, value.getAddress());
   }

   private static native void _setPhysxActorWheelQueryFilterData(long var0, long var2);

   public PhysXIntegrationParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
      this.checkNotNull();
      return wrapPointer(_transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
   }

   private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

   public boolean isValid(PxVehicleAxleDescription axleDesc) {
      this.checkNotNull();
      return _isValid(this.address, axleDesc.getAddress());
   }

   private static native boolean _isValid(long var0, long var2);

   public void create(
      PxVehicleAxleDescription axleDesc,
      PxQueryFilterData roadQueryFilterData,
      PxQueryFilterCallback roadQueryFilterCallback,
      PxVehiclePhysXMaterialFriction materialFrictions,
      int nbMaterialFrictions,
      float defaultFriction,
      PxTransform physxActorCMassLocalPose,
      PxGeometry actorGeometry,
      PxTransform physxActorBoxShapeLocalPose,
      PxVehiclePhysXRoadGeometryQueryTypeEnum roadGeometryQueryType
   ) {
      this.checkNotNull();
      _create(
         this.address,
         axleDesc.getAddress(),
         roadQueryFilterData.getAddress(),
         roadQueryFilterCallback != null ? roadQueryFilterCallback.getAddress() : 0L,
         materialFrictions.getAddress(),
         nbMaterialFrictions,
         defaultFriction,
         physxActorCMassLocalPose.getAddress(),
         actorGeometry.getAddress(),
         physxActorBoxShapeLocalPose.getAddress(),
         roadGeometryQueryType.value
      );
   }

   private static native void _create(
      long var0, long var2, long var4, long var6, long var8, int var10, float var11, long var12, long var14, long var16, int var18
   );
}
