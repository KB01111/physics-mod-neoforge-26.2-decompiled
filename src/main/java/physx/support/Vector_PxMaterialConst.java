package physx.support;

import physx.NativeObject;
import physx.physics.PxMaterial;

@Deprecated
public class Vector_PxMaterialConst extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static Vector_PxMaterialConst wrapPointer(long address) {
      return address != 0L ? new Vector_PxMaterialConst(address) : null;
   }

   public static Vector_PxMaterialConst arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected Vector_PxMaterialConst(long address) {
      super(address);
   }

   public Vector_PxMaterialConst() {
      this.address = _Vector_PxMaterialConst();
   }

   private static native long _Vector_PxMaterialConst();

   public Vector_PxMaterialConst(int size) {
      this.address = _Vector_PxMaterialConst(size);
   }

   private static native long _Vector_PxMaterialConst(int var0);

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

   public PxMaterial at(int index) {
      this.checkNotNull();
      return PxMaterial.wrapPointer(_at(this.address, index));
   }

   private static native long _at(long var0, int var2);

   public PxMaterialConstPtr data() {
      this.checkNotNull();
      return PxMaterialConstPtr.wrapPointer(_data(this.address));
   }

   private static native long _data(long var0);

   public int size() {
      this.checkNotNull();
      return _size(this.address);
   }

   private static native int _size(long var0);

   public void push_back(PxMaterial value) {
      this.checkNotNull();
      _push_back(this.address, value.getAddress());
   }

   private static native void _push_back(long var0, long var2);

   public void clear() {
      this.checkNotNull();
      _clear(this.address);
   }

   private static native void _clear(long var0);
}
