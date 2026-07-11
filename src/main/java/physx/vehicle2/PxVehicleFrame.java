package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxMat33;

public class PxVehicleFrame extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleFrame wrapPointer(long address) {
      return address != 0L ? new PxVehicleFrame(address) : null;
   }

   public static PxVehicleFrame arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleFrame(long address) {
      super(address);
   }

   public PxVehicleFrame() {
      this.address = _PxVehicleFrame();
   }

   private static native long _PxVehicleFrame();

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

   public PxVehicleAxesEnum getLngAxis() {
      this.checkNotNull();
      return PxVehicleAxesEnum.forValue(_getLngAxis(this.address));
   }

   private static native int _getLngAxis(long var0);

   public void setLngAxis(PxVehicleAxesEnum value) {
      this.checkNotNull();
      _setLngAxis(this.address, value.value);
   }

   private static native void _setLngAxis(long var0, int var2);

   public PxVehicleAxesEnum getLatAxis() {
      this.checkNotNull();
      return PxVehicleAxesEnum.forValue(_getLatAxis(this.address));
   }

   private static native int _getLatAxis(long var0);

   public void setLatAxis(PxVehicleAxesEnum value) {
      this.checkNotNull();
      _setLatAxis(this.address, value.value);
   }

   private static native void _setLatAxis(long var0, int var2);

   public PxVehicleAxesEnum getVrtAxis() {
      this.checkNotNull();
      return PxVehicleAxesEnum.forValue(_getVrtAxis(this.address));
   }

   private static native int _getVrtAxis(long var0);

   public void setVrtAxis(PxVehicleAxesEnum value) {
      this.checkNotNull();
      _setVrtAxis(this.address, value.value);
   }

   private static native void _setVrtAxis(long var0, int var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);

   public PxMat33 getFrame() {
      this.checkNotNull();
      return PxMat33.wrapPointer(_getFrame(this.address));
   }

   private static native long _getFrame(long var0);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
