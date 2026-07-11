package physx.geometry;

import physx.NativeObject;
import physx.common.PxRefCounted;
import physx.common.PxVec3;

public class PxHeightField extends PxRefCounted {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxHeightField() {
   }

   private static native int __sizeOf();

   public static PxHeightField wrapPointer(long address) {
      return address != 0L ? new PxHeightField(address) : null;
   }

   public static PxHeightField arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxHeightField(long address) {
      super(address);
   }

   public int saveCells(NativeObject destBuffer, int destBufferSize) {
      this.checkNotNull();
      return _saveCells(this.address, destBuffer.getAddress(), destBufferSize);
   }

   private static native int _saveCells(long var0, long var2, int var4);

   public boolean modifySamples(int startCol, int startRow, PxHeightFieldDesc subfieldDesc) {
      this.checkNotNull();
      return _modifySamples(this.address, startCol, startRow, subfieldDesc.getAddress());
   }

   private static native boolean _modifySamples(long var0, int var2, int var3, long var4);

   public boolean modifySamples(int startCol, int startRow, PxHeightFieldDesc subfieldDesc, boolean shrinkBounds) {
      this.checkNotNull();
      return _modifySamples(this.address, startCol, startRow, subfieldDesc.getAddress(), shrinkBounds);
   }

   private static native boolean _modifySamples(long var0, int var2, int var3, long var4, boolean var6);

   public int getNbRows() {
      this.checkNotNull();
      return _getNbRows(this.address);
   }

   private static native int _getNbRows(long var0);

   public int getNbColumns() {
      this.checkNotNull();
      return _getNbColumns(this.address);
   }

   private static native int _getNbColumns(long var0);

   public PxHeightFieldFormatEnum getFormat() {
      this.checkNotNull();
      return PxHeightFieldFormatEnum.forValue(_getFormat(this.address));
   }

   private static native int _getFormat(long var0);

   public int getSampleStride() {
      this.checkNotNull();
      return _getSampleStride(this.address);
   }

   private static native int _getSampleStride(long var0);

   public float getConvexEdgeThreshold() {
      this.checkNotNull();
      return _getConvexEdgeThreshold(this.address);
   }

   private static native float _getConvexEdgeThreshold(long var0);

   public PxHeightFieldFlags getFlags() {
      this.checkNotNull();
      return PxHeightFieldFlags.wrapPointer(_getFlags(this.address));
   }

   private static native long _getFlags(long var0);

   public float getHeight(float x, float z) {
      this.checkNotNull();
      return _getHeight(this.address, x, z);
   }

   private static native float _getHeight(long var0, float var2, float var3);

   public short getTriangleMaterialIndex(int triangleIndex) {
      this.checkNotNull();
      return _getTriangleMaterialIndex(this.address, triangleIndex);
   }

   private static native short _getTriangleMaterialIndex(long var0, int var2);

   public PxVec3 getTriangleNormal(int triangleIndex) {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getTriangleNormal(this.address, triangleIndex));
   }

   private static native long _getTriangleNormal(long var0, int var2);

   public PxHeightFieldSample getSample(int row, int column) {
      this.checkNotNull();
      return PxHeightFieldSample.wrapPointer(_getSample(this.address, row, column));
   }

   private static native long _getSample(long var0, int var2, int var3);

   public int getTimestamp() {
      this.checkNotNull();
      return _getTimestamp(this.address);
   }

   private static native int _getTimestamp(long var0);
}
