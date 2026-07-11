package physx.physics;

public class PxRaycastResult extends PxRaycastCallback {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxRaycastResult wrapPointer(long address) {
      return address != 0L ? new PxRaycastResult(address) : null;
   }

   public static PxRaycastResult arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxRaycastResult(long address) {
      super(address);
   }

   public PxRaycastResult() {
      this.address = _PxRaycastResult();
   }

   private static native long _PxRaycastResult();

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

   public PxRaycastHit getBlock() {
      this.checkNotNull();
      return PxRaycastHit.wrapPointer(_getBlock(this.address));
   }

   private static native long _getBlock(long var0);

   public void setBlock(PxRaycastHit value) {
      this.checkNotNull();
      _setBlock(this.address, value.getAddress());
   }

   private static native void _setBlock(long var0, long var2);

   public boolean getHasBlock() {
      this.checkNotNull();
      return _getHasBlock(this.address);
   }

   private static native boolean _getHasBlock(long var0);

   public void setHasBlock(boolean value) {
      this.checkNotNull();
      _setHasBlock(this.address, value);
   }

   private static native void _setHasBlock(long var0, boolean var2);

   public int getNbAnyHits() {
      this.checkNotNull();
      return _getNbAnyHits(this.address);
   }

   private static native int _getNbAnyHits(long var0);

   public PxRaycastHit getAnyHit(int index) {
      this.checkNotNull();
      return PxRaycastHit.wrapPointer(_getAnyHit(this.address, index));
   }

   private static native long _getAnyHit(long var0, int var2);

   public int getNbTouches() {
      this.checkNotNull();
      return _getNbTouches(this.address);
   }

   private static native int _getNbTouches(long var0);

   public PxRaycastHit getTouch(int index) {
      this.checkNotNull();
      return PxRaycastHit.wrapPointer(_getTouch(this.address, index));
   }

   private static native long _getTouch(long var0, int var2);
}
