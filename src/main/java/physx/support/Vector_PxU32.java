package physx.support;

import physx.NativeObject;

@Deprecated
public class Vector_PxU32 extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static Vector_PxU32 wrapPointer(long address) {
      return address != 0L ? new Vector_PxU32(address) : null;
   }

   public static Vector_PxU32 arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected Vector_PxU32(long address) {
      super(address);
   }

   public Vector_PxU32() {
      this.address = _Vector_PxU32();
   }

   private static native long _Vector_PxU32();

   public Vector_PxU32(int size) {
      this.address = _Vector_PxU32(size);
   }

   private static native long _Vector_PxU32(int var0);

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

   public int at(int index) {
      this.checkNotNull();
      return _at(this.address, index);
   }

   private static native int _at(long var0, int var2);

   public NativeObject data() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_data(this.address));
   }

   private static native long _data(long var0);

   public int size() {
      this.checkNotNull();
      return _size(this.address);
   }

   private static native int _size(long var0);

   public void push_back(int value) {
      this.checkNotNull();
      _push_back(this.address, value);
   }

   private static native void _push_back(long var0, int var2);

   public void clear() {
      this.checkNotNull();
      _clear(this.address);
   }

   private static native void _clear(long var0);
}
