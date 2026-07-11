package physx.character;

import physx.physics.PxActor;
import physx.physics.PxShape;

public class SimpleControllerBehaviorCallback extends PxControllerBehaviorCallback {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected SimpleControllerBehaviorCallback() {
   }

   private static native int __sizeOf();

   public static SimpleControllerBehaviorCallback wrapPointer(long address) {
      return address != 0L ? new SimpleControllerBehaviorCallback(address) : null;
   }

   public static SimpleControllerBehaviorCallback arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected SimpleControllerBehaviorCallback(long address) {
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

   public int getShapeBehaviorFlags(PxShape shape, PxActor actor) {
      this.checkNotNull();
      return _getShapeBehaviorFlags(this.address, shape.getAddress(), actor.getAddress());
   }

   private static native int _getShapeBehaviorFlags(long var0, long var2, long var4);

   public int getControllerBehaviorFlags(PxController controller) {
      this.checkNotNull();
      return _getControllerBehaviorFlags(this.address, controller.getAddress());
   }

   private static native int _getControllerBehaviorFlags(long var0, long var2);

   public int getObstacleBehaviorFlags(PxObstacle obstacle) {
      this.checkNotNull();
      return _getObstacleBehaviorFlags(this.address, obstacle.getAddress());
   }

   private static native int _getObstacleBehaviorFlags(long var0, long var2);
}
