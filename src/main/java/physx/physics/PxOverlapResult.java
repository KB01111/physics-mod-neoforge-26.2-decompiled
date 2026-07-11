package physx.physics;

public class PxOverlapResult extends PxOverlapCallback {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxOverlapResult wrapPointer(long address) {
      return address != 0L ? new PxOverlapResult(address) : null;
   }

   public static PxOverlapResult arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxOverlapResult(long address) {
      super(address);
   }

   public PxOverlapResult() {
      this.address = _PxOverlapResult();
   }

   private static native long _PxOverlapResult();

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

   public PxOverlapHit getBlock() {
      this.checkNotNull();
      return PxOverlapHit.wrapPointer(_getBlock(this.address));
   }

   private static native long _getBlock(long var0);

   public void setBlock(PxOverlapHit value) {
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

   public PxOverlapHit getAnyHit(int index) {
      this.checkNotNull();
      return PxOverlapHit.wrapPointer(_getAnyHit(this.address, index));
   }

   private static native long _getAnyHit(long var0, int var2);

   public int getNbTouches() {
      this.checkNotNull();
      return _getNbTouches(this.address);
   }

   private static native int _getNbTouches(long var0);

   public PxOverlapHit getTouch(int index) {
      this.checkNotNull();
      return PxOverlapHit.wrapPointer(_getTouch(this.address, index));
   }

   private static native long _getTouch(long var0, int var2);
}
