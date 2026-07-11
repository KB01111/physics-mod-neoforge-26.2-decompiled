package physx.geometry;

import physx.NativeObject;

public class PxHullPolygon extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxHullPolygon wrapPointer(long address) {
      return address != 0L ? new PxHullPolygon(address) : null;
   }

   public static PxHullPolygon arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxHullPolygon(long address) {
      super(address);
   }

   public static PxHullPolygon createAt(long address) {
      __placement_new_PxHullPolygon(address);
      PxHullPolygon createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxHullPolygon createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxHullPolygon(address);
      PxHullPolygon createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxHullPolygon(long var0);

   public PxHullPolygon() {
      this.address = _PxHullPolygon();
   }

   private static native long _PxHullPolygon();

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

   public float getMPlane(int index) {
      this.checkNotNull();
      return _getMPlane(this.address, index);
   }

   private static native float _getMPlane(long var0, int var2);

   public void setMPlane(int index, float value) {
      this.checkNotNull();
      _setMPlane(this.address, index, value);
   }

   private static native void _setMPlane(long var0, int var2, float var3);

   public short getMNbVerts() {
      this.checkNotNull();
      return _getMNbVerts(this.address);
   }

   private static native short _getMNbVerts(long var0);

   public void setMNbVerts(short value) {
      this.checkNotNull();
      _setMNbVerts(this.address, value);
   }

   private static native void _setMNbVerts(long var0, short var2);

   public short getMIndexBase() {
      this.checkNotNull();
      return _getMIndexBase(this.address);
   }

   private static native short _getMIndexBase(long var0);

   public void setMIndexBase(short value) {
      this.checkNotNull();
      _setMIndexBase(this.address, value);
   }

   private static native void _setMIndexBase(long var0, short var2);
}
