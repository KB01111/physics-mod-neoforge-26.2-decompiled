package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxVec4;

public class PxParticleRigidAttachment extends PxParticleRigidFilterPair {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxParticleRigidAttachment wrapPointer(long address) {
      return address != 0L ? new PxParticleRigidAttachment(address) : null;
   }

   public static PxParticleRigidAttachment arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxParticleRigidAttachment(long address) {
      super(address);
   }

   public static PxParticleRigidAttachment createAt(long address) {
      __placement_new_PxParticleRigidAttachment(address);
      PxParticleRigidAttachment createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxParticleRigidAttachment createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxParticleRigidAttachment(address);
      PxParticleRigidAttachment createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxParticleRigidAttachment(long var0);

   public PxParticleRigidAttachment() {
      this.address = _PxParticleRigidAttachment();
   }

   private static native long _PxParticleRigidAttachment();

   public PxVec4 getMLocalPose0() {
      this.checkNotNull();
      return PxVec4.wrapPointer(_getMLocalPose0(this.address));
   }

   private static native long _getMLocalPose0(long var0);

   public void setMLocalPose0(PxVec4 value) {
      this.checkNotNull();
      _setMLocalPose0(this.address, value.getAddress());
   }

   private static native void _setMLocalPose0(long var0, long var2);

   public PxConeLimitParams getMConeLimitParams() {
      this.checkNotNull();
      return PxConeLimitParams.wrapPointer(_getMConeLimitParams(this.address));
   }

   private static native long _getMConeLimitParams(long var0);

   public void setMConeLimitParams(PxConeLimitParams value) {
      this.checkNotNull();
      _setMConeLimitParams(this.address, value.getAddress());
   }

   private static native void _setMConeLimitParams(long var0, long var2);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleRigidAttachment");
   }
}
