package physx.physics;

import physx.NativeObject;

public class PxShapeFlags extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxShapeFlags() {
   }

   private static native int __sizeOf();

   public static PxShapeFlags wrapPointer(long address) {
      return address != 0L ? new PxShapeFlags(address) : null;
   }

   public static PxShapeFlags arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxShapeFlags(long address) {
      super(address);
   }

   public static PxShapeFlags createAt(long address, byte flags) {
      __placement_new_PxShapeFlags(address, flags);
      PxShapeFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxShapeFlags createAt(T allocator, NativeObject.Allocator<T> allocate, byte flags) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxShapeFlags(address, flags);
      PxShapeFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxShapeFlags(long var0, byte var2);

   public PxShapeFlags(byte flags) {
      this.address = _PxShapeFlags(flags);
   }

   private static native long _PxShapeFlags(byte var0);

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

   public boolean isSet(PxShapeFlagEnum flag) {
      this.checkNotNull();
      return _isSet(this.address, flag.value);
   }

   private static native boolean _isSet(long var0, int var2);

   public void raise(PxShapeFlagEnum flag) {
      this.checkNotNull();
      _raise(this.address, flag.value);
   }

   private static native void _raise(long var0, int var2);

   public void clear(PxShapeFlagEnum flag) {
      this.checkNotNull();
      _clear(this.address, flag.value);
   }

   private static native void _clear(long var0, int var2);
}
