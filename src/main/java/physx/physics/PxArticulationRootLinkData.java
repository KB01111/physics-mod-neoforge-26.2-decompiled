package physx.physics;

import physx.NativeObject;
import physx.common.PxTransform;
import physx.common.PxVec3;

public class PxArticulationRootLinkData extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxArticulationRootLinkData wrapPointer(long address) {
      return address != 0L ? new PxArticulationRootLinkData(address) : null;
   }

   public static PxArticulationRootLinkData arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxArticulationRootLinkData(long address) {
      super(address);
   }

   public PxArticulationRootLinkData() {
      this.address = _PxArticulationRootLinkData();
   }

   private static native long _PxArticulationRootLinkData();

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

   public PxTransform getTransform() {
      this.checkNotNull();
      return PxTransform.wrapPointer(_getTransform(this.address));
   }

   private static native long _getTransform(long var0);

   public void setTransform(PxTransform value) {
      this.checkNotNull();
      _setTransform(this.address, value.getAddress());
   }

   private static native void _setTransform(long var0, long var2);

   public PxVec3 getWorldLinVel() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getWorldLinVel(this.address));
   }

   private static native long _getWorldLinVel(long var0);

   public void setWorldLinVel(PxVec3 value) {
      this.checkNotNull();
      _setWorldLinVel(this.address, value.getAddress());
   }

   private static native void _setWorldLinVel(long var0, long var2);

   public PxVec3 getWorldAngVel() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getWorldAngVel(this.address));
   }

   private static native long _getWorldAngVel(long var0);

   public void setWorldAngVel(PxVec3 value) {
      this.checkNotNull();
      _setWorldAngVel(this.address, value.getAddress());
   }

   private static native void _setWorldAngVel(long var0, long var2);

   public PxVec3 getWorldLinAccel() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getWorldLinAccel(this.address));
   }

   private static native long _getWorldLinAccel(long var0);

   public void setWorldLinAccel(PxVec3 value) {
      this.checkNotNull();
      _setWorldLinAccel(this.address, value.getAddress());
   }

   private static native void _setWorldLinAccel(long var0, long var2);

   public PxVec3 getWorldAngAccel() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getWorldAngAccel(this.address));
   }

   private static native long _getWorldAngAccel(long var0);

   public void setWorldAngAccel(PxVec3 value) {
      this.checkNotNull();
      _setWorldAngAccel(this.address, value.getAddress());
   }

   private static native void _setWorldAngAccel(long var0, long var2);
}
