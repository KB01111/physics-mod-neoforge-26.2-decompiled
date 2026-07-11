package physx.physics;

public class PxRaycastBuffer10 extends PxRaycastCallback {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxRaycastBuffer10 wrapPointer(long address) {
      return address != 0L ? new PxRaycastBuffer10(address) : null;
   }

   public static PxRaycastBuffer10 arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxRaycastBuffer10(long address) {
      super(address);
   }

   public PxRaycastBuffer10() {
      this.address = _PxRaycastBuffer10();
   }

   private static native long _PxRaycastBuffer10();

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

   public PxRaycastHit getTouches() {
      this.checkNotNull();
      return PxRaycastHit.wrapPointer(_getTouches(this.address));
   }

   private static native long _getTouches(long var0);

   public PxRaycastHit getTouch(int index) {
      this.checkNotNull();
      return PxRaycastHit.wrapPointer(_getTouch(this.address, index));
   }

   private static native long _getTouch(long var0, int var2);

   public int getMaxNbTouches() {
      this.checkNotNull();
      return _getMaxNbTouches(this.address);
   }

   private static native int _getMaxNbTouches(long var0);
}
