package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxBase;
import physx.common.PxVec4;

public class PxParticleBuffer extends PxBase {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxParticleBuffer() {
   }

   private static native int __sizeOf();

   public static PxParticleBuffer wrapPointer(long address) {
      return address != 0L ? new PxParticleBuffer(address) : null;
   }

   public static PxParticleBuffer arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxParticleBuffer(long address) {
      super(address);
   }

   @Deprecated
   public int getBufferUniqueId() {
      this.checkNotNull();
      return _getBufferUniqueId(this.address);
   }

   private static native int _getBufferUniqueId(long var0);

   public PxVec4 getPositionInvMasses() {
      this.checkNotNull();
      return PxVec4.wrapPointer(_getPositionInvMasses(this.address));
   }

   private static native long _getPositionInvMasses(long var0);

   public PxVec4 getVelocities() {
      this.checkNotNull();
      return PxVec4.wrapPointer(_getVelocities(this.address));
   }

   private static native long _getVelocities(long var0);

   public NativeObject getPhases() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_getPhases(this.address));
   }

   private static native long _getPhases(long var0);

   @Deprecated
   public PxParticleVolume getParticleVolumes() {
      this.checkNotNull();
      return PxParticleVolume.wrapPointer(_getParticleVolumes(this.address));
   }

   private static native long _getParticleVolumes(long var0);

   public void setNbActiveParticles(int nbActiveParticles) {
      this.checkNotNull();
      _setNbActiveParticles(this.address, nbActiveParticles);
   }

   private static native void _setNbActiveParticles(long var0, int var2);

   public int getNbActiveParticles() {
      this.checkNotNull();
      return _getNbActiveParticles(this.address);
   }

   private static native int _getNbActiveParticles(long var0);

   public int getMaxParticles() {
      this.checkNotNull();
      return _getMaxParticles(this.address);
   }

   private static native int _getMaxParticles(long var0);

   @Deprecated
   public int getNbParticleVolumes() {
      this.checkNotNull();
      return _getNbParticleVolumes(this.address);
   }

   private static native int _getNbParticleVolumes(long var0);

   @Deprecated
   public void setNbParticleVolumes(int nbParticleVolumes) {
      this.checkNotNull();
      _setNbParticleVolumes(this.address, nbParticleVolumes);
   }

   private static native void _setNbParticleVolumes(long var0, int var2);

   @Deprecated
   public int getMaxParticleVolumes() {
      this.checkNotNull();
      return _getMaxParticleVolumes(this.address);
   }

   private static native int _getMaxParticleVolumes(long var0);

   @Deprecated
   public void setRigidFilters(PxParticleRigidFilterPair filters, int nbFilters) {
      this.checkNotNull();
      _setRigidFilters(this.address, filters.getAddress(), nbFilters);
   }

   private static native void _setRigidFilters(long var0, long var2, int var4);

   @Deprecated
   public void setRigidAttachments(PxParticleRigidAttachment attachments, int nbAttachments) {
      this.checkNotNull();
      _setRigidAttachments(this.address, attachments.getAddress(), nbAttachments);
   }

   private static native void _setRigidAttachments(long var0, long var2, int var4);

   public int getFlatListStartIndex() {
      this.checkNotNull();
      return _getFlatListStartIndex(this.address);
   }

   private static native int _getFlatListStartIndex(long var0);

   public void raiseFlags(PxParticleBufferFlagEnum flags) {
      this.checkNotNull();
      _raiseFlags(this.address, flags.value);
   }

   private static native void _raiseFlags(long var0, int var2);

   @Override
   public void release() {
      this.checkNotNull();
      _release(this.address);
   }

   private static native void _release(long var0);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleBuffer");
   }
}
