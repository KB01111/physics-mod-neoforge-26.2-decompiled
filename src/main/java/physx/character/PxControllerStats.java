package physx.character;

import physx.NativeObject;

public class PxControllerStats extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxControllerStats() {
   }

   private static native int __sizeOf();

   public static PxControllerStats wrapPointer(long address) {
      return address != 0L ? new PxControllerStats(address) : null;
   }

   public static PxControllerStats arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxControllerStats(long address) {
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

   public short getNbIterations() {
      this.checkNotNull();
      return _getNbIterations(this.address);
   }

   private static native short _getNbIterations(long var0);

   public void setNbIterations(short value) {
      this.checkNotNull();
      _setNbIterations(this.address, value);
   }

   private static native void _setNbIterations(long var0, short var2);

   public short getNbFullUpdates() {
      this.checkNotNull();
      return _getNbFullUpdates(this.address);
   }

   private static native short _getNbFullUpdates(long var0);

   public void setNbFullUpdates(short value) {
      this.checkNotNull();
      _setNbFullUpdates(this.address, value);
   }

   private static native void _setNbFullUpdates(long var0, short var2);

   public short getNbPartialUpdates() {
      this.checkNotNull();
      return _getNbPartialUpdates(this.address);
   }

   private static native short _getNbPartialUpdates(long var0);

   public void setNbPartialUpdates(short value) {
      this.checkNotNull();
      _setNbPartialUpdates(this.address, value);
   }

   private static native void _setNbPartialUpdates(long var0, short var2);

   public short getNbTessellation() {
      this.checkNotNull();
      return _getNbTessellation(this.address);
   }

   private static native short _getNbTessellation(long var0);

   public void setNbTessellation(short value) {
      this.checkNotNull();
      _setNbTessellation(this.address, value);
   }

   private static native void _setNbTessellation(long var0, short var2);
}
