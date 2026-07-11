package physx.physics;

import physx.NativeObject;

public class PxArticulationLimit extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxArticulationLimit wrapPointer(long address) {
      return address != 0L ? new PxArticulationLimit(address) : null;
   }

   public static PxArticulationLimit arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxArticulationLimit(long address) {
      super(address);
   }

   public static PxArticulationLimit createAt(long address) {
      __placement_new_PxArticulationLimit(address);
      PxArticulationLimit createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxArticulationLimit createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxArticulationLimit(address);
      PxArticulationLimit createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxArticulationLimit(long var0);

   public static PxArticulationLimit createAt(long address, float low, float high) {
      __placement_new_PxArticulationLimit(address, low, high);
      PxArticulationLimit createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxArticulationLimit createAt(T allocator, NativeObject.Allocator<T> allocate, float low, float high) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxArticulationLimit(address, low, high);
      PxArticulationLimit createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxArticulationLimit(long var0, float var2, float var3);

   public PxArticulationLimit() {
      this.address = _PxArticulationLimit();
   }

   private static native long _PxArticulationLimit();

   public PxArticulationLimit(float low, float high) {
      this.address = _PxArticulationLimit(low, high);
   }

   private static native long _PxArticulationLimit(float var0, float var1);

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

   public float getLow() {
      this.checkNotNull();
      return _getLow(this.address);
   }

   private static native float _getLow(long var0);

   public void setLow(float value) {
      this.checkNotNull();
      _setLow(this.address, value);
   }

   private static native void _setLow(long var0, float var2);

   public float getHigh() {
      this.checkNotNull();
      return _getHigh(this.address);
   }

   private static native float _getHigh(long var0);

   public void setHigh(float value) {
      this.checkNotNull();
      _setHigh(this.address, value);
   }

   private static native void _setHigh(long var0, float var2);
}
