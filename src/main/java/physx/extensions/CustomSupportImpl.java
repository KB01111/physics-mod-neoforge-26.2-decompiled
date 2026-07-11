package physx.extensions;

import physx.common.PxVec3;

public class CustomSupportImpl extends CustomSupport {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static CustomSupportImpl wrapPointer(long address) {
      return address != 0L ? new CustomSupportImpl(address) : null;
   }

   public static CustomSupportImpl arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected CustomSupportImpl(long address) {
      super(address);
   }

   protected CustomSupportImpl() {
      this.address = this._CustomSupportImpl();
   }

   private native long _CustomSupportImpl();

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

   private float _getCustomMargin() {
      return this.getCustomMargin();
   }

   @Override
   public float getCustomMargin() {
      return 0.0F;
   }

   private void _getCustomSupportLocal(long dir, long result) {
      this.getCustomSupportLocal(PxVec3.wrapPointer(dir), PxVec3.wrapPointer(result));
   }

   @Override
   public void getCustomSupportLocal(PxVec3 dir, PxVec3 result) {
   }
}
