package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxVec4;
import physx.support.PxU32Ptr;

public class PxParticleBufferDesc extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxParticleBufferDesc wrapPointer(long address) {
      return address != 0L ? new PxParticleBufferDesc(address) : null;
   }

   public static PxParticleBufferDesc arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxParticleBufferDesc(long address) {
      super(address);
   }

   public static PxParticleBufferDesc createAt(long address) {
      __placement_new_PxParticleBufferDesc(address);
      PxParticleBufferDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxParticleBufferDesc createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxParticleBufferDesc(address);
      PxParticleBufferDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxParticleBufferDesc(long var0);

   public PxParticleBufferDesc() {
      this.address = _PxParticleBufferDesc();
   }

   private static native long _PxParticleBufferDesc();

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

   public PxVec4 getPositions() {
      this.checkNotNull();
      return PxVec4.wrapPointer(_getPositions(this.address));
   }

   private static native long _getPositions(long var0);

   public void setPositions(PxVec4 value) {
      this.checkNotNull();
      _setPositions(this.address, value.getAddress());
   }

   private static native void _setPositions(long var0, long var2);

   public PxVec4 getVelocities() {
      this.checkNotNull();
      return PxVec4.wrapPointer(_getVelocities(this.address));
   }

   private static native long _getVelocities(long var0);

   public void setVelocities(PxVec4 value) {
      this.checkNotNull();
      _setVelocities(this.address, value.getAddress());
   }

   private static native void _setVelocities(long var0, long var2);

   public PxU32Ptr getPhases() {
      this.checkNotNull();
      return PxU32Ptr.wrapPointer(_getPhases(this.address));
   }

   private static native long _getPhases(long var0);

   public void setPhases(PxU32Ptr value) {
      this.checkNotNull();
      _setPhases(this.address, value.getAddress());
   }

   private static native void _setPhases(long var0, long var2);

   public PxParticleVolume getVolumes() {
      this.checkNotNull();
      return PxParticleVolume.wrapPointer(_getVolumes(this.address));
   }

   private static native long _getVolumes(long var0);

   public void setVolumes(PxParticleVolume value) {
      this.checkNotNull();
      _setVolumes(this.address, value.getAddress());
   }

   private static native void _setVolumes(long var0, long var2);

   public int getNumActiveParticles() {
      this.checkNotNull();
      return _getNumActiveParticles(this.address);
   }

   private static native int _getNumActiveParticles(long var0);

   public void setNumActiveParticles(int value) {
      this.checkNotNull();
      _setNumActiveParticles(this.address, value);
   }

   private static native void _setNumActiveParticles(long var0, int var2);

   public int getMaxParticles() {
      this.checkNotNull();
      return _getMaxParticles(this.address);
   }

   private static native int _getMaxParticles(long var0);

   public void setMaxParticles(int value) {
      this.checkNotNull();
      _setMaxParticles(this.address, value);
   }

   private static native void _setMaxParticles(long var0, int var2);

   public int getNumVolumes() {
      this.checkNotNull();
      return _getNumVolumes(this.address);
   }

   private static native int _getNumVolumes(long var0);

   public void setNumVolumes(int value) {
      this.checkNotNull();
      _setNumVolumes(this.address, value);
   }

   private static native void _setNumVolumes(long var0, int var2);

   public int getMaxVolumes() {
      this.checkNotNull();
      return _getMaxVolumes(this.address);
   }

   private static native int _getMaxVolumes(long var0);

   public void setMaxVolumes(int value) {
      this.checkNotNull();
      _setMaxVolumes(this.address, value);
   }

   private static native void _setMaxVolumes(long var0, int var2);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleBufferDesc");
   }
}
