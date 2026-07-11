package physx.geometry;

import physx.NativeObject;
import physx.common.PxBoundedData;
import physx.common.PxTypedStridedData_PxU16;
import physx.support.PxArray_PxU32;
import physx.support.PxArray_PxVec3;

public class PxTetrahedronMeshDesc extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxTetrahedronMeshDesc wrapPointer(long address) {
      return address != 0L ? new PxTetrahedronMeshDesc(address) : null;
   }

   public static PxTetrahedronMeshDesc arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxTetrahedronMeshDesc(long address) {
      super(address);
   }

   public static PxTetrahedronMeshDesc createAt(long address) {
      __placement_new_PxTetrahedronMeshDesc(address);
      PxTetrahedronMeshDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxTetrahedronMeshDesc createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxTetrahedronMeshDesc(address);
      PxTetrahedronMeshDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxTetrahedronMeshDesc(long var0);

   public static PxTetrahedronMeshDesc createAt(long address, PxArray_PxVec3 meshVertices, PxArray_PxU32 meshTetIndices) {
      __placement_new_PxTetrahedronMeshDesc(address, meshVertices.getAddress(), meshTetIndices.getAddress());
      PxTetrahedronMeshDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxTetrahedronMeshDesc createAt(T allocator, NativeObject.Allocator<T> allocate, PxArray_PxVec3 meshVertices, PxArray_PxU32 meshTetIndices) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxTetrahedronMeshDesc(address, meshVertices.getAddress(), meshTetIndices.getAddress());
      PxTetrahedronMeshDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxTetrahedronMeshDesc(long var0, long var2, long var4);

   public static PxTetrahedronMeshDesc createAt(long address, PxArray_PxVec3 meshVertices, PxArray_PxU32 meshTetIndices, PxTetrahedronMeshFormatEnum meshFormat) {
      __placement_new_PxTetrahedronMeshDesc(address, meshVertices.getAddress(), meshTetIndices.getAddress(), meshFormat.value);
      PxTetrahedronMeshDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxTetrahedronMeshDesc createAt(
      T allocator, NativeObject.Allocator<T> allocate, PxArray_PxVec3 meshVertices, PxArray_PxU32 meshTetIndices, PxTetrahedronMeshFormatEnum meshFormat
   ) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxTetrahedronMeshDesc(address, meshVertices.getAddress(), meshTetIndices.getAddress(), meshFormat.value);
      PxTetrahedronMeshDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxTetrahedronMeshDesc(long var0, long var2, long var4, int var6);

   public static PxTetrahedronMeshDesc createAt(
      long address, PxArray_PxVec3 meshVertices, PxArray_PxU32 meshTetIndices, PxTetrahedronMeshFormatEnum meshFormat, short numberOfTetsPerHexElement
   ) {
      __placement_new_PxTetrahedronMeshDesc(address, meshVertices.getAddress(), meshTetIndices.getAddress(), meshFormat.value, numberOfTetsPerHexElement);
      PxTetrahedronMeshDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxTetrahedronMeshDesc createAt(
      T allocator,
      NativeObject.Allocator<T> allocate,
      PxArray_PxVec3 meshVertices,
      PxArray_PxU32 meshTetIndices,
      PxTetrahedronMeshFormatEnum meshFormat,
      short numberOfTetsPerHexElement
   ) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxTetrahedronMeshDesc(address, meshVertices.getAddress(), meshTetIndices.getAddress(), meshFormat.value, numberOfTetsPerHexElement);
      PxTetrahedronMeshDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxTetrahedronMeshDesc(long var0, long var2, long var4, int var6, short var7);

   public PxTetrahedronMeshDesc() {
      this.address = _PxTetrahedronMeshDesc();
   }

   private static native long _PxTetrahedronMeshDesc();

   public PxTetrahedronMeshDesc(PxArray_PxVec3 meshVertices, PxArray_PxU32 meshTetIndices) {
      this.address = _PxTetrahedronMeshDesc(meshVertices.getAddress(), meshTetIndices.getAddress());
   }

   private static native long _PxTetrahedronMeshDesc(long var0, long var2);

   public PxTetrahedronMeshDesc(PxArray_PxVec3 meshVertices, PxArray_PxU32 meshTetIndices, PxTetrahedronMeshFormatEnum meshFormat) {
      this.address = _PxTetrahedronMeshDesc(meshVertices.getAddress(), meshTetIndices.getAddress(), meshFormat.value);
   }

   private static native long _PxTetrahedronMeshDesc(long var0, long var2, int var4);

   public PxTetrahedronMeshDesc(
      PxArray_PxVec3 meshVertices, PxArray_PxU32 meshTetIndices, PxTetrahedronMeshFormatEnum meshFormat, short numberOfTetsPerHexElement
   ) {
      this.address = _PxTetrahedronMeshDesc(meshVertices.getAddress(), meshTetIndices.getAddress(), meshFormat.value, numberOfTetsPerHexElement);
   }

   private static native long _PxTetrahedronMeshDesc(long var0, long var2, int var4, short var5);

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

   public PxTypedStridedData_PxU16 getMaterialIndices() {
      this.checkNotNull();
      return PxTypedStridedData_PxU16.wrapPointer(_getMaterialIndices(this.address));
   }

   private static native long _getMaterialIndices(long var0);

   public void setMaterialIndices(PxTypedStridedData_PxU16 value) {
      this.checkNotNull();
      _setMaterialIndices(this.address, value.getAddress());
   }

   private static native void _setMaterialIndices(long var0, long var2);

   public PxBoundedData getPoints() {
      this.checkNotNull();
      return PxBoundedData.wrapPointer(_getPoints(this.address));
   }

   private static native long _getPoints(long var0);

   public void setPoints(PxBoundedData value) {
      this.checkNotNull();
      _setPoints(this.address, value.getAddress());
   }

   private static native void _setPoints(long var0, long var2);

   public PxBoundedData getTetrahedrons() {
      this.checkNotNull();
      return PxBoundedData.wrapPointer(_getTetrahedrons(this.address));
   }

   private static native long _getTetrahedrons(long var0);

   public void setTetrahedrons(PxBoundedData value) {
      this.checkNotNull();
      _setTetrahedrons(this.address, value.getAddress());
   }

   private static native void _setTetrahedrons(long var0, long var2);

   public PxMeshFlags getFlags() {
      this.checkNotNull();
      return PxMeshFlags.wrapPointer(_getFlags(this.address));
   }

   private static native long _getFlags(long var0);

   public void setFlags(PxMeshFlags value) {
      this.checkNotNull();
      _setFlags(this.address, value.getAddress());
   }

   private static native void _setFlags(long var0, long var2);

   public short getTetsPerElement() {
      this.checkNotNull();
      return _getTetsPerElement(this.address);
   }

   private static native short _getTetsPerElement(long var0);

   public void setTetsPerElement(short value) {
      this.checkNotNull();
      _setTetsPerElement(this.address, value);
   }

   private static native void _setTetsPerElement(long var0, short var2);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
