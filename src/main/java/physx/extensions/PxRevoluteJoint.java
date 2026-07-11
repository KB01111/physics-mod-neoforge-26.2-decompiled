package physx.extensions;

public class PxRevoluteJoint extends PxJoint {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxRevoluteJoint() {
   }

   private static native int __sizeOf();

   public static PxRevoluteJoint wrapPointer(long address) {
      return address != 0L ? new PxRevoluteJoint(address) : null;
   }

   public static PxRevoluteJoint arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxRevoluteJoint(long address) {
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

   public float getAngle() {
      this.checkNotNull();
      return _getAngle(this.address);
   }

   private static native float _getAngle(long var0);

   public float getVelocity() {
      this.checkNotNull();
      return _getVelocity(this.address);
   }

   private static native float _getVelocity(long var0);

   public void setLimit(PxJointAngularLimitPair limits) {
      this.checkNotNull();
      _setLimit(this.address, limits.getAddress());
   }

   private static native void _setLimit(long var0, long var2);

   public void setDriveVelocity(float velocity) {
      this.checkNotNull();
      _setDriveVelocity(this.address, velocity);
   }

   private static native void _setDriveVelocity(long var0, float var2);

   public void setDriveVelocity(float velocity, boolean autowake) {
      this.checkNotNull();
      _setDriveVelocity(this.address, velocity, autowake);
   }

   private static native void _setDriveVelocity(long var0, float var2, boolean var3);

   public float getDriveVelocity() {
      this.checkNotNull();
      return _getDriveVelocity(this.address);
   }

   private static native float _getDriveVelocity(long var0);

   public void setDriveForceLimit(float limit) {
      this.checkNotNull();
      _setDriveForceLimit(this.address, limit);
   }

   private static native void _setDriveForceLimit(long var0, float var2);

   public float getDriveForceLimit() {
      this.checkNotNull();
      return _getDriveForceLimit(this.address);
   }

   private static native float _getDriveForceLimit(long var0);

   public void setDriveGearRatio(float ratio) {
      this.checkNotNull();
      _setDriveGearRatio(this.address, ratio);
   }

   private static native void _setDriveGearRatio(long var0, float var2);

   public float getDriveGearRatio() {
      this.checkNotNull();
      return _getDriveGearRatio(this.address);
   }

   private static native float _getDriveGearRatio(long var0);

   public void setRevoluteJointFlags(PxRevoluteJointFlags flags) {
      this.checkNotNull();
      _setRevoluteJointFlags(this.address, flags.getAddress());
   }

   private static native void _setRevoluteJointFlags(long var0, long var2);

   public void setRevoluteJointFlag(PxRevoluteJointFlagEnum flag, boolean value) {
      this.checkNotNull();
      _setRevoluteJointFlag(this.address, flag.value, value);
   }

   private static native void _setRevoluteJointFlag(long var0, int var2, boolean var3);

   public PxRevoluteJointFlags getRevoluteJointFlags() {
      this.checkNotNull();
      return PxRevoluteJointFlags.wrapPointer(_getRevoluteJointFlags(this.address));
   }

   private static native long _getRevoluteJointFlags(long var0);
}
