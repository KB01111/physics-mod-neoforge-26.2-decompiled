package physx.vehicle2;

import physx.cooking.PxCookingParams;
import physx.physics.PxMaterial;
import physx.physics.PxPhysics;

public class PhysXActorVehicle extends BaseVehicle {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PhysXActorVehicle() {
   }

   private static native int __sizeOf();

   public static PhysXActorVehicle wrapPointer(long address) {
      return address != 0L ? new PhysXActorVehicle(address) : null;
   }

   public static PhysXActorVehicle arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PhysXActorVehicle(long address) {
      super(address);
   }

   @Override
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

   public PhysXIntegrationParams getPhysXParams() {
      this.checkNotNull();
      return PhysXIntegrationParams.wrapPointer(_getPhysXParams(this.address));
   }

   private static native long _getPhysXParams(long var0);

   public void setPhysXParams(PhysXIntegrationParams value) {
      this.checkNotNull();
      _setPhysXParams(this.address, value.getAddress());
   }

   private static native void _setPhysXParams(long var0, long var2);

   public PhysXIntegrationState getPhysXState() {
      this.checkNotNull();
      return PhysXIntegrationState.wrapPointer(_getPhysXState(this.address));
   }

   private static native long _getPhysXState(long var0);

   public void setPhysXState(PhysXIntegrationState value) {
      this.checkNotNull();
      _setPhysXState(this.address, value.getAddress());
   }

   private static native void _setPhysXState(long var0, long var2);

   public PxVehicleCommandState getCommandState() {
      this.checkNotNull();
      return PxVehicleCommandState.wrapPointer(_getCommandState(this.address));
   }

   private static native long _getCommandState(long var0);

   public void setCommandState(PxVehicleCommandState value) {
      this.checkNotNull();
      _setCommandState(this.address, value.getAddress());
   }

   private static native void _setCommandState(long var0, long var2);

   public boolean initialize(PxPhysics physics, PxCookingParams params, PxMaterial defaultMaterial) {
      this.checkNotNull();
      return _initialize(this.address, physics.getAddress(), params.getAddress(), defaultMaterial.getAddress());
   }

   private static native boolean _initialize(long var0, long var2, long var4, long var6);
}
