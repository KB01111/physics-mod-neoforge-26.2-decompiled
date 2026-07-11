package physx.character;

import physx.common.PxVec3;

public class PxBoxObstacle extends PxObstacle {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxBoxObstacle wrapPointer(long address) {
      return address != 0L ? new PxBoxObstacle(address) : null;
   }

   public static PxBoxObstacle arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxBoxObstacle(long address) {
      super(address);
   }

   public PxBoxObstacle() {
      this.address = _PxBoxObstacle();
   }

   private static native long _PxBoxObstacle();

   @Override
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

   public PxVec3 getMHalfExtents() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getMHalfExtents(this.address));
   }

   private static native long _getMHalfExtents(long var0);

   public void setMHalfExtents(PxVec3 value) {
      this.checkNotNull();
      _setMHalfExtents(this.address, value.getAddress());
   }

   private static native void _setMHalfExtents(long var0, long var2);
}
