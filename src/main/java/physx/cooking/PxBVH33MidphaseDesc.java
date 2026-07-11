package physx.cooking;

import physx.NativeObject;

public class PxBVH33MidphaseDesc extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxBVH33MidphaseDesc() {
   }

   private static native int __sizeOf();

   public static PxBVH33MidphaseDesc wrapPointer(long address) {
      return address != 0L ? new PxBVH33MidphaseDesc(address) : null;
   }

   public static PxBVH33MidphaseDesc arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxBVH33MidphaseDesc(long address) {
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

   public float getMeshSizePerformanceTradeOff() {
      this.checkNotNull();
      return _getMeshSizePerformanceTradeOff(this.address);
   }

   private static native float _getMeshSizePerformanceTradeOff(long var0);

   public void setMeshSizePerformanceTradeOff(float value) {
      this.checkNotNull();
      _setMeshSizePerformanceTradeOff(this.address, value);
   }

   private static native void _setMeshSizePerformanceTradeOff(long var0, float var2);

   public PxMeshCookingHintEnum getMeshCookingHint() {
      this.checkNotNull();
      return PxMeshCookingHintEnum.forValue(_getMeshCookingHint(this.address));
   }

   private static native int _getMeshCookingHint(long var0);

   public void setMeshCookingHint(PxMeshCookingHintEnum value) {
      this.checkNotNull();
      _setMeshCookingHint(this.address, value.value);
   }

   private static native void _setMeshCookingHint(long var0, int var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
