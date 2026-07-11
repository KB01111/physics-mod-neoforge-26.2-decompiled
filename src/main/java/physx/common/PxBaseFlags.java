package physx.common;

import physx.NativeObject;

public class PxBaseFlags extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxBaseFlags() {
   }

   private static native int __sizeOf();

   public static PxBaseFlags wrapPointer(long address) {
      return address != 0L ? new PxBaseFlags(address) : null;
   }

   public static PxBaseFlags arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxBaseFlags(long address) {
      super(address);
   }

   public static PxBaseFlags createAt(long address, short flags) {
      __placement_new_PxBaseFlags(address, flags);
      PxBaseFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxBaseFlags createAt(T allocator, NativeObject.Allocator<T> allocate, short flags) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxBaseFlags(address, flags);
      PxBaseFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxBaseFlags(long var0, short var2);

   public PxBaseFlags(short flags) {
      this.address = _PxBaseFlags(flags);
   }

   private static native long _PxBaseFlags(short var0);

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

   public boolean isSet(PxBaseFlagEnum flag) {
      this.checkNotNull();
      return _isSet(this.address, flag.value);
   }

   private static native boolean _isSet(long var0, int var2);

   public void raise(PxBaseFlagEnum flag) {
      this.checkNotNull();
      _raise(this.address, flag.value);
   }

   private static native void _raise(long var0, int var2);

   public void clear(PxBaseFlagEnum flag) {
      this.checkNotNull();
      _clear(this.address, flag.value);
   }

   private static native void _clear(long var0, int var2);
}
