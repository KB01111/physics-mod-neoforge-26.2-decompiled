package physx.physics;

import physx.NativeObject;

public class PxDominanceGroupPair extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxDominanceGroupPair() {
   }

   private static native int __sizeOf();

   public static PxDominanceGroupPair wrapPointer(long address) {
      return address != 0L ? new PxDominanceGroupPair(address) : null;
   }

   public static PxDominanceGroupPair arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxDominanceGroupPair(long address) {
      super(address);
   }

   public PxDominanceGroupPair(byte a, byte b) {
      this.address = _PxDominanceGroupPair(a, b);
   }

   private static native long _PxDominanceGroupPair(byte var0, byte var1);

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

   public byte getDominance0() {
      this.checkNotNull();
      return _getDominance0(this.address);
   }

   private static native byte _getDominance0(long var0);

   public void setDominance0(byte value) {
      this.checkNotNull();
      _setDominance0(this.address, value);
   }

   private static native void _setDominance0(long var0, byte var2);

   public byte getDominance1() {
      this.checkNotNull();
      return _getDominance1(this.address);
   }

   private static native byte _getDominance1(long var0);

   public void setDominance1(byte value) {
      this.checkNotNull();
      _setDominance1(this.address, value);
   }

   private static native void _setDominance1(long var0, byte var2);
}
