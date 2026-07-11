package physx.physics;

import physx.NativeObject;
import physx.common.PxBounds3;

public class PxBroadPhaseRegion extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxBroadPhaseRegion wrapPointer(long address) {
      return address != 0L ? new PxBroadPhaseRegion(address) : null;
   }

   public static PxBroadPhaseRegion arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxBroadPhaseRegion(long address) {
      super(address);
   }

   public PxBroadPhaseRegion() {
      this.address = _PxBroadPhaseRegion();
   }

   private static native long _PxBroadPhaseRegion();

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

   public PxBounds3 getMBounds() {
      this.checkNotNull();
      return PxBounds3.wrapPointer(_getMBounds(this.address));
   }

   private static native long _getMBounds(long var0);

   public void setMBounds(PxBounds3 value) {
      this.checkNotNull();
      _setMBounds(this.address, value.getAddress());
   }

   private static native void _setMBounds(long var0, long var2);

   public NativeObject getMUserData() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_getMUserData(this.address));
   }

   private static native long _getMUserData(long var0);

   public void setMUserData(NativeObject value) {
      this.checkNotNull();
      _setMUserData(this.address, value.getAddress());
   }

   private static native void _setMUserData(long var0, long var2);
}
