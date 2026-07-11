package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxVehicleSuspensionComplianceState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleSuspensionComplianceState wrapPointer(long address) {
      return address != 0L ? new PxVehicleSuspensionComplianceState(address) : null;
   }

   public static PxVehicleSuspensionComplianceState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleSuspensionComplianceState(long address) {
      super(address);
   }

   public PxVehicleSuspensionComplianceState() {
      this.address = _PxVehicleSuspensionComplianceState();
   }

   private static native long _PxVehicleSuspensionComplianceState();

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

   public float getToe() {
      this.checkNotNull();
      return _getToe(this.address);
   }

   private static native float _getToe(long var0);

   public void setToe(float value) {
      this.checkNotNull();
      _setToe(this.address, value);
   }

   private static native void _setToe(long var0, float var2);

   public float getCamber() {
      this.checkNotNull();
      return _getCamber(this.address);
   }

   private static native float _getCamber(long var0);

   public void setCamber(float value) {
      this.checkNotNull();
      _setCamber(this.address, value);
   }

   private static native void _setCamber(long var0, float var2);

   public PxVec3 getTireForceAppPoint() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getTireForceAppPoint(this.address));
   }

   private static native long _getTireForceAppPoint(long var0);

   public void setTireForceAppPoint(PxVec3 value) {
      this.checkNotNull();
      _setTireForceAppPoint(this.address, value.getAddress());
   }

   private static native void _setTireForceAppPoint(long var0, long var2);

   public PxVec3 getSuspForceAppPoint() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getSuspForceAppPoint(this.address));
   }

   private static native long _getSuspForceAppPoint(long var0);

   public void setSuspForceAppPoint(PxVec3 value) {
      this.checkNotNull();
      _setSuspForceAppPoint(this.address, value.getAddress());
   }

   private static native void _setSuspForceAppPoint(long var0, long var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
