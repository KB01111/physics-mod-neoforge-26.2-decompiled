package physx.physics;

import physx.NativeObject;

public class PxPairFlags extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxPairFlags() {
   }

   private static native int __sizeOf();

   public static PxPairFlags wrapPointer(long address) {
      return address != 0L ? new PxPairFlags(address) : null;
   }

   public static PxPairFlags arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxPairFlags(long address) {
      super(address);
   }

   public static PxPairFlags createAt(long address, short flags) {
      __placement_new_PxPairFlags(address, flags);
      PxPairFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxPairFlags createAt(T allocator, NativeObject.Allocator<T> allocate, short flags) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxPairFlags(address, flags);
      PxPairFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxPairFlags(long var0, short var2);

   public PxPairFlags(short flags) {
      this.address = _PxPairFlags(flags);
   }

   private static native long _PxPairFlags(short var0);

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

   public boolean isSet(PxPairFlagEnum flag) {
      this.checkNotNull();
      return _isSet(this.address, flag.value);
   }

   private static native boolean _isSet(long var0, int var2);

   public void raise(PxPairFlagEnum flag) {
      this.checkNotNull();
      _raise(this.address, flag.value);
   }

   private static native void _raise(long var0, int var2);

   public void clear(PxPairFlagEnum flag) {
      this.checkNotNull();
      _clear(this.address, flag.value);
   }

   private static native void _clear(long var0, int var2);
}
