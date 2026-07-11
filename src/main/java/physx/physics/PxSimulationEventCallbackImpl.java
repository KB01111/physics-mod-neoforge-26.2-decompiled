package physx.physics;

import physx.support.PxActorPtr;

public class PxSimulationEventCallbackImpl extends SimpleSimulationEventCallback {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxSimulationEventCallbackImpl wrapPointer(long address) {
      return address != 0L ? new PxSimulationEventCallbackImpl(address) : null;
   }

   public static PxSimulationEventCallbackImpl arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxSimulationEventCallbackImpl(long address) {
      super(address);
   }

   protected PxSimulationEventCallbackImpl() {
      this.address = this._PxSimulationEventCallbackImpl();
   }

   private native long _PxSimulationEventCallbackImpl();

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

   private void _onConstraintBreak(long constraints, int count) {
      this.onConstraintBreak(PxConstraintInfo.wrapPointer(constraints), count);
   }

   @Override
   public void onConstraintBreak(PxConstraintInfo constraints, int count) {
   }

   private void _onWake(long actors, int count) {
      this.onWake(PxActorPtr.wrapPointer(actors), count);
   }

   @Override
   public void onWake(PxActorPtr actors, int count) {
   }

   private void _onSleep(long actors, int count) {
      this.onSleep(PxActorPtr.wrapPointer(actors), count);
   }

   @Override
   public void onSleep(PxActorPtr actors, int count) {
   }

   private void _onContact(long pairHeader, long pairs, int nbPairs) {
      this.onContact(PxContactPairHeader.wrapPointer(pairHeader), PxContactPair.wrapPointer(pairs), nbPairs);
   }

   @Override
   public void onContact(PxContactPairHeader pairHeader, PxContactPair pairs, int nbPairs) {
   }

   private void _onTrigger(long pairs, int count) {
      this.onTrigger(PxTriggerPair.wrapPointer(pairs), count);
   }

   @Override
   public void onTrigger(PxTriggerPair pairs, int count) {
   }
}
