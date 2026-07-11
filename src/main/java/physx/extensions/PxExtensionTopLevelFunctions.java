package physx.extensions;

import physx.NativeObject;
import physx.common.PxPlane;
import physx.physics.PxFilterData;
import physx.physics.PxMaterial;
import physx.physics.PxPhysics;
import physx.physics.PxRigidStatic;

public class PxExtensionTopLevelFunctions extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxExtensionTopLevelFunctions() {
   }

   private static native int __sizeOf();

   public static PxExtensionTopLevelFunctions wrapPointer(long address) {
      return address != 0L ? new PxExtensionTopLevelFunctions(address) : null;
   }

   public static PxExtensionTopLevelFunctions arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxExtensionTopLevelFunctions(long address) {
      super(address);
   }

   public static PxRigidStatic CreatePlane(PxPhysics sdk, PxPlane plane, PxMaterial material, PxFilterData filterData) {
      return PxRigidStatic.wrapPointer(_CreatePlane(sdk.getAddress(), plane.getAddress(), material.getAddress(), filterData.getAddress()));
   }

   private static native long _CreatePlane(long var0, long var2, long var4, long var6);
}
