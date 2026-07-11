package physx.physics;

import physx.common.PxBase;
import physx.common.PxVec3;

public class PxConstraint extends PxBase {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxConstraint() {
   }

   private static native int __sizeOf();

   public static PxConstraint wrapPointer(long address) {
      return address != 0L ? new PxConstraint(address) : null;
   }

   public static PxConstraint arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxConstraint(long address) {
      super(address);
   }

   public PxScene getScene() {
      this.checkNotNull();
      return PxScene.wrapPointer(_getScene(this.address));
   }

   private static native long _getScene(long var0);

   public void setActors(PxRigidActor actor0, PxRigidActor actor1) {
      this.checkNotNull();
      _setActors(this.address, actor0.getAddress(), actor1.getAddress());
   }

   private static native void _setActors(long var0, long var2, long var4);

   public void markDirty() {
      this.checkNotNull();
      _markDirty(this.address);
   }

   private static native void _markDirty(long var0);

   public void setFlags(PxConstraintFlags flags) {
      this.checkNotNull();
      _setFlags(this.address, flags.getAddress());
   }

   private static native void _setFlags(long var0, long var2);

   public PxConstraintFlags getFlags() {
      this.checkNotNull();
      return PxConstraintFlags.wrapPointer(_getFlags(this.address));
   }

   private static native long _getFlags(long var0);

   public void setFlag(PxConstraintFlagEnum flag, boolean value) {
      this.checkNotNull();
      _setFlag(this.address, flag.value, value);
   }

   private static native void _setFlag(long var0, int var2, boolean var3);

   public void getForce(PxVec3 linear, PxVec3 angular) {
      this.checkNotNull();
      _getForce(this.address, linear.getAddress(), angular.getAddress());
   }

   private static native void _getForce(long var0, long var2, long var4);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);

   public void setBreakForce(float linear, float angular) {
      this.checkNotNull();
      _setBreakForce(this.address, linear, angular);
   }

   private static native void _setBreakForce(long var0, float var2, float var3);

   public void setMinResponseThreshold(float threshold) {
      this.checkNotNull();
      _setMinResponseThreshold(this.address, threshold);
   }

   private static native void _setMinResponseThreshold(long var0, float var2);

   public float getMinResponseThreshold() {
      this.checkNotNull();
      return _getMinResponseThreshold(this.address);
   }

   private static native float _getMinResponseThreshold(long var0);
}
