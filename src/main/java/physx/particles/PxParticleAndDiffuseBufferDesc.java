package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxParticleAndDiffuseBufferDesc extends PxParticleBufferDesc {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxParticleAndDiffuseBufferDesc wrapPointer(long address) {
      return address != 0L ? new PxParticleAndDiffuseBufferDesc(address) : null;
   }

   public static PxParticleAndDiffuseBufferDesc arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxParticleAndDiffuseBufferDesc(long address) {
      super(address);
   }

   public static PxParticleAndDiffuseBufferDesc createAt(long address) {
      __placement_new_PxParticleAndDiffuseBufferDesc(address);
      PxParticleAndDiffuseBufferDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxParticleAndDiffuseBufferDesc createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxParticleAndDiffuseBufferDesc(address);
      PxParticleAndDiffuseBufferDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxParticleAndDiffuseBufferDesc(long var0);

   public PxParticleAndDiffuseBufferDesc() {
      this.address = _PxParticleAndDiffuseBufferDesc();
   }

   private static native long _PxParticleAndDiffuseBufferDesc();

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

   public PxDiffuseParticleParams getDiffuseParams() {
      this.checkNotNull();
      return PxDiffuseParticleParams.wrapPointer(_getDiffuseParams(this.address));
   }

   private static native long _getDiffuseParams(long var0);

   public void setDiffuseParams(PxDiffuseParticleParams value) {
      this.checkNotNull();
      _setDiffuseParams(this.address, value.getAddress());
   }

   private static native void _setDiffuseParams(long var0, long var2);

   public int getMaxDiffuseParticles() {
      this.checkNotNull();
      return _getMaxDiffuseParticles(this.address);
   }

   private static native int _getMaxDiffuseParticles(long var0);

   public void setMaxDiffuseParticles(int value) {
      this.checkNotNull();
      _setMaxDiffuseParticles(this.address, value);
   }

   private static native void _setMaxDiffuseParticles(long var0, int var2);

   public int getMaxActiveDiffuseParticles() {
      this.checkNotNull();
      return _getMaxActiveDiffuseParticles(this.address);
   }

   private static native int _getMaxActiveDiffuseParticles(long var0);

   public void setMaxActiveDiffuseParticles(int value) {
      this.checkNotNull();
      _setMaxActiveDiffuseParticles(this.address, value);
   }

   private static native void _setMaxActiveDiffuseParticles(long var0, int var2);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleAndDiffuseBufferDesc");
   }
}
