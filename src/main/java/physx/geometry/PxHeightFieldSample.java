package physx.geometry;

import physx.NativeObject;

public class PxHeightFieldSample extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxHeightFieldSample wrapPointer(long address) {
      return address != 0L ? new PxHeightFieldSample(address) : null;
   }

   public static PxHeightFieldSample arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxHeightFieldSample(long address) {
      super(address);
   }

   public static PxHeightFieldSample createAt(long address) {
      __placement_new_PxHeightFieldSample(address);
      PxHeightFieldSample createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxHeightFieldSample createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxHeightFieldSample(address);
      PxHeightFieldSample createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxHeightFieldSample(long var0);

   public PxHeightFieldSample() {
      this.address = _PxHeightFieldSample();
   }

   private static native long _PxHeightFieldSample();

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

   public short getHeight() {
      this.checkNotNull();
      return _getHeight(this.address);
   }

   private static native short _getHeight(long var0);

   public void setHeight(short value) {
      this.checkNotNull();
      _setHeight(this.address, value);
   }

   private static native void _setHeight(long var0, short var2);

   public byte getMaterialIndex0() {
      this.checkNotNull();
      return _getMaterialIndex0(this.address);
   }

   private static native byte _getMaterialIndex0(long var0);

   public void setMaterialIndex0(byte value) {
      this.checkNotNull();
      _setMaterialIndex0(this.address, value);
   }

   private static native void _setMaterialIndex0(long var0, byte var2);

   public byte getMaterialIndex1() {
      this.checkNotNull();
      return _getMaterialIndex1(this.address);
   }

   private static native byte _getMaterialIndex1(long var0);

   public void setMaterialIndex1(byte value) {
      this.checkNotNull();
      _setMaterialIndex1(this.address, value);
   }

   private static native void _setMaterialIndex1(long var0, byte var2);

   public byte tessFlag() {
      this.checkNotNull();
      return _tessFlag(this.address);
   }

   private static native byte _tessFlag(long var0);

   public void clearTessFlag() {
      this.checkNotNull();
      _clearTessFlag(this.address);
   }

   private static native void _clearTessFlag(long var0);

   public void setTessFlag() {
      this.checkNotNull();
      _setTessFlag(this.address);
   }

   private static native void _setTessFlag(long var0);
}
