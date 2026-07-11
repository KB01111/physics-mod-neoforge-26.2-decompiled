package physx.physics;

import physx.NativeObject;

public class PxSceneLimits extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxSceneLimits wrapPointer(long address) {
      return address != 0L ? new PxSceneLimits(address) : null;
   }

   public static PxSceneLimits arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxSceneLimits(long address) {
      super(address);
   }

   public static PxSceneLimits createAt(long address) {
      __placement_new_PxSceneLimits(address);
      PxSceneLimits createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxSceneLimits createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxSceneLimits(address);
      PxSceneLimits createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxSceneLimits(long var0);

   public PxSceneLimits() {
      this.address = _PxSceneLimits();
   }

   private static native long _PxSceneLimits();

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

   public int getMaxNbActors() {
      this.checkNotNull();
      return _getMaxNbActors(this.address);
   }

   private static native int _getMaxNbActors(long var0);

   public void setMaxNbActors(int value) {
      this.checkNotNull();
      _setMaxNbActors(this.address, value);
   }

   private static native void _setMaxNbActors(long var0, int var2);

   public int getMaxNbBodies() {
      this.checkNotNull();
      return _getMaxNbBodies(this.address);
   }

   private static native int _getMaxNbBodies(long var0);

   public void setMaxNbBodies(int value) {
      this.checkNotNull();
      _setMaxNbBodies(this.address, value);
   }

   private static native void _setMaxNbBodies(long var0, int var2);

   public int getMaxNbStaticShapes() {
      this.checkNotNull();
      return _getMaxNbStaticShapes(this.address);
   }

   private static native int _getMaxNbStaticShapes(long var0);

   public void setMaxNbStaticShapes(int value) {
      this.checkNotNull();
      _setMaxNbStaticShapes(this.address, value);
   }

   private static native void _setMaxNbStaticShapes(long var0, int var2);

   public int getMaxNbDynamicShapes() {
      this.checkNotNull();
      return _getMaxNbDynamicShapes(this.address);
   }

   private static native int _getMaxNbDynamicShapes(long var0);

   public void setMaxNbDynamicShapes(int value) {
      this.checkNotNull();
      _setMaxNbDynamicShapes(this.address, value);
   }

   private static native void _setMaxNbDynamicShapes(long var0, int var2);

   public int getMaxNbAggregates() {
      this.checkNotNull();
      return _getMaxNbAggregates(this.address);
   }

   private static native int _getMaxNbAggregates(long var0);

   public void setMaxNbAggregates(int value) {
      this.checkNotNull();
      _setMaxNbAggregates(this.address, value);
   }

   private static native void _setMaxNbAggregates(long var0, int var2);

   public int getMaxNbConstraints() {
      this.checkNotNull();
      return _getMaxNbConstraints(this.address);
   }

   private static native int _getMaxNbConstraints(long var0);

   public void setMaxNbConstraints(int value) {
      this.checkNotNull();
      _setMaxNbConstraints(this.address, value);
   }

   private static native void _setMaxNbConstraints(long var0, int var2);

   public int getMaxNbRegions() {
      this.checkNotNull();
      return _getMaxNbRegions(this.address);
   }

   private static native int _getMaxNbRegions(long var0);

   public void setMaxNbRegions(int value) {
      this.checkNotNull();
      _setMaxNbRegions(this.address, value);
   }

   private static native void _setMaxNbRegions(long var0, int var2);

   public int getMaxNbBroadPhaseOverlaps() {
      this.checkNotNull();
      return _getMaxNbBroadPhaseOverlaps(this.address);
   }

   private static native int _getMaxNbBroadPhaseOverlaps(long var0);

   public void setMaxNbBroadPhaseOverlaps(int value) {
      this.checkNotNull();
      _setMaxNbBroadPhaseOverlaps(this.address, value);
   }

   private static native void _setMaxNbBroadPhaseOverlaps(long var0, int var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
