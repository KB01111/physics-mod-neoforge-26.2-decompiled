package physx.extensions;

public class PxSphericalJoint extends PxJoint {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxSphericalJoint() {
   }

   private static native int __sizeOf();

   public static PxSphericalJoint wrapPointer(long address) {
      return address != 0L ? new PxSphericalJoint(address) : null;
   }

   public static PxSphericalJoint arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxSphericalJoint(long address) {
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

   public void setLimitCone(PxJointLimitCone limitCone) {
      this.checkNotNull();
      _setLimitCone(this.address, limitCone.getAddress());
   }

   private static native void _setLimitCone(long var0, long var2);

   public float getSwingYAngle() {
      this.checkNotNull();
      return _getSwingYAngle(this.address);
   }

   private static native float _getSwingYAngle(long var0);

   public float getSwingZAngle() {
      this.checkNotNull();
      return _getSwingZAngle(this.address);
   }

   private static native float _getSwingZAngle(long var0);

   public void setSphericalJointFlags(PxSphericalJointFlags flags) {
      this.checkNotNull();
      _setSphericalJointFlags(this.address, flags.getAddress());
   }

   private static native void _setSphericalJointFlags(long var0, long var2);

   public void setSphericalJointFlag(PxSphericalJointFlagEnum flag, boolean value) {
      this.checkNotNull();
      _setSphericalJointFlag(this.address, flag.value, value);
   }

   private static native void _setSphericalJointFlag(long var0, int var2, boolean var3);

   public PxSphericalJointFlags getSphericalJointFlags() {
      this.checkNotNull();
      return PxSphericalJointFlags.wrapPointer(_getSphericalJointFlags(this.address));
   }

   private static native long _getSphericalJointFlags(long var0);
}
