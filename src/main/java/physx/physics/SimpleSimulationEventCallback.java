package physx.physics;

import physx.support.PxActorPtr;

public class SimpleSimulationEventCallback extends PxSimulationEventCallback {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected SimpleSimulationEventCallback() {
   }

   private static native int __sizeOf();

   public static SimpleSimulationEventCallback wrapPointer(long address) {
      return address != 0L ? new SimpleSimulationEventCallback(address) : null;
   }

   public static SimpleSimulationEventCallback arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected SimpleSimulationEventCallback(long address) {
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

   public void onConstraintBreak(PxConstraintInfo constraints, int count) {
      this.checkNotNull();
      _onConstraintBreak(this.address, constraints.getAddress(), count);
   }

   private static native void _onConstraintBreak(long var0, long var2, int var4);

   public void onWake(PxActorPtr actors, int count) {
      this.checkNotNull();
      _onWake(this.address, actors.getAddress(), count);
   }

   private static native void _onWake(long var0, long var2, int var4);

   public void onSleep(PxActorPtr actors, int count) {
      this.checkNotNull();
      _onSleep(this.address, actors.getAddress(), count);
   }

   private static native void _onSleep(long var0, long var2, int var4);

   public void onContact(PxContactPairHeader pairHeader, PxContactPair pairs, int nbPairs) {
      this.checkNotNull();
      _onContact(this.address, pairHeader.getAddress(), pairs.getAddress(), nbPairs);
   }

   private static native void _onContact(long var0, long var2, long var4, int var6);

   public void onTrigger(PxTriggerPair pairs, int count) {
      this.checkNotNull();
      _onTrigger(this.address, pairs.getAddress(), count);
   }

   private static native void _onTrigger(long var0, long var2, int var4);
}
