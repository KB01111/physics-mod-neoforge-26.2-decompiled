package physx.physics;

public class PxRigidStatic extends PxRigidActor {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxRigidStatic() {
   }

   private static native int __sizeOf();

   public static PxRigidStatic wrapPointer(long address) {
      return address != 0L ? new PxRigidStatic(address) : null;
   }

   public static PxRigidStatic arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxRigidStatic(long address) {
      super(address);
   }
}
