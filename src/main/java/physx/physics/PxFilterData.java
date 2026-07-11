package physx.physics;

import physx.NativeObject;

public class PxFilterData extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxFilterData wrapPointer(long address) {
      return address != 0L ? new PxFilterData(address) : null;
   }

   public static PxFilterData arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxFilterData(long address) {
      super(address);
   }

   public static PxFilterData createAt(long address) {
      __placement_new_PxFilterData(address);
      PxFilterData createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxFilterData createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxFilterData(address);
      PxFilterData createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxFilterData(long var0);

   public static PxFilterData createAt(long address, int w0, int w1, int w2, int w3) {
      __placement_new_PxFilterData(address, w0, w1, w2, w3);
      PxFilterData createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxFilterData createAt(T allocator, NativeObject.Allocator<T> allocate, int w0, int w1, int w2, int w3) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxFilterData(address, w0, w1, w2, w3);
      PxFilterData createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxFilterData(long var0, int var2, int var3, int var4, int var5);

   public PxFilterData() {
      this.address = _PxFilterData();
   }

   private static native long _PxFilterData();

   public PxFilterData(int w0, int w1, int w2, int w3) {
      this.address = _PxFilterData(w0, w1, w2, w3);
   }

   private static native long _PxFilterData(int var0, int var1, int var2, int var3);

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

   public int getWord0() {
      this.checkNotNull();
      return _getWord0(this.address);
   }

   private static native int _getWord0(long var0);

   public void setWord0(int value) {
      this.checkNotNull();
      _setWord0(this.address, value);
   }

   private static native void _setWord0(long var0, int var2);

   public int getWord1() {
      this.checkNotNull();
      return _getWord1(this.address);
   }

   private static native int _getWord1(long var0);

   public void setWord1(int value) {
      this.checkNotNull();
      _setWord1(this.address, value);
   }

   private static native void _setWord1(long var0, int var2);

   public int getWord2() {
      this.checkNotNull();
      return _getWord2(this.address);
   }

   private static native int _getWord2(long var0);

   public void setWord2(int value) {
      this.checkNotNull();
      _setWord2(this.address, value);
   }

   private static native void _setWord2(long var0, int var2);

   public int getWord3() {
      this.checkNotNull();
      return _getWord3(this.address);
   }

   private static native int _getWord3(long var0);

   public void setWord3(int value) {
      this.checkNotNull();
      _setWord3(this.address, value);
   }

   private static native void _setWord3(long var0, int var2);
}
