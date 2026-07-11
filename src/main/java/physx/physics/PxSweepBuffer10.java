package physx.physics;

public class PxSweepBuffer10 extends PxSweepCallback {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxSweepBuffer10 wrapPointer(long address) {
      return address != 0L ? new PxSweepBuffer10(address) : null;
   }

   public static PxSweepBuffer10 arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxSweepBuffer10(long address) {
      super(address);
   }

   public PxSweepBuffer10() {
      this.address = _PxSweepBuffer10();
   }

   private static native long _PxSweepBuffer10();

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

   public PxSweepHit getBlock() {
      this.checkNotNull();
      return PxSweepHit.wrapPointer(_getBlock(this.address));
   }

   private static native long _getBlock(long var0);

   public void setBlock(PxSweepHit value) {
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

   public PxSweepHit getAnyHit(int index) {
      this.checkNotNull();
      return PxSweepHit.wrapPointer(_getAnyHit(this.address, index));
   }

   private static native long _getAnyHit(long var0, int var2);

   public int getNbTouches() {
      this.checkNotNull();
      return _getNbTouches(this.address);
   }

   private static native int _getNbTouches(long var0);

   public PxSweepHit getTouches() {
      this.checkNotNull();
      return PxSweepHit.wrapPointer(_getTouches(this.address));
   }

   private static native long _getTouches(long var0);

   public PxSweepHit getTouch(int index) {
      this.checkNotNull();
      return PxSweepHit.wrapPointer(_getTouch(this.address, index));
   }

   private static native long _getTouch(long var0, int var2);

   public int getMaxNbTouches() {
      this.checkNotNull();
      return _getMaxNbTouches(this.address);
   }

   private static native int _getMaxNbTouches(long var0);
}
