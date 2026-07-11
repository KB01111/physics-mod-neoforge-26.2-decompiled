package physx.physics;

import physx.NativeObject;

public class PxTriggerPair extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxTriggerPair() {
   }

   private static native int __sizeOf();

   public static PxTriggerPair wrapPointer(long address) {
      return address != 0L ? new PxTriggerPair(address) : null;
   }

   public static PxTriggerPair arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxTriggerPair(long address) {
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

   public PxShape getTriggerShape() {
      this.checkNotNull();
      return PxShape.wrapPointer(_getTriggerShape(this.address));
   }

   private static native long _getTriggerShape(long var0);

   public void setTriggerShape(PxShape value) {
      this.checkNotNull();
      _setTriggerShape(this.address, value.getAddress());
   }

   private static native void _setTriggerShape(long var0, long var2);

   public PxActor getTriggerActor() {
      this.checkNotNull();
      return PxActor.wrapPointer(_getTriggerActor(this.address));
   }

   private static native long _getTriggerActor(long var0);

   public void setTriggerActor(PxActor value) {
      this.checkNotNull();
      _setTriggerActor(this.address, value.getAddress());
   }

   private static native void _setTriggerActor(long var0, long var2);

   @Deprecated
   public PxShape getOtherShape() {
      this.checkNotNull();
      return PxShape.wrapPointer(_getOtherShape(this.address));
   }

   private static native long _getOtherShape(long var0);

   @Deprecated
   public void setOtherShape(PxShape value) {
      this.checkNotNull();
      _setOtherShape(this.address, value.getAddress());
   }

   private static native void _setOtherShape(long var0, long var2);

   public PxActor getOtherActor() {
      this.checkNotNull();
      return PxActor.wrapPointer(_getOtherActor(this.address));
   }

   private static native long _getOtherActor(long var0);

   public void setOtherActor(PxActor value) {
      this.checkNotNull();
      _setOtherActor(this.address, value.getAddress());
   }

   private static native void _setOtherActor(long var0, long var2);

   public PxPairFlagEnum getStatus() {
      this.checkNotNull();
      return PxPairFlagEnum.forValue(_getStatus(this.address));
   }

   private static native int _getStatus(long var0);

   public void setStatus(PxPairFlagEnum value) {
      this.checkNotNull();
      _setStatus(this.address, value.value);
   }

   private static native void _setStatus(long var0, int var2);

   public PxTriggerPairFlags getFlags() {
      this.checkNotNull();
      return PxTriggerPairFlags.wrapPointer(_getFlags(this.address));
   }

   private static native long _getFlags(long var0);

   public void setFlags(PxTriggerPairFlags value) {
      this.checkNotNull();
      _setFlags(this.address, value.getAddress());
   }

   private static native void _setFlags(long var0, long var2);
}
