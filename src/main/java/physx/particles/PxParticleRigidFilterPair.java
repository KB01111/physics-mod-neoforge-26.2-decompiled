package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxParticleRigidFilterPair extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxParticleRigidFilterPair wrapPointer(long address) {
      return address != 0L ? new PxParticleRigidFilterPair(address) : null;
   }

   public static PxParticleRigidFilterPair arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxParticleRigidFilterPair(long address) {
      super(address);
   }

   public static PxParticleRigidFilterPair createAt(long address) {
      __placement_new_PxParticleRigidFilterPair(address);
      PxParticleRigidFilterPair createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxParticleRigidFilterPair createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxParticleRigidFilterPair(address);
      PxParticleRigidFilterPair createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxParticleRigidFilterPair(long var0);

   public PxParticleRigidFilterPair() {
      this.address = _PxParticleRigidFilterPair();
   }

   private static native long _PxParticleRigidFilterPair();

   public long getMID0() {
      this.checkNotNull();
      return _getMID0(this.address);
   }

   private static native long _getMID0(long var0);

   public void setMID0(long value) {
      this.checkNotNull();
      _setMID0(this.address, value);
   }

   private static native void _setMID0(long var0, long var2);

   public long getMID1() {
      this.checkNotNull();
      return _getMID1(this.address);
   }

   private static native long _getMID1(long var0);

   public void setMID1(long value) {
      this.checkNotNull();
      _setMID1(this.address, value);
   }

   private static native void _setMID1(long var0, long var2);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleRigidFilterPair");
   }
}
