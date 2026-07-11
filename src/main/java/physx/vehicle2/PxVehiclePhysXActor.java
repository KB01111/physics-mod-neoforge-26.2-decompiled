package physx.vehicle2;

import physx.NativeObject;
import physx.physics.PxRigidBody;
import physx.physics.PxShape;

public class PxVehiclePhysXActor extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxVehiclePhysXActor() {
   }

   private static native int __sizeOf();

   public static PxVehiclePhysXActor wrapPointer(long address) {
      return address != 0L ? new PxVehiclePhysXActor(address) : null;
   }

   public static PxVehiclePhysXActor arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehiclePhysXActor(long address) {
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

   public PxRigidBody getRigidBody() {
      this.checkNotNull();
      return PxRigidBody.wrapPointer(_getRigidBody(this.address));
   }

   private static native long _getRigidBody(long var0);

   public void setRigidBody(PxRigidBody value) {
      this.checkNotNull();
      _setRigidBody(this.address, value.getAddress());
   }

   private static native void _setRigidBody(long var0, long var2);

   public PxShape getWheelShapes(int index) {
      this.checkNotNull();
      return PxShape.wrapPointer(_getWheelShapes(this.address, index));
   }

   private static native long _getWheelShapes(long var0, int var2);

   public void setWheelShapes(int index, PxShape value) {
      this.checkNotNull();
      _setWheelShapes(this.address, index, value.getAddress());
   }

   private static native void _setWheelShapes(long var0, int var2, long var3);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
