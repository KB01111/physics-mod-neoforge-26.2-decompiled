package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleTireForceParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleTireForceParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleTireForceParams(address) : null;
   }

   public static PxVehicleTireForceParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleTireForceParams(long address) {
      super(address);
   }

   public PxVehicleTireForceParams() {
      this.address = _PxVehicleTireForceParams();
   }

   private static native long _PxVehicleTireForceParams();

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

   public float getLatStiffX() {
      this.checkNotNull();
      return _getLatStiffX(this.address);
   }

   private static native float _getLatStiffX(long var0);

   public void setLatStiffX(float value) {
      this.checkNotNull();
      _setLatStiffX(this.address, value);
   }

   private static native void _setLatStiffX(long var0, float var2);

   public float getLatStiffY() {
      this.checkNotNull();
      return _getLatStiffY(this.address);
   }

   private static native float _getLatStiffY(long var0);

   public void setLatStiffY(float value) {
      this.checkNotNull();
      _setLatStiffY(this.address, value);
   }

   private static native void _setLatStiffY(long var0, float var2);

   public float getLongStiff() {
      this.checkNotNull();
      return _getLongStiff(this.address);
   }

   private static native float _getLongStiff(long var0);

   public void setLongStiff(float value) {
      this.checkNotNull();
      _setLongStiff(this.address, value);
   }

   private static native void _setLongStiff(long var0, float var2);

   public float getCamberStiff() {
      this.checkNotNull();
      return _getCamberStiff(this.address);
   }

   private static native float _getCamberStiff(long var0);

   public void setCamberStiff(float value) {
      this.checkNotNull();
      _setCamberStiff(this.address, value);
   }

   private static native void _setCamberStiff(long var0, float var2);

   public float getRestLoad() {
      this.checkNotNull();
      return _getRestLoad(this.address);
   }

   private static native float _getRestLoad(long var0);

   public void setRestLoad(float value) {
      this.checkNotNull();
      _setRestLoad(this.address, value);
   }

   private static native void _setRestLoad(long var0, float var2);

   public PxVehicleTireForceParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
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
