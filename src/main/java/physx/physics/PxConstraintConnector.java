package physx.physics;

import physx.NativeObject;
import physx.common.PxBase;
import physx.common.PxVec3;

public class PxConstraintConnector extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxConstraintConnector() {
   }

   private static native int __sizeOf();

   public static PxConstraintConnector wrapPointer(long address) {
      return address != 0L ? new PxConstraintConnector(address) : null;
   }

   public static PxConstraintConnector arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxConstraintConnector(long address) {
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

   public void prepareData() {
      this.checkNotNull();
      _prepareData(this.address);
   }

   private static native void _prepareData(long var0);

   public void updateOmniPvdProperties() {
      this.checkNotNull();
      _updateOmniPvdProperties(this.address);
   }

   private static native void _updateOmniPvdProperties(long var0);

   public void onConstraintRelease() {
      this.checkNotNull();
      _onConstraintRelease(this.address);
   }

   private static native void _onConstraintRelease(long var0);

   public void onComShift(int actor) {
      this.checkNotNull();
      _onComShift(this.address, actor);
   }

   private static native void _onComShift(long var0, int var2);

   public void onOriginShift(PxVec3 shift) {
      this.checkNotNull();
      _onOriginShift(this.address, shift.getAddress());
   }

   private static native void _onOriginShift(long var0, long var2);

   public PxBase getSerializable() {
      this.checkNotNull();
      return PxBase.wrapPointer(_getSerializable(this.address));
   }

   private static native long _getSerializable(long var0);

   public PxConstraintSolverPrep getPrep() {
      this.checkNotNull();
      return PxConstraintSolverPrep.wrapPointer(_getPrep(this.address));
   }

   private static native long _getPrep(long var0);

   public void getConstantBlock() {
      this.checkNotNull();
      _getConstantBlock(this.address);
   }

   private static native void _getConstantBlock(long var0);

   public void connectToConstraint(PxConstraint constraint) {
      this.checkNotNull();
      _connectToConstraint(this.address, constraint.getAddress());
   }

   private static native void _connectToConstraint(long var0, long var2);
}
