package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxParticleSpring extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxParticleSpring wrapPointer(long address) {
      return address != 0L ? new PxParticleSpring(address) : null;
   }

   public static PxParticleSpring arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxParticleSpring(long address) {
      super(address);
   }

   public static PxParticleSpring createAt(long address) {
      __placement_new_PxParticleSpring(address);
      PxParticleSpring createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxParticleSpring createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxParticleSpring(address);
      PxParticleSpring createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxParticleSpring(long var0);

   public PxParticleSpring() {
      this.address = _PxParticleSpring();
   }

   private static native long _PxParticleSpring();

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

   public int getInd0() {
      this.checkNotNull();
      return _getInd0(this.address);
   }

   private static native int _getInd0(long var0);

   public void setInd0(int value) {
      this.checkNotNull();
      _setInd0(this.address, value);
   }

   private static native void _setInd0(long var0, int var2);

   public int getInd1() {
      this.checkNotNull();
      return _getInd1(this.address);
   }

   private static native int _getInd1(long var0);

   public void setInd1(int value) {
      this.checkNotNull();
      _setInd1(this.address, value);
   }

   private static native void _setInd1(long var0, int var2);

   public float getLength() {
      this.checkNotNull();
      return _getLength(this.address);
   }

   private static native float _getLength(long var0);

   public void setLength(float value) {
      this.checkNotNull();
      _setLength(this.address, value);
   }

   private static native void _setLength(long var0, float var2);

   public float getStiffness() {
      this.checkNotNull();
      return _getStiffness(this.address);
   }

   private static native float _getStiffness(long var0);

   public void setStiffness(float value) {
      this.checkNotNull();
      _setStiffness(this.address, value);
   }

   private static native void _setStiffness(long var0, float var2);

   public float getDamping() {
      this.checkNotNull();
      return _getDamping(this.address);
   }

   private static native float _getDamping(long var0);

   public void setDamping(float value) {
      this.checkNotNull();
      _setDamping(this.address, value);
   }

   private static native void _setDamping(long var0, float var2);

   public float getPad() {
      this.checkNotNull();
      return _getPad(this.address);
   }

   private static native float _getPad(long var0);

   public void setPad(float value) {
      this.checkNotNull();
      _setPad(this.address, value);
   }

   private static native void _setPad(long var0, float var2);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleSpring");
   }
}
