package physx.character;

import physx.physics.PxActor;
import physx.physics.PxShape;

public class PxControllerBehaviorCallbackImpl extends SimpleControllerBehaviorCallback {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxControllerBehaviorCallbackImpl wrapPointer(long address) {
      return address != 0L ? new PxControllerBehaviorCallbackImpl(address) : null;
   }

   public static PxControllerBehaviorCallbackImpl arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxControllerBehaviorCallbackImpl(long address) {
      super(address);
   }

   protected PxControllerBehaviorCallbackImpl() {
      this.address = this._PxControllerBehaviorCallbackImpl();
   }

   private native long _PxControllerBehaviorCallbackImpl();

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

   private int _getShapeBehaviorFlags(long shape, long actor) {
      return this.getShapeBehaviorFlags(PxShape.wrapPointer(shape), PxActor.wrapPointer(actor));
   }

   @Override
   public int getShapeBehaviorFlags(PxShape shape, PxActor actor) {
      return 0;
   }

   private int _getControllerBehaviorFlags(long controller) {
      return this.getControllerBehaviorFlags(PxController.wrapPointer(controller));
   }

   @Override
   public int getControllerBehaviorFlags(PxController controller) {
      return 0;
   }

   private int _getObstacleBehaviorFlags(long obstacle) {
      return this.getObstacleBehaviorFlags(PxObstacle.wrapPointer(obstacle));
   }

   @Override
   public int getObstacleBehaviorFlags(PxObstacle obstacle) {
      return 0;
   }
}
