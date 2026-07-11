package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxCudaContextManager;
import physx.support.PxU32Ptr;

public class PxPartitionedParticleCloth extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxPartitionedParticleCloth wrapPointer(long address) {
      return address != 0L ? new PxPartitionedParticleCloth(address) : null;
   }

   public static PxPartitionedParticleCloth arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxPartitionedParticleCloth(long address) {
      super(address);
   }

   public static PxPartitionedParticleCloth createAt(long address) {
      __placement_new_PxPartitionedParticleCloth(address);
      PxPartitionedParticleCloth createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxPartitionedParticleCloth createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxPartitionedParticleCloth(address);
      PxPartitionedParticleCloth createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxPartitionedParticleCloth(long var0);

   public PxPartitionedParticleCloth() {
      this.address = _PxPartitionedParticleCloth();
   }

   private static native long _PxPartitionedParticleCloth();

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

   public PxU32Ptr getAccumulatedSpringsPerPartitions() {
      this.checkNotNull();
      return PxU32Ptr.wrapPointer(_getAccumulatedSpringsPerPartitions(this.address));
   }

   private static native long _getAccumulatedSpringsPerPartitions(long var0);

   public void setAccumulatedSpringsPerPartitions(PxU32Ptr value) {
      this.checkNotNull();
      _setAccumulatedSpringsPerPartitions(this.address, value.getAddress());
   }

   private static native void _setAccumulatedSpringsPerPartitions(long var0, long var2);

   public PxU32Ptr getAccumulatedCopiesPerParticles() {
      this.checkNotNull();
      return PxU32Ptr.wrapPointer(_getAccumulatedCopiesPerParticles(this.address));
   }

   private static native long _getAccumulatedCopiesPerParticles(long var0);

   public void setAccumulatedCopiesPerParticles(PxU32Ptr value) {
      this.checkNotNull();
      _setAccumulatedCopiesPerParticles(this.address, value.getAddress());
   }

   private static native void _setAccumulatedCopiesPerParticles(long var0, long var2);

   public PxU32Ptr getRemapOutput() {
      this.checkNotNull();
      return PxU32Ptr.wrapPointer(_getRemapOutput(this.address));
   }

   private static native long _getRemapOutput(long var0);

   public void setRemapOutput(PxU32Ptr value) {
      this.checkNotNull();
      _setRemapOutput(this.address, value.getAddress());
   }

   private static native void _setRemapOutput(long var0, long var2);

   public PxParticleSpring getOrderedSprings() {
      this.checkNotNull();
      return PxParticleSpring.wrapPointer(_getOrderedSprings(this.address));
   }

   private static native long _getOrderedSprings(long var0);

   public void setOrderedSprings(PxParticleSpring value) {
      this.checkNotNull();
      _setOrderedSprings(this.address, value.getAddress());
   }

   private static native void _setOrderedSprings(long var0, long var2);

   public PxU32Ptr getSortedClothStartIndices() {
      this.checkNotNull();
      return PxU32Ptr.wrapPointer(_getSortedClothStartIndices(this.address));
   }

   private static native long _getSortedClothStartIndices(long var0);

   public void setSortedClothStartIndices(PxU32Ptr value) {
      this.checkNotNull();
      _setSortedClothStartIndices(this.address, value.getAddress());
   }

   private static native void _setSortedClothStartIndices(long var0, long var2);

   public PxParticleCloth getCloths() {
      this.checkNotNull();
      return PxParticleCloth.wrapPointer(_getCloths(this.address));
   }

   private static native long _getCloths(long var0);

   public void setCloths(PxParticleCloth value) {
      this.checkNotNull();
      _setCloths(this.address, value.getAddress());
   }

   private static native void _setCloths(long var0, long var2);

   public int getRemapOutputSize() {
      this.checkNotNull();
      return _getRemapOutputSize(this.address);
   }

   private static native int _getRemapOutputSize(long var0);

   public void setRemapOutputSize(int value) {
      this.checkNotNull();
      _setRemapOutputSize(this.address, value);
   }

   private static native void _setRemapOutputSize(long var0, int var2);

   public int getNbPartitions() {
      this.checkNotNull();
      return _getNbPartitions(this.address);
   }

   private static native int _getNbPartitions(long var0);

   public void setNbPartitions(int value) {
      this.checkNotNull();
      _setNbPartitions(this.address, value);
   }

   private static native void _setNbPartitions(long var0, int var2);

   public int getNbSprings() {
      this.checkNotNull();
      return _getNbSprings(this.address);
   }

   private static native int _getNbSprings(long var0);

   public void setNbSprings(int value) {
      this.checkNotNull();
      _setNbSprings(this.address, value);
   }

   private static native void _setNbSprings(long var0, int var2);

   public int getNbCloths() {
      this.checkNotNull();
      return _getNbCloths(this.address);
   }

   private static native int _getNbCloths(long var0);

   public void setNbCloths(int value) {
      this.checkNotNull();
      _setNbCloths(this.address, value);
   }

   private static native void _setNbCloths(long var0, int var2);

   public int getMaxSpringsPerPartition() {
      this.checkNotNull();
      return _getMaxSpringsPerPartition(this.address);
   }

   private static native int _getMaxSpringsPerPartition(long var0);

   public void setMaxSpringsPerPartition(int value) {
      this.checkNotNull();
      _setMaxSpringsPerPartition(this.address, value);
   }

   private static native void _setMaxSpringsPerPartition(long var0, int var2);

   public PxCudaContextManager getMCudaManager() {
      this.checkNotNull();
      return PxCudaContextManager.wrapPointer(_getMCudaManager(this.address));
   }

   private static native long _getMCudaManager(long var0);

   public void setMCudaManager(PxCudaContextManager value) {
      this.checkNotNull();
      _setMCudaManager(this.address, value.getAddress());
   }

   private static native void _setMCudaManager(long var0, long var2);

   public void allocateBuffers(int nbParticles, PxCudaContextManager cudaManager) {
      this.checkNotNull();
      _allocateBuffers(this.address, nbParticles, cudaManager.getAddress());
   }

   private static native void _allocateBuffers(long var0, int var2, long var3);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxPartitionedParticleCloth");
   }
}
