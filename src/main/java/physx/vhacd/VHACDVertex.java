package physx.vhacd;

import physx.NativeObject;
import physx.PlatformChecks;

public class VHACDVertex extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static VHACDVertex wrapPointer(long address) {
      return address != 0L ? new VHACDVertex(address) : null;
   }

   public static VHACDVertex arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected VHACDVertex(long address) {
      super(address);
   }

   public VHACDVertex() {
      this.address = _VHACDVertex();
   }

   private static native long _VHACDVertex();

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

   public double getMX() {
      this.checkNotNull();
      return _getMX(this.address);
   }

   private static native double _getMX(long var0);

   public void setMX(double value) {
      this.checkNotNull();
      _setMX(this.address, value);
   }

   private static native void _setMX(long var0, double var2);

   public double getMY() {
      this.checkNotNull();
      return _getMY(this.address);
   }

   private static native double _getMY(long var0);

   public void setMY(double value) {
      this.checkNotNull();
      _setMY(this.address, value);
   }

   private static native void _setMY(long var0, double var2);

   public double getMZ() {
      this.checkNotNull();
      return _getMZ(this.address);
   }

   private static native double _getMZ(long var0);

   public void setMZ(double value) {
      this.checkNotNull();
      _setMZ(this.address, value);
   }

   private static native void _setMZ(long var0, double var2);

   static {
      PlatformChecks.requirePlatform(15, "physx.vhacd.VHACDVertex");
   }
}
