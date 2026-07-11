package physx.extensions;

public class PxPrismaticJoint extends PxJoint {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxPrismaticJoint() {
   }

   private static native int __sizeOf();

   public static PxPrismaticJoint wrapPointer(long address) {
      return address != 0L ? new PxPrismaticJoint(address) : null;
   }

   public static PxPrismaticJoint arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxPrismaticJoint(long address) {
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

   public float getPosition() {
      this.checkNotNull();
      return _getPosition(this.address);
   }

   private static native float _getPosition(long var0);

   public float getVelocity() {
      this.checkNotNull();
      return _getVelocity(this.address);
   }

   private static native float _getVelocity(long var0);

   public void setLimit(PxJointLinearLimitPair limit) {
      this.checkNotNull();
      _setLimit(this.address, limit.getAddress());
   }

   private static native void _setLimit(long var0, long var2);

   public void setPrismaticJointFlags(PxPrismaticJointFlags flags) {
      this.checkNotNull();
      _setPrismaticJointFlags(this.address, flags.getAddress());
   }

   private static native void _setPrismaticJointFlags(long var0, long var2);

   public void setPrismaticJointFlag(PxPrismaticJointFlagEnum flag, boolean value) {
      this.checkNotNull();
      _setPrismaticJointFlag(this.address, flag.value, value);
   }

   private static native void _setPrismaticJointFlag(long var0, int var2, boolean var3);

   public PxPrismaticJointFlags getPrismaticJointFlags() {
      this.checkNotNull();
      return PxPrismaticJointFlags.wrapPointer(_getPrismaticJointFlags(this.address));
   }

   private static native long _getPrismaticJointFlags(long var0);
}
