package physx.vehicle2;

import physx.cooking.PxCookingParams;
import physx.physics.PxMaterial;
import physx.physics.PxPhysics;

public class DirectDriveVehicle extends PhysXActorVehicle {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static DirectDriveVehicle wrapPointer(long address) {
      return address != 0L ? new DirectDriveVehicle(address) : null;
   }

   public static DirectDriveVehicle arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected DirectDriveVehicle(long address) {
      super(address);
   }

   public DirectDriveVehicle() {
      this.address = _DirectDriveVehicle();
   }

   private static native long _DirectDriveVehicle();

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

   public DirectDrivetrainParams getDirectDriveParams() {
      this.checkNotNull();
      return DirectDrivetrainParams.wrapPointer(_getDirectDriveParams(this.address));
   }

   private static native long _getDirectDriveParams(long var0);

   public void setDirectDriveParams(DirectDrivetrainParams value) {
      this.checkNotNull();
      _setDirectDriveParams(this.address, value.getAddress());
   }

   private static native void _setDirectDriveParams(long var0, long var2);

   public DirectDrivetrainState getDirectDriveState() {
      this.checkNotNull();
      return DirectDrivetrainState.wrapPointer(_getDirectDriveState(this.address));
   }

   private static native long _getDirectDriveState(long var0);

   public void setDirectDriveState(DirectDrivetrainState value) {
      this.checkNotNull();
      _setDirectDriveState(this.address, value.getAddress());
   }

   private static native void _setDirectDriveState(long var0, long var2);

   public PxVehicleDirectDriveTransmissionCommandState getTransmissionCommandState() {
      this.checkNotNull();
      return PxVehicleDirectDriveTransmissionCommandState.wrapPointer(_getTransmissionCommandState(this.address));
   }

   private static native long _getTransmissionCommandState(long var0);

   public void setTransmissionCommandState(PxVehicleDirectDriveTransmissionCommandState value) {
      this.checkNotNull();
      _setTransmissionCommandState(this.address, value.getAddress());
   }

   private static native void _setTransmissionCommandState(long var0, long var2);

   @Override
   public boolean initialize(PxPhysics physics, PxCookingParams params, PxMaterial defaultMaterial) {
      this.checkNotNull();
      return _initialize(this.address, physics.getAddress(), params.getAddress(), defaultMaterial.getAddress());
   }

   private static native boolean _initialize(long var0, long var2, long var4, long var6);

   public boolean initialize(PxPhysics physics, PxCookingParams params, PxMaterial defaultMaterial, boolean addPhysXBeginEndComponents) {
      this.checkNotNull();
      return _initialize(this.address, physics.getAddress(), params.getAddress(), defaultMaterial.getAddress(), addPhysXBeginEndComponents);
   }

   private static native boolean _initialize(long var0, long var2, long var4, long var6, boolean var8);

   @Override
   public void initComponentSequence(boolean addPhysXBeginEndComponents) {
      this.checkNotNull();
      _initComponentSequence(this.address, addPhysXBeginEndComponents);
   }

   private static native void _initComponentSequence(long var0, boolean var2);
}
