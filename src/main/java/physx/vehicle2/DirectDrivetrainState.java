package physx.vehicle2;

import physx.NativeObject;

public class DirectDrivetrainState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static DirectDrivetrainState wrapPointer(long address) {
      return address != 0L ? new DirectDrivetrainState(address) : null;
   }

   public static DirectDrivetrainState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected DirectDrivetrainState(long address) {
      super(address);
   }

   public DirectDrivetrainState() {
      this.address = _DirectDrivetrainState();
   }

   private static native long _DirectDrivetrainState();

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

   public float getDirectDriveThrottleResponseStates(int index) {
      this.checkNotNull();
      return _getDirectDriveThrottleResponseStates(this.address, index);
   }

   private static native float _getDirectDriveThrottleResponseStates(long var0, int var2);

   public void setDirectDriveThrottleResponseStates(int index, float value) {
      this.checkNotNull();
      _setDirectDriveThrottleResponseStates(this.address, index, value);
   }

   private static native void _setDirectDriveThrottleResponseStates(long var0, int var2, float var3);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
