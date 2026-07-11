package physx.physics;

public class PxOverlapHit extends PxQueryHit {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxOverlapHit() {
   }

   private static native int __sizeOf();

   public static PxOverlapHit wrapPointer(long address) {
      return address != 0L ? new PxOverlapHit(address) : null;
   }

   public static PxOverlapHit arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxOverlapHit(long address) {
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

   public PxRigidActor getActor() {
      this.checkNotNull();
      return PxRigidActor.wrapPointer(_getActor(this.address));
   }

   private static native long _getActor(long var0);

   public void setActor(PxRigidActor value) {
      this.checkNotNull();
      _setActor(this.address, value.getAddress());
   }

   private static native void _setActor(long var0, long var2);

   public PxShape getShape() {
      this.checkNotNull();
      return PxShape.wrapPointer(_getShape(this.address));
   }

   private static native long _getShape(long var0);

   public void setShape(PxShape value) {
      this.checkNotNull();
      _setShape(this.address, value.getAddress());
   }

   private static native void _setShape(long var0, long var2);
}
