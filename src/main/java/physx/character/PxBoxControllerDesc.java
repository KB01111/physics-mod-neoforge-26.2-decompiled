package physx.character;

import physx.NativeObject;

public class PxBoxControllerDesc extends PxControllerDesc {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxBoxControllerDesc wrapPointer(long address) {
      return address != 0L ? new PxBoxControllerDesc(address) : null;
   }

   public static PxBoxControllerDesc arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxBoxControllerDesc(long address) {
      super(address);
   }

   public static PxBoxControllerDesc createAt(long address) {
      __placement_new_PxBoxControllerDesc(address);
      PxBoxControllerDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxBoxControllerDesc createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxBoxControllerDesc(address);
      PxBoxControllerDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxBoxControllerDesc(long var0);

   public PxBoxControllerDesc() {
      this.address = _PxBoxControllerDesc();
   }

   private static native long _PxBoxControllerDesc();

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

   public float getHalfHeight() {
      this.checkNotNull();
      return _getHalfHeight(this.address);
   }

   private static native float _getHalfHeight(long var0);

   public void setHalfHeight(float value) {
      this.checkNotNull();
      _setHalfHeight(this.address, value);
   }

   private static native void _setHalfHeight(long var0, float var2);

   public float getHalfSideExtent() {
      this.checkNotNull();
      return _getHalfSideExtent(this.address);
   }

   private static native float _getHalfSideExtent(long var0);

   public void setHalfSideExtent(float value) {
      this.checkNotNull();
      _setHalfSideExtent(this.address, value);
   }

   private static native void _setHalfSideExtent(long var0, float var2);

   public float getHalfForwardExtent() {
      this.checkNotNull();
      return _getHalfForwardExtent(this.address);
   }

   private static native float _getHalfForwardExtent(long var0);

   public void setHalfForwardExtent(float value) {
      this.checkNotNull();
      _setHalfForwardExtent(this.address, value);
   }

   private static native void _setHalfForwardExtent(long var0, float var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
