package physx.character;

import physx.NativeObject;
import physx.common.PxQuat;
import physx.geometry.PxGeometryTypeEnum;

public class PxObstacle extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxObstacle() {
   }

   private static native int __sizeOf();

   public static PxObstacle wrapPointer(long address) {
      return address != 0L ? new PxObstacle(address) : null;
   }

   public static PxObstacle arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxObstacle(long address) {
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

   public NativeObject getMUserData() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_getMUserData(this.address));
   }

   private static native long _getMUserData(long var0);

   public void setMUserData(NativeObject value) {
      this.checkNotNull();
      _setMUserData(this.address, value.getAddress());
   }

   private static native void _setMUserData(long var0, long var2);

   public PxExtendedVec3 getMPos() {
      this.checkNotNull();
      return PxExtendedVec3.wrapPointer(_getMPos(this.address));
   }

   private static native long _getMPos(long var0);

   public void setMPos(PxExtendedVec3 value) {
      this.checkNotNull();
      _setMPos(this.address, value.getAddress());
   }

   private static native void _setMPos(long var0, long var2);

   public PxQuat getMRot() {
      this.checkNotNull();
      return PxQuat.wrapPointer(_getMRot(this.address));
   }

   private static native long _getMRot(long var0);

   public void setMRot(PxQuat value) {
      this.checkNotNull();
      _setMRot(this.address, value.getAddress());
   }

   private static native void _setMRot(long var0, long var2);

   public PxGeometryTypeEnum getType() {
      this.checkNotNull();
      return PxGeometryTypeEnum.forValue(_getType(this.address));
   }

   private static native int _getType(long var0);
}
