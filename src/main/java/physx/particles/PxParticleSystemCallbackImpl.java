package physx.particles;

import physx.PlatformChecks;
import physx.common.CUstream;

public class PxParticleSystemCallbackImpl extends PxParticleSystemCallback {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxParticleSystemCallbackImpl wrapPointer(long address) {
      return address != 0L ? new PxParticleSystemCallbackImpl(address) : null;
   }

   public static PxParticleSystemCallbackImpl arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxParticleSystemCallbackImpl(long address) {
      super(address);
   }

   protected PxParticleSystemCallbackImpl() {
      this.address = this._PxParticleSystemCallbackImpl();
   }

   private native long _PxParticleSystemCallbackImpl();

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

   private void _onBegin(long gpuParticleSystem, long stream) {
      this.onBegin(PxGpuMirroredGpuParticleSystemPointer.wrapPointer(gpuParticleSystem), CUstream.wrapPointer(stream));
   }

   @Override
   public void onBegin(PxGpuMirroredGpuParticleSystemPointer gpuParticleSystem, CUstream stream) {
   }

   private void _onAdvance(long gpuParticleSystem, long stream) {
      this.onAdvance(PxGpuMirroredGpuParticleSystemPointer.wrapPointer(gpuParticleSystem), CUstream.wrapPointer(stream));
   }

   @Override
   public void onAdvance(PxGpuMirroredGpuParticleSystemPointer gpuParticleSystem, CUstream stream) {
   }

   private void _onPostSolve(long gpuParticleSystem, long stream) {
      this.onPostSolve(PxGpuMirroredGpuParticleSystemPointer.wrapPointer(gpuParticleSystem), CUstream.wrapPointer(stream));
   }

   @Override
   public void onPostSolve(PxGpuMirroredGpuParticleSystemPointer gpuParticleSystem, CUstream stream) {
   }

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleSystemCallbackImpl");
   }
}
