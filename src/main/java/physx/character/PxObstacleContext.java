package physx.character;

import physx.NativeObject;

public class PxObstacleContext extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxObstacleContext() {
   }

   private static native int __sizeOf();

   public static PxObstacleContext wrapPointer(long address) {
      return address != 0L ? new PxObstacleContext(address) : null;
   }

   public static PxObstacleContext arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxObstacleContext(long address) {
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

   public void release() {
      this.checkNotNull();
      _release(this.address);
   }

   private static native void _release(long var0);

   public PxControllerManager getControllerManager() {
      this.checkNotNull();
      return PxControllerManager.wrapPointer(_getControllerManager(this.address));
   }

   private static native long _getControllerManager(long var0);

   public int addObstacle(PxObstacle obstacle) {
      this.checkNotNull();
      return _addObstacle(this.address, obstacle.getAddress());
   }

   private static native int _addObstacle(long var0, long var2);

   public boolean removeObstacle(int handle) {
      this.checkNotNull();
      return _removeObstacle(this.address, handle);
   }

   private static native boolean _removeObstacle(long var0, int var2);

   public boolean updateObstacle(int handle, PxObstacle obstacle) {
      this.checkNotNull();
      return _updateObstacle(this.address, handle, obstacle.getAddress());
   }

   private static native boolean _updateObstacle(long var0, int var2, long var3);

   public int getNbObstacles() {
      this.checkNotNull();
      return _getNbObstacles(this.address);
   }

   private static native int _getNbObstacles(long var0);

   public PxObstacle getObstacle(int i) {
      this.checkNotNull();
      return PxObstacle.wrapPointer(_getObstacle(this.address, i));
   }

   private static native long _getObstacle(long var0, int var2);

   public PxObstacle getObstacleByHandle(int handle) {
      this.checkNotNull();
      return PxObstacle.wrapPointer(_getObstacleByHandle(this.address, handle));
   }

   private static native long _getObstacleByHandle(long var0, int var2);
}
