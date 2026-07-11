package physx.vhacd;

import physx.NativeObject;
import physx.PlatformChecks;

public class Vector_VHACDTriangle extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected Vector_VHACDTriangle() {
   }

   private static native int __sizeOf();

   public static Vector_VHACDTriangle wrapPointer(long address) {
      return address != 0L ? new Vector_VHACDTriangle(address) : null;
   }

   public static Vector_VHACDTriangle arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected Vector_VHACDTriangle(long address) {
      super(address);
   }

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

   public VHACDTriangle at(int index) {
      this.checkNotNull();
      return VHACDTriangle.wrapPointer(_at(this.address, index));
   }

   private static native long _at(long var0, int var2);

   public VHACDTriangle data() {
      this.checkNotNull();
      return VHACDTriangle.wrapPointer(_data(this.address));
   }

   private static native long _data(long var0);

   public int size() {
      this.checkNotNull();
      return _size(this.address);
   }

   private static native int _size(long var0);

   public void push_back(VHACDTriangle value) {
      this.checkNotNull();
      _push_back(this.address, value.getAddress());
   }

   private static native void _push_back(long var0, long var2);

   public void clear() {
      this.checkNotNull();
      _clear(this.address);
   }

   private static native void _clear(long var0);

   static {
      PlatformChecks.requirePlatform(15, "physx.vhacd.Vector_VHACDTriangle");
   }
}
