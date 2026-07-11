package physx.vehicle2;

import physx.NativeObject;

public class PxVehiclePhysXSuspensionLimitConstraintParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxVehiclePhysXSuspensionLimitConstraintParams() {
   }

   private static native int __sizeOf();

   public static PxVehiclePhysXSuspensionLimitConstraintParams wrapPointer(long address) {
      return address != 0L ? new PxVehiclePhysXSuspensionLimitConstraintParams(address) : null;
   }

   public static PxVehiclePhysXSuspensionLimitConstraintParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehiclePhysXSuspensionLimitConstraintParams(long address) {
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

   public float getRestitution() {
      this.checkNotNull();
      return _getRestitution(this.address);
   }

   private static native float _getRestitution(long var0);

   public void setRestitution(float value) {
      this.checkNotNull();
      _setRestitution(this.address, value);
   }

   private static native void _setRestitution(long var0, float var2);

   public PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum getDirectionForSuspensionLimitConstraint() {
      this.checkNotNull();
      return PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum.forValue(_getDirectionForSuspensionLimitConstraint(this.address));
   }

   private static native int _getDirectionForSuspensionLimitConstraint(long var0);

   public void setDirectionForSuspensionLimitConstraint(PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum value) {
      this.checkNotNull();
      _setDirectionForSuspensionLimitConstraint(this.address, value.value);
   }

   private static native void _setDirectionForSuspensionLimitConstraint(long var0, int var2);

   public PxVehiclePhysXSuspensionLimitConstraintParams transformAndScale(
      PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale
   ) {
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
