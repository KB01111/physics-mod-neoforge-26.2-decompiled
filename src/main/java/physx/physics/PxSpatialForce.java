package physx.physics;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxSpatialForce extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxSpatialForce() {
   }

   private static native int __sizeOf();

   public static PxSpatialForce wrapPointer(long address) {
      return address != 0L ? new PxSpatialForce(address) : null;
   }

   public static PxSpatialForce arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxSpatialForce(long address) {
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

   public PxVec3 getForce() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getForce(this.address));
   }

   private static native long _getForce(long var0);

   public void setForce(PxVec3 value) {
      this.checkNotNull();
      _setForce(this.address, value.getAddress());
   }

   private static native void _setForce(long var0, long var2);

   public PxVec3 getTorque() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getTorque(this.address));
   }

   private static native long _getTorque(long var0);

   public void setTorque(PxVec3 value) {
      this.checkNotNull();
      _setTorque(this.address, value.getAddress());
   }

   private static native void _setTorque(long var0, long var2);
}
