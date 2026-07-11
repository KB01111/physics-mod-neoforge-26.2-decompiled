package physx.physics;

import physx.NativeObject;

public class PxContactPairHeader extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxContactPairHeader() {
   }

   private static native int __sizeOf();

   public static PxContactPairHeader wrapPointer(long address) {
      return address != 0L ? new PxContactPairHeader(address) : null;
   }

   public static PxContactPairHeader arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxContactPairHeader(long address) {
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

   public PxActor getActors(int index) {
      this.checkNotNull();
      return PxActor.wrapPointer(_getActors(this.address, index));
   }

   private static native long _getActors(long var0, int var2);

   public void setActors(int index, PxActor value) {
      this.checkNotNull();
      _setActors(this.address, index, value.getAddress());
   }

   private static native void _setActors(long var0, int var2, long var3);

   public PxContactPairHeaderFlags getFlags() {
      this.checkNotNull();
      return PxContactPairHeaderFlags.wrapPointer(_getFlags(this.address));
   }

   private static native long _getFlags(long var0);

   public void setFlags(PxContactPairHeaderFlags value) {
      this.checkNotNull();
      _setFlags(this.address, value.getAddress());
   }

   private static native void _setFlags(long var0, long var2);

   public PxContactPair getPairs() {
      this.checkNotNull();
      return PxContactPair.wrapPointer(_getPairs(this.address));
   }

   private static native long _getPairs(long var0);

   public void setPairs(PxContactPair value) {
      this.checkNotNull();
      _setPairs(this.address, value.getAddress());
   }

   private static native void _setPairs(long var0, long var2);

   public int getNbPairs() {
      this.checkNotNull();
      return _getNbPairs(this.address);
   }

   private static native int _getNbPairs(long var0);

   public void setNbPairs(int value) {
      this.checkNotNull();
      _setNbPairs(this.address, value);
   }

   private static native void _setNbPairs(long var0, int var2);
}
