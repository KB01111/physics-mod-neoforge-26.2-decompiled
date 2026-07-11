package physx.geometry;

import physx.NativeObject;
import physx.common.PxStridedData;

public class PxHeightFieldDesc extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxHeightFieldDesc wrapPointer(long address) {
      return address != 0L ? new PxHeightFieldDesc(address) : null;
   }

   public static PxHeightFieldDesc arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxHeightFieldDesc(long address) {
      super(address);
   }

   public static PxHeightFieldDesc createAt(long address) {
      __placement_new_PxHeightFieldDesc(address);
      PxHeightFieldDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxHeightFieldDesc createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxHeightFieldDesc(address);
      PxHeightFieldDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxHeightFieldDesc(long var0);

   public PxHeightFieldDesc() {
      this.address = _PxHeightFieldDesc();
   }

   private static native long _PxHeightFieldDesc();

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

   public int getNbRows() {
      this.checkNotNull();
      return _getNbRows(this.address);
   }

   private static native int _getNbRows(long var0);

   public void setNbRows(int value) {
      this.checkNotNull();
      _setNbRows(this.address, value);
   }

   private static native void _setNbRows(long var0, int var2);

   public int getNbColumns() {
      this.checkNotNull();
      return _getNbColumns(this.address);
   }

   private static native int _getNbColumns(long var0);

   public void setNbColumns(int value) {
      this.checkNotNull();
      _setNbColumns(this.address, value);
   }

   private static native void _setNbColumns(long var0, int var2);

   public PxHeightFieldFormatEnum getFormat() {
      this.checkNotNull();
      return PxHeightFieldFormatEnum.forValue(_getFormat(this.address));
   }

   private static native int _getFormat(long var0);

   public void setFormat(PxHeightFieldFormatEnum value) {
      this.checkNotNull();
      _setFormat(this.address, value.value);
   }

   private static native void _setFormat(long var0, int var2);

   public PxStridedData getSamples() {
      this.checkNotNull();
      return PxStridedData.wrapPointer(_getSamples(this.address));
   }

   private static native long _getSamples(long var0);

   public void setSamples(PxStridedData value) {
      this.checkNotNull();
      _setSamples(this.address, value.getAddress());
   }

   private static native void _setSamples(long var0, long var2);

   public float getConvexEdgeThreshold() {
      this.checkNotNull();
      return _getConvexEdgeThreshold(this.address);
   }

   private static native float _getConvexEdgeThreshold(long var0);

   public void setConvexEdgeThreshold(float value) {
      this.checkNotNull();
      _setConvexEdgeThreshold(this.address, value);
   }

   private static native void _setConvexEdgeThreshold(long var0, float var2);

   public PxHeightFieldFlags getFlags() {
      this.checkNotNull();
      return PxHeightFieldFlags.wrapPointer(_getFlags(this.address));
   }

   private static native long _getFlags(long var0);

   public void setFlags(PxHeightFieldFlags value) {
      this.checkNotNull();
      _setFlags(this.address, value.getAddress());
   }

   private static native void _setFlags(long var0, long var2);

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
