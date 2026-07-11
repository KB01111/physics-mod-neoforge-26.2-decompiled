package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxVec4;

public class PxConeLimitParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxConeLimitParams() {
   }

   private static native int __sizeOf();

   public static PxConeLimitParams wrapPointer(long address) {
      return address != 0L ? new PxConeLimitParams(address) : null;
   }

   public static PxConeLimitParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxConeLimitParams(long address) {
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

   public PxVec4 getLowHighLimits() {
      this.checkNotNull();
      return PxVec4.wrapPointer(_getLowHighLimits(this.address));
   }

   private static native long _getLowHighLimits(long var0);

   public void setLowHighLimits(PxVec4 value) {
      this.checkNotNull();
      _setLowHighLimits(this.address, value.getAddress());
   }

   private static native void _setLowHighLimits(long var0, long var2);

   public PxVec4 getAxisAngle() {
      this.checkNotNull();
      return PxVec4.wrapPointer(_getAxisAngle(this.address));
   }

   private static native long _getAxisAngle(long var0);

   public void setAxisAngle(PxVec4 value) {
      this.checkNotNull();
      _setAxisAngle(this.address, value.getAddress());
   }

   private static native void _setAxisAngle(long var0, long var2);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxConeLimitParams");
   }
}
