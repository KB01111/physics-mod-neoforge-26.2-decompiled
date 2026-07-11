package physx.extensions;

import physx.NativeObject;

public class PxD6JointDriveFlags extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxD6JointDriveFlags() {
   }

   private static native int __sizeOf();

   public static PxD6JointDriveFlags wrapPointer(long address) {
      return address != 0L ? new PxD6JointDriveFlags(address) : null;
   }

   public static PxD6JointDriveFlags arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxD6JointDriveFlags(long address) {
      super(address);
   }

   public static PxD6JointDriveFlags createAt(long address, int flags) {
      __placement_new_PxD6JointDriveFlags(address, flags);
      PxD6JointDriveFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxD6JointDriveFlags createAt(T allocator, NativeObject.Allocator<T> allocate, int flags) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxD6JointDriveFlags(address, flags);
      PxD6JointDriveFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxD6JointDriveFlags(long var0, int var2);

   public PxD6JointDriveFlags(int flags) {
      this.address = _PxD6JointDriveFlags(flags);
   }

   private static native long _PxD6JointDriveFlags(int var0);

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

   public boolean isSet(PxD6JointDriveFlagEnum flag) {
      this.checkNotNull();
      return _isSet(this.address, flag.value);
   }

   private static native boolean _isSet(long var0, int var2);

   public void raise(PxD6JointDriveFlagEnum flag) {
      this.checkNotNull();
      _raise(this.address, flag.value);
   }

   private static native void _raise(long var0, int var2);

   public void clear(PxD6JointDriveFlagEnum flag) {
      this.checkNotNull();
      _clear(this.address, flag.value);
   }

   private static native void _clear(long var0, int var2);
}
