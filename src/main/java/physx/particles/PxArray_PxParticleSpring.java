package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxArray_PxParticleSpring extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxArray_PxParticleSpring wrapPointer(long address) {
      return address != 0L ? new PxArray_PxParticleSpring(address) : null;
   }

   public static PxArray_PxParticleSpring arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxArray_PxParticleSpring(long address) {
      super(address);
   }

   public PxArray_PxParticleSpring() {
      this.address = _PxArray_PxParticleSpring();
   }

   private static native long _PxArray_PxParticleSpring();

   public PxArray_PxParticleSpring(int size) {
      this.address = _PxArray_PxParticleSpring(size);
   }

   private static native long _PxArray_PxParticleSpring(int var0);

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

   public PxParticleSpring get(int index) {
      this.checkNotNull();
      return PxParticleSpring.wrapPointer(_get(this.address, index));
   }

   private static native long _get(long var0, int var2);

   public void set(int index, PxParticleSpring value) {
      this.checkNotNull();
      _set(this.address, index, value.getAddress());
   }

   private static native void _set(long var0, int var2, long var3);

   public PxParticleSpring begin() {
      this.checkNotNull();
      return PxParticleSpring.wrapPointer(_begin(this.address));
   }

   private static native long _begin(long var0);

   public int size() {
      this.checkNotNull();
      return _size(this.address);
   }

   private static native int _size(long var0);

   public void pushBack(PxParticleSpring value) {
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
      PlatformChecks.requirePlatform(3, "physx.particles.PxArray_PxParticleSpring");
   }
}
