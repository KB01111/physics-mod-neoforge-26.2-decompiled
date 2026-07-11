package physx.common;

import physx.NativeObject;

public class PxDebugTriangle extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxDebugTriangle() {
   }

   private static native int __sizeOf();

   public static PxDebugTriangle wrapPointer(long address) {
      return address != 0L ? new PxDebugTriangle(address) : null;
   }

   public static PxDebugTriangle arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxDebugTriangle(long address) {
      super(address);
   }

   public PxVec3 getPos0() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getPos0(this.address));
   }

   private static native long _getPos0(long var0);

   public void setPos0(PxVec3 value) {
      this.checkNotNull();
      _setPos0(this.address, value.getAddress());
   }

   private static native void _setPos0(long var0, long var2);

   public int getColor0() {
      this.checkNotNull();
      return _getColor0(this.address);
   }

   private static native int _getColor0(long var0);

   public void setColor0(int value) {
      this.checkNotNull();
      _setColor0(this.address, value);
   }

   private static native void _setColor0(long var0, int var2);

   public PxVec3 getPos1() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getPos1(this.address));
   }

   private static native long _getPos1(long var0);

   public void setPos1(PxVec3 value) {
      this.checkNotNull();
      _setPos1(this.address, value.getAddress());
   }

   private static native void _setPos1(long var0, long var2);

   public int getColor1() {
      this.checkNotNull();
      return _getColor1(this.address);
   }

   private static native int _getColor1(long var0);

   public void setColor1(int value) {
      this.checkNotNull();
      _setColor1(this.address, value);
   }

   private static native void _setColor1(long var0, int var2);

   public PxVec3 getPos2() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getPos2(this.address));
   }

   private static native long _getPos2(long var0);

   public void setPos2(PxVec3 value) {
      this.checkNotNull();
      _setPos2(this.address, value.getAddress());
   }

   private static native void _setPos2(long var0, long var2);

   public int getColor2() {
      this.checkNotNull();
      return _getColor2(this.address);
   }

   private static native int _getColor2(long var0);

   public void setColor2(int value) {
      this.checkNotNull();
      _setColor2(this.address, value);
   }

   private static native void _setColor2(long var0, int var2);
}
