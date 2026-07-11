package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxGpuMirroredGpuParticleSystemPointer extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxGpuMirroredGpuParticleSystemPointer() {
   }

   private static native int __sizeOf();

   public static PxGpuMirroredGpuParticleSystemPointer wrapPointer(long address) {
      return address != 0L ? new PxGpuMirroredGpuParticleSystemPointer(address) : null;
   }

   public static PxGpuMirroredGpuParticleSystemPointer arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxGpuMirroredGpuParticleSystemPointer(long address) {
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

   public PxGpuParticleSystem getMDevicePtr() {
      this.checkNotNull();
      return PxGpuParticleSystem.wrapPointer(_getMDevicePtr(this.address));
   }

   private static native long _getMDevicePtr(long var0);

   public void setMDevicePtr(PxGpuParticleSystem value) {
      this.checkNotNull();
      _setMDevicePtr(this.address, value.getAddress());
   }

   private static native void _setMDevicePtr(long var0, long var2);

   public PxGpuParticleSystem getMHostPtr() {
      this.checkNotNull();
      return PxGpuParticleSystem.wrapPointer(_getMHostPtr(this.address));
   }

   private static native long _getMHostPtr(long var0);

   public void setMHostPtr(PxGpuParticleSystem value) {
      this.checkNotNull();
      _setMHostPtr(this.address, value.getAddress());
   }

   private static native void _setMHostPtr(long var0, long var2);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxGpuMirroredGpuParticleSystemPointer");
   }
}
