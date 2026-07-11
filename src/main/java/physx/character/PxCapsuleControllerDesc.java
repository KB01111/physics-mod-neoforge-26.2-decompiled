package physx.character;

import physx.NativeObject;

public class PxCapsuleControllerDesc extends PxControllerDesc {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxCapsuleControllerDesc wrapPointer(long address) {
      return address != 0L ? new PxCapsuleControllerDesc(address) : null;
   }

   public static PxCapsuleControllerDesc arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxCapsuleControllerDesc(long address) {
      super(address);
   }

   public static PxCapsuleControllerDesc createAt(long address) {
      __placement_new_PxCapsuleControllerDesc(address);
      PxCapsuleControllerDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxCapsuleControllerDesc createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxCapsuleControllerDesc(address);
      PxCapsuleControllerDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxCapsuleControllerDesc(long var0);

   public PxCapsuleControllerDesc() {
      this.address = _PxCapsuleControllerDesc();
   }

   private static native long _PxCapsuleControllerDesc();

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

   public float getRadius() {
      this.checkNotNull();
      return _getRadius(this.address);
   }

   private static native float _getRadius(long var0);

   public void setRadius(float value) {
      this.checkNotNull();
      _setRadius(this.address, value);
   }

   private static native void _setRadius(long var0, float var2);

   public float getHeight() {
      this.checkNotNull();
      return _getHeight(this.address);
   }

   private static native float _getHeight(long var0);

   public void setHeight(float value) {
      this.checkNotNull();
      _setHeight(this.address, value);
   }

   private static native void _setHeight(long var0, float var2);

   public PxCapsuleClimbingModeEnum getClimbingMode() {
      this.checkNotNull();
      return PxCapsuleClimbingModeEnum.forValue(_getClimbingMode(this.address));
   }

   private static native int _getClimbingMode(long var0);

   public void setClimbingMode(PxCapsuleClimbingModeEnum value) {
      this.checkNotNull();
      _setClimbingMode(this.address, value.value);
   }

   private static native void _setClimbingMode(long var0, int var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
