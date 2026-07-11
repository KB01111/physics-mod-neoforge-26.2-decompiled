package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxArray_PxParticleRigidFilterPair extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxArray_PxParticleRigidFilterPair wrapPointer(long address) {
      return address != 0L ? new PxArray_PxParticleRigidFilterPair(address) : null;
   }

   public static PxArray_PxParticleRigidFilterPair arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxArray_PxParticleRigidFilterPair(long address) {
      super(address);
   }

   public PxArray_PxParticleRigidFilterPair() {
      this.address = _PxArray_PxParticleRigidFilterPair();
   }

   private static native long _PxArray_PxParticleRigidFilterPair();

   public PxArray_PxParticleRigidFilterPair(int size) {
      this.address = _PxArray_PxParticleRigidFilterPair(size);
   }

   private static native long _PxArray_PxParticleRigidFilterPair(int var0);

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

   public PxParticleRigidFilterPair get(int index) {
      this.checkNotNull();
      return PxParticleRigidFilterPair.wrapPointer(_get(this.address, index));
   }

   private static native long _get(long var0, int var2);

   public void set(int index, PxParticleRigidFilterPair value) {
      this.checkNotNull();
      _set(this.address, index, value.getAddress());
   }

   private static native void _set(long var0, int var2, long var3);

   public PxParticleRigidFilterPair begin() {
      this.checkNotNull();
      return PxParticleRigidFilterPair.wrapPointer(_begin(this.address));
   }

   private static native long _begin(long var0);

   public int size() {
      this.checkNotNull();
      return _size(this.address);
   }

   private static native int _size(long var0);

   public void pushBack(PxParticleRigidFilterPair value) {
      this.checkNotNull();
      _pushBack(this.address, value.getAddress());
   }

   private static native void _pushBack(long var0, long var2);

   public void clear() {
      this.checkNotNull();
      _clear(this.address);
   }

   private static native void _clear(long var0);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxArray_PxParticleRigidFilterPair");
   }
}
