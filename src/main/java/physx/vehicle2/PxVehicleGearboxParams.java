package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleGearboxParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleGearboxParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleGearboxParams(address) : null;
   }

   public static PxVehicleGearboxParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleGearboxParams(long address) {
      super(address);
   }

   public static PxVehicleGearboxParams createAt(long address) {
      __placement_new_PxVehicleGearboxParams(address);
      PxVehicleGearboxParams createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxVehicleGearboxParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxVehicleGearboxParams(address);
      PxVehicleGearboxParams createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxVehicleGearboxParams(long var0);

   public PxVehicleGearboxParams() {
      this.address = _PxVehicleGearboxParams();
   }

   private static native long _PxVehicleGearboxParams();

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

   public int getNeutralGear() {
      this.checkNotNull();
      return _getNeutralGear(this.address);
   }

   private static native int _getNeutralGear(long var0);

   public void setNeutralGear(int value) {
      this.checkNotNull();
      _setNeutralGear(this.address, value);
   }

   private static native void _setNeutralGear(long var0, int var2);

   public float getRatios(int index) {
      this.checkNotNull();
      return _getRatios(this.address, index);
   }

   private static native float _getRatios(long var0, int var2);

   public void setRatios(int index, float value) {
      this.checkNotNull();
      _setRatios(this.address, index, value);
   }

   private static native void _setRatios(long var0, int var2, float var3);

   public float getFinalRatio() {
      this.checkNotNull();
      return _getFinalRatio(this.address);
   }

   private static native float _getFinalRatio(long var0);

   public void setFinalRatio(float value) {
      this.checkNotNull();
      _setFinalRatio(this.address, value);
   }

   private static native void _setFinalRatio(long var0, float var2);

   public int getNbRatios() {
      this.checkNotNull();
      return _getNbRatios(this.address);
   }

   private static native int _getNbRatios(long var0);

   public void setNbRatios(int value) {
      this.checkNotNull();
      _setNbRatios(this.address, value);
   }

   private static native void _setNbRatios(long var0, int var2);

   public float getSwitchTime() {
      this.checkNotNull();
      return _getSwitchTime(this.address);
   }

   private static native float _getSwitchTime(long var0);

   public void setSwitchTime(float value) {
      this.checkNotNull();
      _setSwitchTime(this.address, value);
   }

   private static native void _setSwitchTime(long var0, float var2);

   public PxVehicleGearboxParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
      this.checkNotNull();
      return wrapPointer(_transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
   }

   private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
