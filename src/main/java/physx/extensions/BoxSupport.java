package physx.extensions;

import physx.NativeObject;
import physx.common.PxVec3;

public class BoxSupport extends Support {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected BoxSupport() {
   }

   private static native int __sizeOf();

   public static BoxSupport wrapPointer(long address) {
      return address != 0L ? new BoxSupport(address) : null;
   }

   public static BoxSupport arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected BoxSupport(long address) {
      super(address);
   }

   public static BoxSupport createAt(long address, PxVec3 halfExtents) {
      __placement_new_BoxSupport(address, halfExtents.getAddress());
      BoxSupport createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> BoxSupport createAt(T allocator, NativeObject.Allocator<T> allocate, PxVec3 halfExtents) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_BoxSupport(address, halfExtents.getAddress());
      BoxSupport createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_BoxSupport(long var0, long var2);

   public static BoxSupport createAt(long address, PxVec3 halfExtents, float margin) {
      __placement_new_BoxSupport(address, halfExtents.getAddress(), margin);
      BoxSupport createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> BoxSupport createAt(T allocator, NativeObject.Allocator<T> allocate, PxVec3 halfExtents, float margin) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_BoxSupport(address, halfExtents.getAddress(), margin);
      BoxSupport createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_BoxSupport(long var0, long var2, float var4);

   public BoxSupport(PxVec3 halfExtents) {
      this.address = _BoxSupport(halfExtents.getAddress());
   }

   private static native long _BoxSupport(long var0);

   public BoxSupport(PxVec3 halfExtents, float margin) {
      this.address = _BoxSupport(halfExtents.getAddress(), margin);
   }

   private static native long _BoxSupport(long var0, float var2);

   @Override
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

   public PxVec3 getHalfExtents() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getHalfExtents(this.address));
   }

   private static native long _getHalfExtents(long var0);

   public void setHalfExtents(PxVec3 value) {
      this.checkNotNull();
      _setHalfExtents(this.address, value.getAddress());
   }

   private static native void _setHalfExtents(long var0, long var2);

   @Override
   public float getMargin() {
      this.checkNotNull();
      return _getMargin(this.address);
   }

   private static native float _getMargin(long var0);

   public void setMargin(float value) {
      this.checkNotNull();
      _setMargin(this.address, value);
   }

   private static native void _setMargin(long var0, float var2);
}
