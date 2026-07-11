package physx.common;

import physx.NativeObject;

public class PxDebugPoint extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxDebugPoint() {
   }

   private static native int __sizeOf();

   public static PxDebugPoint wrapPointer(long address) {
      return address != 0L ? new PxDebugPoint(address) : null;
   }

   public static PxDebugPoint arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxDebugPoint(long address) {
      super(address);
   }

   public PxVec3 getPos() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getPos(this.address));
   }

   private static native long _getPos(long var0);

   public void setPos(PxVec3 value) {
      this.checkNotNull();
      _setPos(this.address, value.getAddress());
   }

   private static native void _setPos(long var0, long var2);

   public int getColor() {
      this.checkNotNull();
      return _getColor(this.address);
   }

   private static native int _getColor(long var0);

   public void setColor(int value) {
      this.checkNotNull();
      _setColor(this.address, value);
   }

   private static native void _setColor(long var0, int var2);
}
