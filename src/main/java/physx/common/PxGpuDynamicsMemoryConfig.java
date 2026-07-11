package physx.common;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxGpuDynamicsMemoryConfig extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxGpuDynamicsMemoryConfig wrapPointer(long address) {
      return address != 0L ? new PxGpuDynamicsMemoryConfig(address) : null;
   }

   public static PxGpuDynamicsMemoryConfig arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxGpuDynamicsMemoryConfig(long address) {
      super(address);
   }

   public PxGpuDynamicsMemoryConfig() {
      this.address = _PxGpuDynamicsMemoryConfig();
   }

   private static native long _PxGpuDynamicsMemoryConfig();

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

   public int getTempBufferCapacity() {
      this.checkNotNull();
      return _getTempBufferCapacity(this.address);
   }

   private static native int _getTempBufferCapacity(long var0);

   public void setTempBufferCapacity(int value) {
      this.checkNotNull();
      _setTempBufferCapacity(this.address, value);
   }

   private static native void _setTempBufferCapacity(long var0, int var2);

   public int getMaxRigidContactCount() {
      this.checkNotNull();
      return _getMaxRigidContactCount(this.address);
   }

   private static native int _getMaxRigidContactCount(long var0);

   public void setMaxRigidContactCount(int value) {
      this.checkNotNull();
      _setMaxRigidContactCount(this.address, value);
   }

   private static native void _setMaxRigidContactCount(long var0, int var2);

   public int getMaxRigidPatchCount() {
      this.checkNotNull();
      return _getMaxRigidPatchCount(this.address);
   }

   private static native int _getMaxRigidPatchCount(long var0);

   public void setMaxRigidPatchCount(int value) {
      this.checkNotNull();
      _setMaxRigidPatchCount(this.address, value);
   }

   private static native void _setMaxRigidPatchCount(long var0, int var2);

   public int getHeapCapacity() {
      this.checkNotNull();
      return _getHeapCapacity(this.address);
   }

   private static native int _getHeapCapacity(long var0);

   public void setHeapCapacity(int value) {
      this.checkNotNull();
      _setHeapCapacity(this.address, value);
   }

   private static native void _setHeapCapacity(long var0, int var2);

   public int getFoundLostPairsCapacity() {
      this.checkNotNull();
      return _getFoundLostPairsCapacity(this.address);
   }

   private static native int _getFoundLostPairsCapacity(long var0);

   public void setFoundLostPairsCapacity(int value) {
      this.checkNotNull();
      _setFoundLostPairsCapacity(this.address, value);
   }

   private static native void _setFoundLostPairsCapacity(long var0, int var2);

   public int getFoundLostAggregatePairsCapacity() {
      this.checkNotNull();
      return _getFoundLostAggregatePairsCapacity(this.address);
   }

   private static native int _getFoundLostAggregatePairsCapacity(long var0);

   public void setFoundLostAggregatePairsCapacity(int value) {
      this.checkNotNull();
      _setFoundLostAggregatePairsCapacity(this.address, value);
   }

   private static native void _setFoundLostAggregatePairsCapacity(long var0, int var2);

   public int getTotalAggregatePairsCapacity() {
      this.checkNotNull();
      return _getTotalAggregatePairsCapacity(this.address);
   }

   private static native int _getTotalAggregatePairsCapacity(long var0);

   public void setTotalAggregatePairsCapacity(int value) {
      this.checkNotNull();
      _setTotalAggregatePairsCapacity(this.address, value);
   }

   private static native void _setTotalAggregatePairsCapacity(long var0, int var2);

   public int getMaxDeformableSurfaceContacts() {
      this.checkNotNull();
      return _getMaxDeformableSurfaceContacts(this.address);
   }

   private static native int _getMaxDeformableSurfaceContacts(long var0);

   public void setMaxDeformableSurfaceContacts(int value) {
      this.checkNotNull();
      _setMaxDeformableSurfaceContacts(this.address, value);
   }

   private static native void _setMaxDeformableSurfaceContacts(long var0, int var2);

   public int getMaxFemClothContacts() {
      this.checkNotNull();
      return _getMaxFemClothContacts(this.address);
   }

   private static native int _getMaxFemClothContacts(long var0);

   public void setMaxFemClothContacts(int value) {
      this.checkNotNull();
      _setMaxFemClothContacts(this.address, value);
   }

   private static native void _setMaxFemClothContacts(long var0, int var2);

   public int getMaxDeformableVolumeContacts() {
      this.checkNotNull();
      return _getMaxDeformableVolumeContacts(this.address);
   }

   private static native int _getMaxDeformableVolumeContacts(long var0);

   public void setMaxDeformableVolumeContacts(int value) {
      this.checkNotNull();
      _setMaxDeformableVolumeContacts(this.address, value);
   }

   private static native void _setMaxDeformableVolumeContacts(long var0, int var2);

   public int getMaxSoftBodyContacts() {
      this.checkNotNull();
      return _getMaxSoftBodyContacts(this.address);
   }

   private static native int _getMaxSoftBodyContacts(long var0);

   public void setMaxSoftBodyContacts(int value) {
      this.checkNotNull();
      _setMaxSoftBodyContacts(this.address, value);
   }

   private static native void _setMaxSoftBodyContacts(long var0, int var2);

   public int getMaxParticleContacts() {
      this.checkNotNull();
      return _getMaxParticleContacts(this.address);
   }

   private static native int _getMaxParticleContacts(long var0);

   public void setMaxParticleContacts(int value) {
      this.checkNotNull();
      _setMaxParticleContacts(this.address, value);
   }

   private static native void _setMaxParticleContacts(long var0, int var2);

   public int getCollisionStackSize() {
      this.checkNotNull();
      return _getCollisionStackSize(this.address);
   }

   private static native int _getCollisionStackSize(long var0);

   public void setCollisionStackSize(int value) {
      this.checkNotNull();
      _setCollisionStackSize(this.address, value);
   }

   private static native void _setCollisionStackSize(long var0, int var2);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);

   static {
      PlatformChecks.requirePlatform(3, "physx.common.PxGpuDynamicsMemoryConfig");
   }
}
