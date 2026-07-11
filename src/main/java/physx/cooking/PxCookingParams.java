package physx.cooking;

import physx.NativeObject;
import physx.common.PxTolerancesScale;

public class PxCookingParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxCookingParams() {
   }

   private static native int __sizeOf();

   public static PxCookingParams wrapPointer(long address) {
      return address != 0L ? new PxCookingParams(address) : null;
   }

   public static PxCookingParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxCookingParams(long address) {
      super(address);
   }

   public PxCookingParams(PxTolerancesScale sc) {
      this.address = _PxCookingParams(sc.getAddress());
   }

   private static native long _PxCookingParams(long var0);

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

   public float getAreaTestEpsilon() {
      this.checkNotNull();
      return _getAreaTestEpsilon(this.address);
   }

   private static native float _getAreaTestEpsilon(long var0);

   public void setAreaTestEpsilon(float value) {
      this.checkNotNull();
      _setAreaTestEpsilon(this.address, value);
   }

   private static native void _setAreaTestEpsilon(long var0, float var2);

   public float getPlaneTolerance() {
      this.checkNotNull();
      return _getPlaneTolerance(this.address);
   }

   private static native float _getPlaneTolerance(long var0);

   public void setPlaneTolerance(float value) {
      this.checkNotNull();
      _setPlaneTolerance(this.address, value);
   }

   private static native void _setPlaneTolerance(long var0, float var2);

   public PxConvexMeshCookingTypeEnum getConvexMeshCookingType() {
      this.checkNotNull();
      return PxConvexMeshCookingTypeEnum.forValue(_getConvexMeshCookingType(this.address));
   }

   private static native int _getConvexMeshCookingType(long var0);

   public void setConvexMeshCookingType(PxConvexMeshCookingTypeEnum value) {
      this.checkNotNull();
      _setConvexMeshCookingType(this.address, value.value);
   }

   private static native void _setConvexMeshCookingType(long var0, int var2);

   public boolean getSuppressTriangleMeshRemapTable() {
      this.checkNotNull();
      return _getSuppressTriangleMeshRemapTable(this.address);
   }

   private static native boolean _getSuppressTriangleMeshRemapTable(long var0);

   public void setSuppressTriangleMeshRemapTable(boolean value) {
      this.checkNotNull();
      _setSuppressTriangleMeshRemapTable(this.address, value);
   }

   private static native void _setSuppressTriangleMeshRemapTable(long var0, boolean var2);

   public boolean getBuildTriangleAdjacencies() {
      this.checkNotNull();
      return _getBuildTriangleAdjacencies(this.address);
   }

   private static native boolean _getBuildTriangleAdjacencies(long var0);

   public void setBuildTriangleAdjacencies(boolean value) {
      this.checkNotNull();
      _setBuildTriangleAdjacencies(this.address, value);
   }

   private static native void _setBuildTriangleAdjacencies(long var0, boolean var2);

   public boolean getBuildGPUData() {
      this.checkNotNull();
      return _getBuildGPUData(this.address);
   }

   private static native boolean _getBuildGPUData(long var0);

   public void setBuildGPUData(boolean value) {
      this.checkNotNull();
      _setBuildGPUData(this.address, value);
   }

   private static native void _setBuildGPUData(long var0, boolean var2);

   public PxTolerancesScale getScale() {
      this.checkNotNull();
      return PxTolerancesScale.wrapPointer(_getScale(this.address));
   }

   private static native long _getScale(long var0);

   public void setScale(PxTolerancesScale value) {
      this.checkNotNull();
      _setScale(this.address, value.getAddress());
   }

   private static native void _setScale(long var0, long var2);

   public PxMeshPreprocessingFlags getMeshPreprocessParams() {
      this.checkNotNull();
      return PxMeshPreprocessingFlags.wrapPointer(_getMeshPreprocessParams(this.address));
   }

   private static native long _getMeshPreprocessParams(long var0);

   public void setMeshPreprocessParams(PxMeshPreprocessingFlags value) {
      this.checkNotNull();
      _setMeshPreprocessParams(this.address, value.getAddress());
   }

   private static native void _setMeshPreprocessParams(long var0, long var2);

   public float getMeshWeldTolerance() {
      this.checkNotNull();
      return _getMeshWeldTolerance(this.address);
   }

   private static native float _getMeshWeldTolerance(long var0);

   public void setMeshWeldTolerance(float value) {
      this.checkNotNull();
      _setMeshWeldTolerance(this.address, value);
   }

   private static native void _setMeshWeldTolerance(long var0, float var2);

   public PxMidphaseDesc getMidphaseDesc() {
      this.checkNotNull();
      return PxMidphaseDesc.wrapPointer(_getMidphaseDesc(this.address));
   }

   private static native long _getMidphaseDesc(long var0);

   public void setMidphaseDesc(PxMidphaseDesc value) {
      this.checkNotNull();
      _setMidphaseDesc(this.address, value.getAddress());
   }

   private static native void _setMidphaseDesc(long var0, long var2);

   public int getGaussMapLimit() {
      this.checkNotNull();
      return _getGaussMapLimit(this.address);
   }

   private static native int _getGaussMapLimit(long var0);

   public void setGaussMapLimit(int value) {
      this.checkNotNull();
      _setGaussMapLimit(this.address, value);
   }

   private static native void _setGaussMapLimit(long var0, int var2);
}
