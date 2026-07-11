package physx.common;

import physx.NativeObject;

public class PxBounds3 extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxBounds3 wrapPointer(long address) {
      return address != 0L ? new PxBounds3(address) : null;
   }

   public static PxBounds3 arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxBounds3(long address) {
      super(address);
   }

   public static PxBounds3 createAt(long address) {
      __placement_new_PxBounds3(address);
      PxBounds3 createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxBounds3 createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxBounds3(address);
      PxBounds3 createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxBounds3(long var0);

   public static PxBounds3 createAt(long address, PxVec3 minimum, PxVec3 maximum) {
      __placement_new_PxBounds3(address, minimum.getAddress(), maximum.getAddress());
      PxBounds3 createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxBounds3 createAt(T allocator, NativeObject.Allocator<T> allocate, PxVec3 minimum, PxVec3 maximum) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxBounds3(address, minimum.getAddress(), maximum.getAddress());
      PxBounds3 createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxBounds3(long var0, long var2, long var4);

   public PxBounds3() {
      this.address = _PxBounds3();
   }

   private static native long _PxBounds3();

   public PxBounds3(PxVec3 minimum, PxVec3 maximum) {
      this.address = _PxBounds3(minimum.getAddress(), maximum.getAddress());
   }

   private static native long _PxBounds3(long var0, long var2);

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

   public PxVec3 getMinimum() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getMinimum(this.address));
   }

   private static native long _getMinimum(long var0);

   public void setMinimum(PxVec3 value) {
      this.checkNotNull();
      _setMinimum(this.address, value.getAddress());
   }

   private static native void _setMinimum(long var0, long var2);

   public PxVec3 getMaximum() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getMaximum(this.address));
   }

   private static native long _getMaximum(long var0);

   public void setMaximum(PxVec3 value) {
      this.checkNotNull();
      _setMaximum(this.address, value.getAddress());
   }

   private static native void _setMaximum(long var0, long var2);

   public void setEmpty() {
      this.checkNotNull();
      _setEmpty(this.address);
   }

   private static native void _setEmpty(long var0);

   public void setMaximal() {
      this.checkNotNull();
      _setMaximal(this.address);
   }

   private static native void _setMaximal(long var0);

   public void include(PxVec3 v) {
      this.checkNotNull();
      _include(this.address, v.getAddress());
   }

   private static native void _include(long var0, long var2);

   public boolean isEmpty() {
      this.checkNotNull();
      return _isEmpty(this.address);
   }

   private static native boolean _isEmpty(long var0);

   public boolean intersects(PxBounds3 b) {
      this.checkNotNull();
      return _intersects(this.address, b.getAddress());
   }

   private static native boolean _intersects(long var0, long var2);

   public boolean intersects1D(PxBounds3 b, int axis) {
      this.checkNotNull();
      return _intersects1D(this.address, b.getAddress(), axis);
   }

   private static native boolean _intersects1D(long var0, long var2, int var4);

   public boolean contains(PxVec3 v) {
      this.checkNotNull();
      return _contains(this.address, v.getAddress());
   }

   private static native boolean _contains(long var0, long var2);

   public boolean isInside(PxBounds3 box) {
      this.checkNotNull();
      return _isInside(this.address, box.getAddress());
   }

   private static native boolean _isInside(long var0, long var2);

   public PxVec3 getCenter() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getCenter(this.address));
   }

   private static native long _getCenter(long var0);

   public PxVec3 getDimensions() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getDimensions(this.address));
   }

   private static native long _getDimensions(long var0);

   public PxVec3 getExtents() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getExtents(this.address));
   }

   private static native long _getExtents(long var0);

   public void scaleSafe(float scale) {
      this.checkNotNull();
      _scaleSafe(this.address, scale);
   }

   private static native void _scaleSafe(long var0, float var2);

   public void scaleFast(float scale) {
      this.checkNotNull();
      _scaleFast(this.address, scale);
   }

   private static native void _scaleFast(long var0, float var2);

   public void fattenSafe(float distance) {
      this.checkNotNull();
      _fattenSafe(this.address, distance);
   }

   private static native void _fattenSafe(long var0, float var2);

   public void fattenFast(float distance) {
      this.checkNotNull();
      _fattenFast(this.address, distance);
   }

   private static native void _fattenFast(long var0, float var2);

   public boolean isFinite() {
      this.checkNotNull();
      return _isFinite(this.address);
   }

   private static native boolean _isFinite(long var0);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
