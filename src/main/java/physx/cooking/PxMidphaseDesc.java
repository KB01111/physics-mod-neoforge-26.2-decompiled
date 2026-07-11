package physx.cooking;

import physx.NativeObject;

public class PxMidphaseDesc extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxMidphaseDesc wrapPointer(long address) {
      return address != 0L ? new PxMidphaseDesc(address) : null;
   }

   public static PxMidphaseDesc arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxMidphaseDesc(long address) {
      super(address);
   }

   public PxMidphaseDesc() {
      this.address = _PxMidphaseDesc();
   }

   private static native long _PxMidphaseDesc();

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

   public PxBVH33MidphaseDesc getMBVH33Desc() {
      this.checkNotNull();
      return PxBVH33MidphaseDesc.wrapPointer(_getMBVH33Desc(this.address));
   }

   private static native long _getMBVH33Desc(long var0);

   public void setMBVH33Desc(PxBVH33MidphaseDesc value) {
      this.checkNotNull();
      _setMBVH33Desc(this.address, value.getAddress());
   }

   private static native void _setMBVH33Desc(long var0, long var2);

   public PxBVH34MidphaseDesc getMBVH34Desc() {
      this.checkNotNull();
      return PxBVH34MidphaseDesc.wrapPointer(_getMBVH34Desc(this.address));
   }

   private static native long _getMBVH34Desc(long var0);

   public void setMBVH34Desc(PxBVH34MidphaseDesc value) {
      this.checkNotNull();
      _setMBVH34Desc(this.address, value.getAddress());
   }

   private static native void _setMBVH34Desc(long var0, long var2);

   public PxMeshMidPhaseEnum getType() {
      this.checkNotNull();
      return PxMeshMidPhaseEnum.forValue(_getType(this.address));
   }

   private static native int _getType(long var0);

   public void setToDefault(PxMeshMidPhaseEnum type) {
      this.checkNotNull();
      _setToDefault(this.address, type.value);
   }

   private static native void _setToDefault(long var0, int var2);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
