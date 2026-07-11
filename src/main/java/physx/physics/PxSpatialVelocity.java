package physx.physics;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxSpatialVelocity extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxSpatialVelocity() {
   }

   private static native int __sizeOf();

   public static PxSpatialVelocity wrapPointer(long address) {
      return address != 0L ? new PxSpatialVelocity(address) : null;
   }

   public static PxSpatialVelocity arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxSpatialVelocity(long address) {
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

   public PxVec3 getLinear() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getLinear(this.address));
   }

   private static native long _getLinear(long var0);

   public void setLinear(PxVec3 value) {
      this.checkNotNull();
      _setLinear(this.address, value.getAddress());
   }

   private static native void _setLinear(long var0, long var2);

   public PxVec3 getAngular() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getAngular(this.address));
   }

   private static native long _getAngular(long var0);

   public void setAngular(PxVec3 value) {
      this.checkNotNull();
      _setAngular(this.address, value.getAddress());
   }

   private static native void _setAngular(long var0, long var2);
}
