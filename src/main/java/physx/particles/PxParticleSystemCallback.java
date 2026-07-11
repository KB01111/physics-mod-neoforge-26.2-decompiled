package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.CUstream;

public class PxParticleSystemCallback extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxParticleSystemCallback() {
   }

   private static native int __sizeOf();

   public static PxParticleSystemCallback wrapPointer(long address) {
      return address != 0L ? new PxParticleSystemCallback(address) : null;
   }

   public static PxParticleSystemCallback arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxParticleSystemCallback(long address) {
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

   public void onBegin(PxGpuMirroredGpuParticleSystemPointer gpuParticleSystem, CUstream stream) {
      this.checkNotNull();
      _onBegin(this.address, gpuParticleSystem.getAddress(), stream.getAddress());
   }

   private static native void _onBegin(long var0, long var2, long var4);

   public void onAdvance(PxGpuMirroredGpuParticleSystemPointer gpuParticleSystem, CUstream stream) {
      this.checkNotNull();
      _onAdvance(this.address, gpuParticleSystem.getAddress(), stream.getAddress());
   }

   private static native void _onAdvance(long var0, long var2, long var4);

   public void onPostSolve(PxGpuMirroredGpuParticleSystemPointer gpuParticleSystem, CUstream stream) {
      this.checkNotNull();
      _onPostSolve(this.address, gpuParticleSystem.getAddress(), stream.getAddress());
   }

   private static native void _onPostSolve(long var0, long var2, long var4);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleSystemCallback");
   }
}
