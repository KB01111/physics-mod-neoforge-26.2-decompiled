package physx.extensions;

import physx.NativeObject;
import physx.common.PxCollection;
import physx.physics.PxScene;

public class PxCollectionExt extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxCollectionExt() {
   }

   private static native int __sizeOf();

   public static PxCollectionExt wrapPointer(long address) {
      return address != 0L ? new PxCollectionExt(address) : null;
   }

   public static PxCollectionExt arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxCollectionExt(long address) {
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

   public static void releaseObjects(PxCollection collection) {
      _releaseObjects(collection.getAddress());
   }

   private static native void _releaseObjects(long var0);

   public static void releaseObjects(PxCollection collection, boolean releaseExclusiveShapes) {
      _releaseObjects(collection.getAddress(), releaseExclusiveShapes);
   }

   private static native void _releaseObjects(long var0, boolean var2);

   public static void remove(PxCollection collection, short concreteType) {
      _remove(collection.getAddress(), concreteType);
   }

   private static native void _remove(long var0, short var2);

   public static void remove(PxCollection collection, short concreteType, PxCollection to) {
      _remove(collection.getAddress(), concreteType, to.getAddress());
   }

   private static native void _remove(long var0, short var2, long var3);

   public static PxCollection createCollection(PxScene scene) {
      return PxCollection.wrapPointer(_createCollection(scene.getAddress()));
   }

   private static native long _createCollection(long var0);
}
