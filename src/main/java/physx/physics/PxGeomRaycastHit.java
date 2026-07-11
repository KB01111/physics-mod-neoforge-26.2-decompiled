package physx.physics;

public class PxGeomRaycastHit extends PxLocationHit {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxGeomRaycastHit() {
   }

   private static native int __sizeOf();

   public static PxGeomRaycastHit wrapPointer(long address) {
      return address != 0L ? new PxGeomRaycastHit(address) : null;
   }

   public static PxGeomRaycastHit arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxGeomRaycastHit(long address) {
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

   public float getU() {
      this.checkNotNull();
      return _getU(this.address);
   }

   private static native float _getU(long var0);

   public void setU(float value) {
      this.checkNotNull();
      _setU(this.address, value);
   }

   private static native void _setU(long var0, float var2);

   public float getV() {
      this.checkNotNull();
      return _getV(this.address);
   }

   private static native float _getV(long var0);

   public void setV(float value) {
      this.checkNotNull();
      _setV(this.address, value);
   }

   private static native void _setV(long var0, float var2);

   public boolean hadInitialOverlap() {
      this.checkNotNull();
      return _hadInitialOverlap(this.address);
   }

   private static native boolean _hadInitialOverlap(long var0);
}
