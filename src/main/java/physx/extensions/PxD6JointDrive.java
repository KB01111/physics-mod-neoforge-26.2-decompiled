package physx.extensions;

public class PxD6JointDrive extends PxSpring {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxD6JointDrive wrapPointer(long address) {
      return address != 0L ? new PxD6JointDrive(address) : null;
   }

   public static PxD6JointDrive arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxD6JointDrive(long address) {
      super(address);
   }

   public PxD6JointDrive() {
      this.address = _PxD6JointDrive();
   }

   private static native long _PxD6JointDrive();

   public PxD6JointDrive(float driveStiffness, float driveDamping, float driveForceLimit) {
      this.address = _PxD6JointDrive(driveStiffness, driveDamping, driveForceLimit);
   }

   private static native long _PxD6JointDrive(float var0, float var1, float var2);

   public PxD6JointDrive(float driveStiffness, float driveDamping, float driveForceLimit, boolean isAcceleration) {
      this.address = _PxD6JointDrive(driveStiffness, driveDamping, driveForceLimit, isAcceleration);
   }

   private static native long _PxD6JointDrive(float var0, float var1, float var2, boolean var3);

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

   public float getForceLimit() {
      this.checkNotNull();
      return _getForceLimit(this.address);
   }

   private static native float _getForceLimit(long var0);

   public void setForceLimit(float value) {
      this.checkNotNull();
      _setForceLimit(this.address, value);
   }

   private static native void _setForceLimit(long var0, float var2);

   public PxD6JointDriveFlags getFlags() {
      this.checkNotNull();
      return PxD6JointDriveFlags.wrapPointer(_getFlags(this.address));
   }

   private static native long _getFlags(long var0);

   public void setFlags(PxD6JointDriveFlags value) {
      this.checkNotNull();
      _setFlags(this.address, value.getAddress());
   }

   private static native void _setFlags(long var0, long var2);
}
