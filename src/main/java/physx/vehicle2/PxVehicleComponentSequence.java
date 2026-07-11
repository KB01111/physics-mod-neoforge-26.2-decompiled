package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleComponentSequence extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleComponentSequence wrapPointer(long address) {
      return address != 0L ? new PxVehicleComponentSequence(address) : null;
   }

   public static PxVehicleComponentSequence arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleComponentSequence(long address) {
      super(address);
   }

   public PxVehicleComponentSequence() {
      this.address = _PxVehicleComponentSequence();
   }

   private static native long _PxVehicleComponentSequence();

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

   public boolean add(PxVehicleComponent component) {
      this.checkNotNull();
      return _add(this.address, component.getAddress());
   }

   private static native boolean _add(long var0, long var2);

   public byte beginSubstepGroup() {
      this.checkNotNull();
      return _beginSubstepGroup(this.address);
   }

   private static native byte _beginSubstepGroup(long var0);

   public byte beginSubstepGroup(byte nbSubSteps) {
      this.checkNotNull();
      return _beginSubstepGroup(this.address, nbSubSteps);
   }

   private static native byte _beginSubstepGroup(long var0, byte var2);

   public void endSubstepGroup() {
      this.checkNotNull();
      _endSubstepGroup(this.address);
   }

   private static native void _endSubstepGroup(long var0);

   public void setSubsteps(byte subGroupHandle, byte nbSteps) {
      this.checkNotNull();
      _setSubsteps(this.address, subGroupHandle, nbSteps);
   }

   private static native void _setSubsteps(long var0, byte var2, byte var3);

   public void update(float dt, PxVehicleSimulationContext context) {
      this.checkNotNull();
      _update(this.address, dt, context.getAddress());
   }

   private static native void _update(long var0, float var2, long var3);
}
