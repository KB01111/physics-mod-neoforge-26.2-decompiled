package physx.physics;

import physx.NativeObject;

public class PxSceneFlags extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxSceneFlags() {
   }

   private static native int __sizeOf();

   public static PxSceneFlags wrapPointer(long address) {
      return address != 0L ? new PxSceneFlags(address) : null;
   }

   public static PxSceneFlags arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxSceneFlags(long address) {
      super(address);
   }

   public static PxSceneFlags createAt(long address, int flags) {
      __placement_new_PxSceneFlags(address, flags);
      PxSceneFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxSceneFlags createAt(T allocator, NativeObject.Allocator<T> allocate, int flags) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxSceneFlags(address, flags);
      PxSceneFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxSceneFlags(long var0, int var2);

   public PxSceneFlags(int flags) {
      this.address = _PxSceneFlags(flags);
   }

   private static native long _PxSceneFlags(int var0);

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

   public boolean isSet(PxSceneFlagEnum flag) {
      this.checkNotNull();
      return _isSet(this.address, flag.value);
   }

   private static native boolean _isSet(long var0, int var2);

   public void raise(PxSceneFlagEnum flag) {
      this.checkNotNull();
      _raise(this.address, flag.value);
   }

   private static native void _raise(long var0, int var2);

   public void clear(PxSceneFlagEnum flag) {
      this.checkNotNull();
      _clear(this.address, flag.value);
   }

   private static native void _clear(long var0, int var2);
}
