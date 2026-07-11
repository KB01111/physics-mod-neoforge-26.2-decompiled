package physx.physics;

public class SimpleQueryFilterCallback extends PxQueryFilterCallback {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected SimpleQueryFilterCallback() {
   }

   private static native int __sizeOf();

   public static SimpleQueryFilterCallback wrapPointer(long address) {
      return address != 0L ? new SimpleQueryFilterCallback(address) : null;
   }

   public static SimpleQueryFilterCallback arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected SimpleQueryFilterCallback(long address) {
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

   public int simplePreFilter(PxFilterData filterData, PxShape shape, PxRigidActor actor, PxHitFlags queryFlags) {
      this.checkNotNull();
      return _simplePreFilter(this.address, filterData.getAddress(), shape.getAddress(), actor.getAddress(), queryFlags.getAddress());
   }

   private static native int _simplePreFilter(long var0, long var2, long var4, long var6, long var8);

   public int simplePostFilter(PxFilterData filterData, PxQueryHit hit, PxShape shape, PxRigidActor actor) {
      this.checkNotNull();
      return _simplePostFilter(this.address, filterData.getAddress(), hit.getAddress(), shape.getAddress(), actor.getAddress());
   }

   private static native int _simplePostFilter(long var0, long var2, long var4, long var6, long var8);
}
