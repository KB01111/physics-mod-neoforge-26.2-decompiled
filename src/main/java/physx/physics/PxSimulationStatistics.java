package physx.physics;

import physx.NativeObject;

public class PxSimulationStatistics extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxSimulationStatistics() {
   }

   private static native int __sizeOf();

   public static PxSimulationStatistics wrapPointer(long address) {
      return address != 0L ? new PxSimulationStatistics(address) : null;
   }

   public static PxSimulationStatistics arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxSimulationStatistics(long address) {
      super(address);
   }

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

   public int getNbActiveConstraints() {
      this.checkNotNull();
      return _getNbActiveConstraints(this.address);
   }

   private static native int _getNbActiveConstraints(long var0);

   public void setNbActiveConstraints(int value) {
      this.checkNotNull();
      _setNbActiveConstraints(this.address, value);
   }

   private static native void _setNbActiveConstraints(long var0, int var2);

   public int getNbActiveDynamicBodies() {
      this.checkNotNull();
      return _getNbActiveDynamicBodies(this.address);
   }

   private static native int _getNbActiveDynamicBodies(long var0);

   public void setNbActiveDynamicBodies(int value) {
      this.checkNotNull();
      _setNbActiveDynamicBodies(this.address, value);
   }

   private static native void _setNbActiveDynamicBodies(long var0, int var2);

   public int getNbActiveKinematicBodies() {
      this.checkNotNull();
      return _getNbActiveKinematicBodies(this.address);
   }

   private static native int _getNbActiveKinematicBodies(long var0);

   public void setNbActiveKinematicBodies(int value) {
      this.checkNotNull();
      _setNbActiveKinematicBodies(this.address, value);
   }

   private static native void _setNbActiveKinematicBodies(long var0, int var2);

   public int getNbStaticBodies() {
      this.checkNotNull();
      return _getNbStaticBodies(this.address);
   }

   private static native int _getNbStaticBodies(long var0);

   public void setNbStaticBodies(int value) {
      this.checkNotNull();
      _setNbStaticBodies(this.address, value);
   }

   private static native void _setNbStaticBodies(long var0, int var2);

   public int getNbDynamicBodies() {
      this.checkNotNull();
      return _getNbDynamicBodies(this.address);
   }

   private static native int _getNbDynamicBodies(long var0);

   public void setNbDynamicBodies(int value) {
      this.checkNotNull();
      _setNbDynamicBodies(this.address, value);
   }

   private static native void _setNbDynamicBodies(long var0, int var2);

   public int getNbKinematicBodies() {
      this.checkNotNull();
      return _getNbKinematicBodies(this.address);
   }

   private static native int _getNbKinematicBodies(long var0);

   public void setNbKinematicBodies(int value) {
      this.checkNotNull();
      _setNbKinematicBodies(this.address, value);
   }

   private static native void _setNbKinematicBodies(long var0, int var2);

   public int getNbShapes(int index) {
      this.checkNotNull();
      return _getNbShapes(this.address, index);
   }

   private static native int _getNbShapes(long var0, int var2);

   public void setNbShapes(int index, int value) {
      this.checkNotNull();
      _setNbShapes(this.address, index, value);
   }

   private static native void _setNbShapes(long var0, int var2, int var3);

   public int getNbAggregates() {
      this.checkNotNull();
      return _getNbAggregates(this.address);
   }

   private static native int _getNbAggregates(long var0);

   public void setNbAggregates(int value) {
      this.checkNotNull();
      _setNbAggregates(this.address, value);
   }

   private static native void _setNbAggregates(long var0, int var2);

   public int getNbArticulations() {
      this.checkNotNull();
      return _getNbArticulations(this.address);
   }

   private static native int _getNbArticulations(long var0);

   public void setNbArticulations(int value) {
      this.checkNotNull();
      _setNbArticulations(this.address, value);
   }

   private static native void _setNbArticulations(long var0, int var2);

   public int getNbAxisSolverConstraints() {
      this.checkNotNull();
      return _getNbAxisSolverConstraints(this.address);
   }

   private static native int _getNbAxisSolverConstraints(long var0);

   public void setNbAxisSolverConstraints(int value) {
      this.checkNotNull();
      _setNbAxisSolverConstraints(this.address, value);
   }

   private static native void _setNbAxisSolverConstraints(long var0, int var2);

   public int getCompressedContactSize() {
      this.checkNotNull();
      return _getCompressedContactSize(this.address);
   }

   private static native int _getCompressedContactSize(long var0);

   public void setCompressedContactSize(int value) {
      this.checkNotNull();
      _setCompressedContactSize(this.address, value);
   }

   private static native void _setCompressedContactSize(long var0, int var2);

   public int getRequiredContactConstraintMemory() {
      this.checkNotNull();
      return _getRequiredContactConstraintMemory(this.address);
   }

   private static native int _getRequiredContactConstraintMemory(long var0);

   public void setRequiredContactConstraintMemory(int value) {
      this.checkNotNull();
      _setRequiredContactConstraintMemory(this.address, value);
   }

   private static native void _setRequiredContactConstraintMemory(long var0, int var2);

   public int getPeakConstraintMemory() {
      this.checkNotNull();
      return _getPeakConstraintMemory(this.address);
   }

   private static native int _getPeakConstraintMemory(long var0);

   public void setPeakConstraintMemory(int value) {
      this.checkNotNull();
      _setPeakConstraintMemory(this.address, value);
   }

   private static native void _setPeakConstraintMemory(long var0, int var2);

   public int getNbDiscreteContactPairsTotal() {
      this.checkNotNull();
      return _getNbDiscreteContactPairsTotal(this.address);
   }

   private static native int _getNbDiscreteContactPairsTotal(long var0);

   public void setNbDiscreteContactPairsTotal(int value) {
      this.checkNotNull();
      _setNbDiscreteContactPairsTotal(this.address, value);
   }

   private static native void _setNbDiscreteContactPairsTotal(long var0, int var2);

   public int getNbDiscreteContactPairsWithCacheHits() {
      this.checkNotNull();
      return _getNbDiscreteContactPairsWithCacheHits(this.address);
   }

   private static native int _getNbDiscreteContactPairsWithCacheHits(long var0);

   public void setNbDiscreteContactPairsWithCacheHits(int value) {
      this.checkNotNull();
      _setNbDiscreteContactPairsWithCacheHits(this.address, value);
   }

   private static native void _setNbDiscreteContactPairsWithCacheHits(long var0, int var2);

   public int getNbDiscreteContactPairsWithContacts() {
      this.checkNotNull();
      return _getNbDiscreteContactPairsWithContacts(this.address);
   }

   private static native int _getNbDiscreteContactPairsWithContacts(long var0);

   public void setNbDiscreteContactPairsWithContacts(int value) {
      this.checkNotNull();
      _setNbDiscreteContactPairsWithContacts(this.address, value);
   }

   private static native void _setNbDiscreteContactPairsWithContacts(long var0, int var2);

   public int getNbNewPairs() {
      this.checkNotNull();
      return _getNbNewPairs(this.address);
   }

   private static native int _getNbNewPairs(long var0);

   public void setNbNewPairs(int value) {
      this.checkNotNull();
      _setNbNewPairs(this.address, value);
   }

   private static native void _setNbNewPairs(long var0, int var2);

   public int getNbLostPairs() {
      this.checkNotNull();
      return _getNbLostPairs(this.address);
   }

   private static native int _getNbLostPairs(long var0);

   public void setNbLostPairs(int value) {
      this.checkNotNull();
      _setNbLostPairs(this.address, value);
   }

   private static native void _setNbLostPairs(long var0, int var2);

   public int getNbNewTouches() {
      this.checkNotNull();
      return _getNbNewTouches(this.address);
   }

   private static native int _getNbNewTouches(long var0);

   public void setNbNewTouches(int value) {
      this.checkNotNull();
      _setNbNewTouches(this.address, value);
   }

   private static native void _setNbNewTouches(long var0, int var2);

   public int getNbLostTouches() {
      this.checkNotNull();
      return _getNbLostTouches(this.address);
   }

   private static native int _getNbLostTouches(long var0);

   public void setNbLostTouches(int value) {
      this.checkNotNull();
      _setNbLostTouches(this.address, value);
   }

   private static native void _setNbLostTouches(long var0, int var2);

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

   public int getNbBroadPhaseAdds() {
      this.checkNotNull();
      return _getNbBroadPhaseAdds(this.address);
   }

   private static native int _getNbBroadPhaseAdds(long var0);

   public void setNbBroadPhaseAdds(int value) {
      this.checkNotNull();
      _setNbBroadPhaseAdds(this.address, value);
   }

   private static native void _setNbBroadPhaseAdds(long var0, int var2);

   public int getNbBroadPhaseRemoves() {
      this.checkNotNull();
      return _getNbBroadPhaseRemoves(this.address);
   }

   private static native int _getNbBroadPhaseRemoves(long var0);

   public void setNbBroadPhaseRemoves(int value) {
      this.checkNotNull();
      _setNbBroadPhaseRemoves(this.address, value);
   }

   private static native void _setNbBroadPhaseRemoves(long var0, int var2);
}
