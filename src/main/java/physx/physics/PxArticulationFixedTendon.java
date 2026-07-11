package physx.physics;

public class PxArticulationFixedTendon extends PxArticulationTendon {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxArticulationFixedTendon() {
   }

   private static native int __sizeOf();

   public static PxArticulationFixedTendon wrapPointer(long address) {
      return address != 0L ? new PxArticulationFixedTendon(address) : null;
   }

   public static PxArticulationFixedTendon arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxArticulationFixedTendon(long address) {
      super(address);
   }

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

   public PxArticulationTendonJoint createTendonJoint(
      PxArticulationTendonJoint parent, PxArticulationAxisEnum axis, float coefficient, float recipCoefficient, PxArticulationLink link
   ) {
      this.checkNotNull();
      return PxArticulationTendonJoint.wrapPointer(
         _createTendonJoint(this.address, parent.getAddress(), axis.value, coefficient, recipCoefficient, link.getAddress())
      );
   }

   private static native long _createTendonJoint(long var0, long var2, int var4, float var5, float var6, long var7);

   public int getNbTendonJoints() {
      this.checkNotNull();
      return _getNbTendonJoints(this.address);
   }

   private static native int _getNbTendonJoints(long var0);

   public void setRestLength(float restLength) {
      this.checkNotNull();
      _setRestLength(this.address, restLength);
   }

   private static native void _setRestLength(long var0, float var2);

   public float getRestLength() {
      this.checkNotNull();
      return _getRestLength(this.address);
   }

   private static native float _getRestLength(long var0);

   public void setLimitParameters(PxArticulationTendonLimit parameter) {
      this.checkNotNull();
      _setLimitParameters(this.address, parameter.getAddress());
   }

   private static native void _setLimitParameters(long var0, long var2);

   public PxArticulationTendonLimit getLimitParameters() {
      this.checkNotNull();
      return PxArticulationTendonLimit.wrapPointer(_getLimitParameters(this.address));
   }

   private static native long _getLimitParameters(long var0);
}
