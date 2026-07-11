package physx.particles;

import physx.PlatformChecks;
import physx.common.PxCudaContextManager;
import physx.physics.PxActor;
import physx.physics.PxFilterData;
import physx.physics.PxRigidActor;

public class PxParticleSystem extends PxActor {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxParticleSystem() {
   }

   private static native int __sizeOf();

   public static PxParticleSystem wrapPointer(long address) {
      return address != 0L ? new PxParticleSystem(address) : null;
   }

   public static PxParticleSystem arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxParticleSystem(long address) {
      super(address);
   }

   public void setSolverIterationCounts(int minPositionIters, int minVelocityIters) {
      this.checkNotNull();
      _setSolverIterationCounts(this.address, minPositionIters, minVelocityIters);
   }

   private static native void _setSolverIterationCounts(long var0, int var2, int var3);

   public PxFilterData getSimulationFilterData() {
      this.checkNotNull();
      return PxFilterData.wrapPointer(_getSimulationFilterData(this.address));
   }

   private static native long _getSimulationFilterData(long var0);

   public void setSimulationFilterData(PxFilterData data) {
      this.checkNotNull();
      _setSimulationFilterData(this.address, data.getAddress());
   }

   private static native void _setSimulationFilterData(long var0, long var2);

   public void setParticleFlag(PxParticleFlagEnum flag, boolean val) {
      this.checkNotNull();
      _setParticleFlag(this.address, flag.value, val);
   }

   private static native void _setParticleFlag(long var0, int var2, boolean var3);

   public void setParticleFlags(PxParticleFlags flags) {
      this.checkNotNull();
      _setParticleFlags(this.address, flags.getAddress());
   }

   private static native void _setParticleFlags(long var0, long var2);

   public PxParticleFlags getParticleFlags() {
      this.checkNotNull();
      return PxParticleFlags.wrapPointer(_getParticleFlags(this.address));
   }

   private static native long _getParticleFlags(long var0);

   public void setMaxDepenetrationVelocity(float maxDepenetrationVelocity) {
      this.checkNotNull();
      _setMaxDepenetrationVelocity(this.address, maxDepenetrationVelocity);
   }

   private static native void _setMaxDepenetrationVelocity(long var0, float var2);

   public float getMaxDepenetrationVelocity() {
      this.checkNotNull();
      return _getMaxDepenetrationVelocity(this.address);
   }

   private static native float _getMaxDepenetrationVelocity(long var0);

   public void setMaxVelocity(float maxVelocity) {
      this.checkNotNull();
      _setMaxVelocity(this.address, maxVelocity);
   }

   private static native void _setMaxVelocity(long var0, float var2);

   public float getMaxVelocity() {
      this.checkNotNull();
      return _getMaxVelocity(this.address);
   }

   private static native float _getMaxVelocity(long var0);

   public PxCudaContextManager getCudaContextManager() {
      this.checkNotNull();
      return PxCudaContextManager.wrapPointer(_getCudaContextManager(this.address));
   }

   private static native long _getCudaContextManager(long var0);

   public void setRestOffset(float restOffset) {
      this.checkNotNull();
      _setRestOffset(this.address, restOffset);
   }

   private static native void _setRestOffset(long var0, float var2);

   public float getRestOffset() {
      this.checkNotNull();
      return _getRestOffset(this.address);
   }

   private static native float _getRestOffset(long var0);

   public void setContactOffset(float contactOffset) {
      this.checkNotNull();
      _setContactOffset(this.address, contactOffset);
   }

   private static native void _setContactOffset(long var0, float var2);

   public float getContactOffset() {
      this.checkNotNull();
      return _getContactOffset(this.address);
   }

   private static native float _getContactOffset(long var0);

   public void setParticleContactOffset(float particleContactOffset) {
      this.checkNotNull();
      _setParticleContactOffset(this.address, particleContactOffset);
   }

   private static native void _setParticleContactOffset(long var0, float var2);

   public float getParticleContactOffset() {
      this.checkNotNull();
      return _getParticleContactOffset(this.address);
   }

   private static native float _getParticleContactOffset(long var0);

   public void setSolidRestOffset(float solidRestOffset) {
      this.checkNotNull();
      _setSolidRestOffset(this.address, solidRestOffset);
   }

   private static native void _setSolidRestOffset(long var0, float var2);

   public float getSolidRestOffset() {
      this.checkNotNull();
      return _getSolidRestOffset(this.address);
   }

   private static native float _getSolidRestOffset(long var0);

   public void addRigidAttachment(PxRigidActor actor) {
      this.checkNotNull();
      _addRigidAttachment(this.address, actor.getAddress());
   }

   private static native void _addRigidAttachment(long var0, long var2);

   public void removeRigidAttachment(PxRigidActor actor) {
      this.checkNotNull();
      _removeRigidAttachment(this.address, actor.getAddress());
   }

   private static native void _removeRigidAttachment(long var0, long var2);

   public void enableCCD(boolean enable) {
      this.checkNotNull();
      _enableCCD(this.address, enable);
   }

   private static native void _enableCCD(long var0, boolean var2);

   public int createPhase(PxParticleMaterial material, PxParticlePhaseFlags flags) {
      this.checkNotNull();
      return _createPhase(this.address, material.getAddress(), flags.getAddress());
   }

   private static native int _createPhase(long var0, long var2, long var4);

   public int getNbParticleMaterials() {
      this.checkNotNull();
      return _getNbParticleMaterials(this.address);
   }

   private static native int _getNbParticleMaterials(long var0);

   public void setParticleSystemCallback(PxParticleSystemCallback callback) {
      this.checkNotNull();
      _setParticleSystemCallback(this.address, callback.getAddress());
   }

   private static native void _setParticleSystemCallback(long var0, long var2);

   public PxParticleSystemCallback getParticleSystemCallback() {
      this.checkNotNull();
      return PxParticleSystemCallback.wrapPointer(_getParticleSystemCallback(this.address));
   }

   private static native long _getParticleSystemCallback(long var0);

   public void addParticleBuffer(PxParticleBuffer particleBuffer) {
      this.checkNotNull();
      _addParticleBuffer(this.address, particleBuffer.getAddress());
   }

   private static native void _addParticleBuffer(long var0, long var2);

   public void removeParticleBuffer(PxParticleBuffer particleBuffer) {
      this.checkNotNull();
      _removeParticleBuffer(this.address, particleBuffer.getAddress());
   }

   private static native void _removeParticleBuffer(long var0, long var2);

   public int getGpuParticleSystemIndex() {
      this.checkNotNull();
      return _getGpuParticleSystemIndex(this.address);
   }

   private static native int _getGpuParticleSystemIndex(long var0);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleSystem");
   }
}
