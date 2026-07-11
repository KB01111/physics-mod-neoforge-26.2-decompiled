package physx.physics;

import physx.NativeObject;
import physx.common.PxBase;
import physx.common.PxBounds3;

public class PxActor extends PxBase {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxActor() {
   }

   private static native int __sizeOf();

   public static PxActor wrapPointer(long address) {
      return address != 0L ? new PxActor(address) : null;
   }

   public static PxActor arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxActor(long address) {
      super(address);
   }

   public NativeObject getUserData() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_getUserData(this.address));
   }

   private static native long _getUserData(long var0);

   public void setUserData(NativeObject value) {
      this.checkNotNull();
      _setUserData(this.address, value.getAddress());
   }

   private static native void _setUserData(long var0, long var2);

   public PxActorTypeEnum getType() {
      this.checkNotNull();
      return PxActorTypeEnum.forValue(_getType(this.address));
   }

   private static native int _getType(long var0);

   public PxScene getScene() {
      this.checkNotNull();
      return PxScene.wrapPointer(_getScene(this.address));
   }

   private static native long _getScene(long var0);

   public void setName(String name) {
      this.checkNotNull();
      _setName(this.address, name);
   }

   private static native void _setName(long var0, String var2);

   public String getName() {
      this.checkNotNull();
      return _getName(this.address);
   }

   private static native String _getName(long var0);

   public PxBounds3 getWorldBounds() {
      this.checkNotNull();
      return PxBounds3.wrapPointer(_getWorldBounds(this.address));
   }

   private static native long _getWorldBounds(long var0);

   public PxBounds3 getWorldBounds(float inflation) {
      this.checkNotNull();
      return PxBounds3.wrapPointer(_getWorldBounds(this.address, inflation));
   }

   private static native long _getWorldBounds(long var0, float var2);

   public void setActorFlag(PxActorFlagEnum flag, boolean value) {
      this.checkNotNull();
      _setActorFlag(this.address, flag.value, value);
   }

   private static native void _setActorFlag(long var0, int var2, boolean var3);

   public void setActorFlags(PxActorFlags flags) {
      this.checkNotNull();
      _setActorFlags(this.address, flags.getAddress());
   }

   private static native void _setActorFlags(long var0, long var2);

   public PxActorFlags getActorFlags() {
      this.checkNotNull();
      return PxActorFlags.wrapPointer(_getActorFlags(this.address));
   }

   private static native long _getActorFlags(long var0);

   public void setDominanceGroup(byte dominanceGroup) {
      this.checkNotNull();
      _setDominanceGroup(this.address, dominanceGroup);
   }

   private static native void _setDominanceGroup(long var0, byte var2);

   public byte getDominanceGroup() {
      this.checkNotNull();
      return _getDominanceGroup(this.address);
   }

   private static native byte _getDominanceGroup(long var0);

   public void setOwnerClient(byte inClient) {
      this.checkNotNull();
      _setOwnerClient(this.address, inClient);
   }

   private static native void _setOwnerClient(long var0, byte var2);

   public byte getOwnerClient() {
      this.checkNotNull();
      return _getOwnerClient(this.address);
   }

   private static native byte _getOwnerClient(long var0);
}
