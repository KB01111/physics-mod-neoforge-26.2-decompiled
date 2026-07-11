package physx.vhacd;

import physx.NativeObject;
import physx.PlatformChecks;

public class VHACDTriangle extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static VHACDTriangle wrapPointer(long address) {
      return address != 0L ? new VHACDTriangle(address) : null;
   }

   public static VHACDTriangle arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected VHACDTriangle(long address) {
      super(address);
   }

   public VHACDTriangle() {
      this.address = _VHACDTriangle();
   }

   private static native long _VHACDTriangle();

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

   public int getMI0() {
      this.checkNotNull();
      return _getMI0(this.address);
   }

   private static native int _getMI0(long var0);

   public void setMI0(int value) {
      this.checkNotNull();
      _setMI0(this.address, value);
   }

   private static native void _setMI0(long var0, int var2);

   public int getMI1() {
      this.checkNotNull();
      return _getMI1(this.address);
   }

   private static native int _getMI1(long var0);

   public void setMI1(int value) {
      this.checkNotNull();
      _setMI1(this.address, value);
   }

   private static native void _setMI1(long var0, int var2);

   public int getMI2() {
      this.checkNotNull();
      return _getMI2(this.address);
   }

   private static native int _getMI2(long var0);

   public void setMI2(int value) {
      this.checkNotNull();
      _setMI2(this.address, value);
   }

   private static native void _setMI2(long var0, int var2);

   static {
      PlatformChecks.requirePlatform(15, "physx.vhacd.VHACDTriangle");
   }
}
