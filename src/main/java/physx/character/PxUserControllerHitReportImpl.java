package physx.character;

public class PxUserControllerHitReportImpl extends PxUserControllerHitReport {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxUserControllerHitReportImpl wrapPointer(long address) {
      return address != 0L ? new PxUserControllerHitReportImpl(address) : null;
   }

   public static PxUserControllerHitReportImpl arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxUserControllerHitReportImpl(long address) {
      super(address);
   }

   protected PxUserControllerHitReportImpl() {
      this.address = this._PxUserControllerHitReportImpl();
   }

   private native long _PxUserControllerHitReportImpl();

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

   private void _onShapeHit(long hit) {
      this.onShapeHit(PxControllerShapeHit.wrapPointer(hit));
   }

   @Override
   public void onShapeHit(PxControllerShapeHit hit) {
   }

   private void _onControllerHit(long hit) {
      this.onControllerHit(PxControllersHit.wrapPointer(hit));
   }

   @Override
   public void onControllerHit(PxControllersHit hit) {
   }

   private void _onObstacleHit(long hit) {
      this.onObstacleHit(PxControllerObstacleHit.wrapPointer(hit));
   }

   @Override
   public void onObstacleHit(PxControllerObstacleHit hit) {
   }
}
