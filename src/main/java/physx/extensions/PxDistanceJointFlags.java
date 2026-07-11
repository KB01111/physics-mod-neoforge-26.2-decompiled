package physx.extensions;

import physx.NativeObject;

public class PxDistanceJointFlags extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxDistanceJointFlags() {
   }

   private static native int __sizeOf();

   public static PxDistanceJointFlags wrapPointer(long address) {
      return address != 0L ? new PxDistanceJointFlags(address) : null;
   }

   public static PxDistanceJointFlags arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxDistanceJointFlags(long address) {
      super(address);
   }

   public static PxDistanceJointFlags createAt(long address, short flags) {
      __placement_new_PxDistanceJointFlags(address, flags);
      PxDistanceJointFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxDistanceJointFlags createAt(T allocator, NativeObject.Allocator<T> allocate, short flags) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxDistanceJointFlags(address, flags);
      PxDistanceJointFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxDistanceJointFlags(long var0, short var2);

   public PxDistanceJointFlags(short flags) {
      this.address = _PxDistanceJointFlags(flags);
   }

   private static native long _PxDistanceJointFlags(short var0);

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

   public boolean isSet(PxDistanceJointFlagEnum flag) {
      this.checkNotNull();
      return _isSet(this.address, flag.value);
   }

   private static native boolean _isSet(long var0, int var2);

   public void raise(PxDistanceJointFlagEnum flag) {
      this.checkNotNull();
      _raise(this.address, flag.value);
   }

   private static native void _raise(long var0, int var2);

   public void clear(PxDistanceJointFlagEnum flag) {
      this.checkNotNull();
      _clear(this.address, flag.value);
   }

   private static native void _clear(long var0, int var2);
}
