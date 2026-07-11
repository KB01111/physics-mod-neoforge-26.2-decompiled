package physx.support;

import physx.NativeObject;
import physx.character.PxController;
import physx.character.PxControllerObstacleHit;
import physx.character.PxControllerShapeHit;
import physx.character.PxControllersHit;
import physx.character.PxObstacle;
import physx.common.PxBounds3;
import physx.common.PxDebugLine;
import physx.common.PxDebugPoint;
import physx.common.PxDebugTriangle;
import physx.common.PxVec3;
import physx.physics.PxActor;
import physx.physics.PxContactPair;
import physx.physics.PxContactPairHeader;
import physx.physics.PxShape;
import physx.physics.PxTriggerPair;

public class NativeArrayHelpers extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected NativeArrayHelpers() {
   }

   private static native int __sizeOf();

   public static NativeArrayHelpers wrapPointer(long address) {
      return address != 0L ? new NativeArrayHelpers(address) : null;
   }

   public static NativeArrayHelpers arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected NativeArrayHelpers(long address) {
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

   public static byte getU8At(PxU8ConstPtr base, int index) {
      return _getU8At(base.getAddress(), index);
   }

   private static native byte _getU8At(long var0, int var2);

   public static short getU16At(PxU16ConstPtr base, int index) {
      return _getU16At(base.getAddress(), index);
   }

   private static native short _getU16At(long var0, int var2);

   public static int getU32At(PxU32ConstPtr base, int index) {
      return _getU32At(base.getAddress(), index);
   }

   private static native int _getU32At(long var0, int var2);

   public static float getRealAt(PxRealPtr base, int index) {
      return _getRealAt(base.getAddress(), index);
   }

   private static native float _getRealAt(long var0, int var2);

   public static void setU8At(NativeObject base, int index, byte value) {
      _setU8At(base.getAddress(), index, value);
   }

   private static native void _setU8At(long var0, int var2, byte var3);

   public static void setU16At(NativeObject base, int index, short value) {
      _setU16At(base.getAddress(), index, value);
   }

   private static native void _setU16At(long var0, int var2, short var3);

   public static void setU32At(NativeObject base, int index, int value) {
      _setU32At(base.getAddress(), index, value);
   }

   private static native void _setU32At(long var0, int var2, int var3);

   public static void setRealAt(NativeObject base, int index, float value) {
      _setRealAt(base.getAddress(), index, value);
   }

   private static native void _setRealAt(long var0, int var2, float var3);

   public static PxU8Ptr voidToU8Ptr(NativeObject voidPtr) {
      return PxU8Ptr.wrapPointer(_voidToU8Ptr(voidPtr.getAddress()));
   }

   private static native long _voidToU8Ptr(long var0);

   public static PxU16Ptr voidToU16Ptr(NativeObject voidPtr) {
      return PxU16Ptr.wrapPointer(_voidToU16Ptr(voidPtr.getAddress()));
   }

   private static native long _voidToU16Ptr(long var0);

   public static PxU32Ptr voidToU32Ptr(NativeObject voidPtr) {
      return PxU32Ptr.wrapPointer(_voidToU32Ptr(voidPtr.getAddress()));
   }

   private static native long _voidToU32Ptr(long var0);

   public static PxI32Ptr voidToI32Ptr(NativeObject voidPtr) {
      return PxI32Ptr.wrapPointer(_voidToI32Ptr(voidPtr.getAddress()));
   }

   private static native long _voidToI32Ptr(long var0);

   public static PxRealPtr voidToRealPtr(NativeObject voidPtr) {
      return PxRealPtr.wrapPointer(_voidToRealPtr(voidPtr.getAddress()));
   }

   private static native long _voidToRealPtr(long var0);

   public static PxActor getActorAt(PxActor base, int index) {
      return PxActor.wrapPointer(_getActorAt(base.getAddress(), index));
   }

   private static native long _getActorAt(long var0, int var2);

   public static PxBounds3 getBounds3At(PxBounds3 base, int index) {
      return PxBounds3.wrapPointer(_getBounds3At(base.getAddress(), index));
   }

   private static native long _getBounds3At(long var0, int var2);

   public static PxContactPair getContactPairAt(PxContactPair base, int index) {
      return PxContactPair.wrapPointer(_getContactPairAt(base.getAddress(), index));
   }

   private static native long _getContactPairAt(long var0, int var2);

   public static PxContactPairHeader getContactPairHeaderAt(PxContactPairHeader base, int index) {
      return PxContactPairHeader.wrapPointer(_getContactPairHeaderAt(base.getAddress(), index));
   }

   private static native long _getContactPairHeaderAt(long var0, int var2);

   public static PxController getControllerAt(PxController base, int index) {
      return PxController.wrapPointer(_getControllerAt(base.getAddress(), index));
   }

   private static native long _getControllerAt(long var0, int var2);

   public static PxControllerShapeHit getControllerShapeHitAt(PxControllerShapeHit base, int index) {
      return PxControllerShapeHit.wrapPointer(_getControllerShapeHitAt(base.getAddress(), index));
   }

   private static native long _getControllerShapeHitAt(long var0, int var2);

   public static PxControllersHit getControllersHitAt(PxControllersHit base, int index) {
      return PxControllersHit.wrapPointer(_getControllersHitAt(base.getAddress(), index));
   }

   private static native long _getControllersHitAt(long var0, int var2);

   public static PxControllerObstacleHit getControllerObstacleHitAt(PxControllerObstacleHit base, int index) {
      return PxControllerObstacleHit.wrapPointer(_getControllerObstacleHitAt(base.getAddress(), index));
   }

   private static native long _getControllerObstacleHitAt(long var0, int var2);

   public static PxDebugPoint getDebugPointAt(PxDebugPoint base, int index) {
      return PxDebugPoint.wrapPointer(_getDebugPointAt(base.getAddress(), index));
   }

   private static native long _getDebugPointAt(long var0, int var2);

   public static PxDebugLine getDebugLineAt(PxDebugLine base, int index) {
      return PxDebugLine.wrapPointer(_getDebugLineAt(base.getAddress(), index));
   }

   private static native long _getDebugLineAt(long var0, int var2);

   public static PxDebugTriangle getDebugTriangleAt(PxDebugTriangle base, int index) {
      return PxDebugTriangle.wrapPointer(_getDebugTriangleAt(base.getAddress(), index));
   }

   private static native long _getDebugTriangleAt(long var0, int var2);

   public static PxObstacle getObstacleAt(PxObstacle base, int index) {
      return PxObstacle.wrapPointer(_getObstacleAt(base.getAddress(), index));
   }

   private static native long _getObstacleAt(long var0, int var2);

   public static PxShape getShapeAt(PxShape base, int index) {
      return PxShape.wrapPointer(_getShapeAt(base.getAddress(), index));
   }

   private static native long _getShapeAt(long var0, int var2);

   public static PxTriggerPair getTriggerPairAt(PxTriggerPair base, int index) {
      return PxTriggerPair.wrapPointer(_getTriggerPairAt(base.getAddress(), index));
   }

   private static native long _getTriggerPairAt(long var0, int var2);

   public static PxVec3 getVec3At(PxVec3 base, int index) {
      return PxVec3.wrapPointer(_getVec3At(base.getAddress(), index));
   }

   private static native long _getVec3At(long var0, int var2);
}
