package physx.extensions;

public class PxDistanceJoint extends PxJoint {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxDistanceJoint() {
   }

   private static native int __sizeOf();

   public static PxDistanceJoint wrapPointer(long address) {
      return address != 0L ? new PxDistanceJoint(address) : null;
   }

   public static PxDistanceJoint arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxDistanceJoint(long address) {
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

   public float getDistance() {
      this.checkNotNull();
      return _getDistance(this.address);
   }

   private static native float _getDistance(long var0);

   public void setMinDistance(float distance) {
      this.checkNotNull();
      _setMinDistance(this.address, distance);
   }

   private static native void _setMinDistance(long var0, float var2);

   public float getMinDistance() {
      this.checkNotNull();
      return _getMinDistance(this.address);
   }

   private static native float _getMinDistance(long var0);

   public void setMaxDistance(float distance) {
      this.checkNotNull();
      _setMaxDistance(this.address, distance);
   }

   private static native void _setMaxDistance(long var0, float var2);

   public float getMaxDistance() {
      this.checkNotNull();
      return _getMaxDistance(this.address);
   }

   private static native float _getMaxDistance(long var0);

   public void setTolerance(float tolerance) {
      this.checkNotNull();
      _setTolerance(this.address, tolerance);
   }

   private static native void _setTolerance(long var0, float var2);

   public float getTolerance() {
      this.checkNotNull();
      return _getTolerance(this.address);
   }

   private static native float _getTolerance(long var0);

   public void setStiffness(float stiffness) {
      this.checkNotNull();
      _setStiffness(this.address, stiffness);
   }

   private static native void _setStiffness(long var0, float var2);

   public float getStiffness() {
      this.checkNotNull();
      return _getStiffness(this.address);
   }

   private static native float _getStiffness(long var0);

   public void setDamping(float damping) {
      this.checkNotNull();
      _setDamping(this.address, damping);
   }

   private static native void _setDamping(long var0, float var2);

   public float getDamping() {
      this.checkNotNull();
      return _getDamping(this.address);
   }

   private static native float _getDamping(long var0);

   public void setDistanceJointFlags(PxDistanceJointFlags flags) {
      this.checkNotNull();
      _setDistanceJointFlags(this.address, flags.getAddress());
   }

   private static native void _setDistanceJointFlags(long var0, long var2);

   public void setDistanceJointFlag(PxDistanceJointFlagEnum flag, boolean value) {
      this.checkNotNull();
      _setDistanceJointFlag(this.address, flag.value, value);
   }

   private static native void _setDistanceJointFlag(long var0, int var2, boolean var3);

   public PxDistanceJointFlags getDistanceJointFlags() {
      this.checkNotNull();
      return PxDistanceJointFlags.wrapPointer(_getDistanceJointFlags(this.address));
   }

   private static native long _getDistanceJointFlags(long var0);
}
