package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxFoundation;
import physx.cooking.PxCookingParams;
import physx.geometry.PxConvexMesh;
import physx.physics.PxPhysics;
import physx.support.PxArray_PxReal;
import physx.support.PxArray_PxVec3;

public class PxVehicleTopLevelFunctions extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxVehicleTopLevelFunctions() {
   }

   private static native int __sizeOf();

   public static PxVehicleTopLevelFunctions wrapPointer(long address) {
      return address != 0L ? new PxVehicleTopLevelFunctions(address) : null;
   }

   public static PxVehicleTopLevelFunctions arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleTopLevelFunctions(long address) {
      super(address);
   }

   public static int getMAX_NB_ENGINE_TORQUE_CURVE_ENTRIES() {
      return _getMAX_NB_ENGINE_TORQUE_CURVE_ENTRIES();
   }

   private static native int _getMAX_NB_ENGINE_TORQUE_CURVE_ENTRIES();

   public static boolean InitVehicleExtension(PxFoundation foundation) {
      return _InitVehicleExtension(foundation.getAddress());
   }

   private static native boolean _InitVehicleExtension(long var0);

   public static void CloseVehicleExtension() {
      _CloseVehicleExtension();
   }

   private static native void _CloseVehicleExtension();

   public static boolean VehicleComputeSprungMasses(
      int nbSprungMasses, PxArray_PxVec3 sprungMassCoordinates, float totalMass, PxVehicleAxesEnum gravityDirection, PxArray_PxReal sprungMasses
   ) {
      return _VehicleComputeSprungMasses(nbSprungMasses, sprungMassCoordinates.getAddress(), totalMass, gravityDirection.value, sprungMasses.getAddress());
   }

   private static native boolean _VehicleComputeSprungMasses(int var0, long var1, float var3, int var4, long var5);

   public static PxConvexMesh VehicleUnitCylinderSweepMeshCreate(PxVehicleFrame vehicleFrame, PxPhysics physics, PxCookingParams params) {
      return PxConvexMesh.wrapPointer(_VehicleUnitCylinderSweepMeshCreate(vehicleFrame.getAddress(), physics.getAddress(), params.getAddress()));
   }

   private static native long _VehicleUnitCylinderSweepMeshCreate(long var0, long var2, long var4);

   public static void VehicleUnitCylinderSweepMeshDestroy(PxConvexMesh mesh) {
      _VehicleUnitCylinderSweepMeshDestroy(mesh.getAddress());
   }

   private static native void _VehicleUnitCylinderSweepMeshDestroy(long var0);
}
