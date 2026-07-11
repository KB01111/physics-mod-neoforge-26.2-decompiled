package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleClutchCommandResponseState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleClutchCommandResponseState wrapPointer(long address) {
      return address != 0L ? new PxVehicleClutchCommandResponseState(address) : null;
   }

   public static PxVehicleClutchCommandResponseState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleClutchCommandResponseState(long address) {
      super(address);
   }

   public PxVehicleClutchCommandResponseState() {
      this.address = _PxVehicleClutchCommandResponseState();
   }

   private static native long _PxVehicleClutchCommandResponseState();

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

   public float getNormalisedCommandResponse() {
      this.checkNotNull();
      return _getNormalisedCommandResponse(this.address);
   }

   private static native float _getNormalisedCommandResponse(long var0);

   public void setNormalisedCommandResponse(float value) {
      this.checkNotNull();
      _setNormalisedCommandResponse(this.address, value);
   }

   private static native void _setNormalisedCommandResponse(long var0, float var2);

   public float getCommandResponse() {
      this.checkNotNull();
      return _getCommandResponse(this.address);
   }

   private static native float _getCommandResponse(long var0);

   public void setCommandResponse(float value) {
      this.checkNotNull();
      _setCommandResponse(this.address, value);
   }

   private static native void _setCommandResponse(long var0, float var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
