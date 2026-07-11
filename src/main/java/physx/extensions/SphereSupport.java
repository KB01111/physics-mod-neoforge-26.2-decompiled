package physx.extensions;

import physx.NativeObject;

public class SphereSupport extends Support {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected SphereSupport() {
   }

   private static native int __sizeOf();

   public static SphereSupport wrapPointer(long address) {
      return address != 0L ? new SphereSupport(address) : null;
   }

   public static SphereSupport arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected SphereSupport(long address) {
      super(address);
   }

   public static SphereSupport createAt(long address, float radius) {
      __placement_new_SphereSupport(address, radius);
      SphereSupport createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> SphereSupport createAt(T allocator, NativeObject.Allocator<T> allocate, float radius) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_SphereSupport(address, radius);
      SphereSupport createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_SphereSupport(long var0, float var2);

   public SphereSupport(float radius) {
      this.address = _SphereSupport(radius);
   }

   private static native long _SphereSupport(float var0);

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

   public float getRadius() {
      this.checkNotNull();
      return _getRadius(this.address);
   }

   private static native float _getRadius(long var0);

   public void setRadius(float value) {
      this.checkNotNull();
      _setRadius(this.address, value);
   }

   private static native void _setRadius(long var0, float var2);
}
