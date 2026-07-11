package physx.character;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxControllerHit extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxControllerHit() {
   }

   private static native int __sizeOf();

   public static PxControllerHit wrapPointer(long address) {
      return address != 0L ? new PxControllerHit(address) : null;
   }

   public static PxControllerHit arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxControllerHit(long address) {
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

   public PxController getController() {
      this.checkNotNull();
      return PxController.wrapPointer(_getController(this.address));
   }

   private static native long _getController(long var0);

   public void setController(PxController value) {
      this.checkNotNull();
      _setController(this.address, value.getAddress());
   }

   private static native void _setController(long var0, long var2);

   public PxExtendedVec3 getWorldPos() {
      this.checkNotNull();
      return PxExtendedVec3.wrapPointer(_getWorldPos(this.address));
   }

   private static native long _getWorldPos(long var0);

   public void setWorldPos(PxExtendedVec3 value) {
      this.checkNotNull();
      _setWorldPos(this.address, value.getAddress());
   }

   private static native void _setWorldPos(long var0, long var2);

   public PxVec3 getWorldNormal() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getWorldNormal(this.address));
   }

   private static native long _getWorldNormal(long var0);

   public void setWorldNormal(PxVec3 value) {
      this.checkNotNull();
      _setWorldNormal(this.address, value.getAddress());
   }

   private static native void _setWorldNormal(long var0, long var2);

   public PxVec3 getDir() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getDir(this.address));
   }

   private static native long _getDir(long var0);

   public void setDir(PxVec3 value) {
      this.checkNotNull();
      _setDir(this.address, value.getAddress());
   }

   private static native void _setDir(long var0, long var2);

   public float getLength() {
      this.checkNotNull();
      return _getLength(this.address);
   }

   private static native float _getLength(long var0);

   public void setLength(float value) {
      this.checkNotNull();
      _setLength(this.address, value);
   }

   private static native void _setLength(long var0, float var2);
}
