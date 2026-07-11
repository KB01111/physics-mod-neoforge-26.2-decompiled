package physx.geometry;

import physx.NativeObject;

public class PxGeometry extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxGeometry() {
   }

   private static native int __sizeOf();

   public static PxGeometry wrapPointer(long address) {
      return address != 0L ? new PxGeometry(address) : null;
   }

   public static PxGeometry arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxGeometry(long address) {
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

   public PxGeometryTypeEnum getType() {
      this.checkNotNull();
      return PxGeometryTypeEnum.forValue(_getType(this.address));
   }

   private static native int _getType(long var0);
}
