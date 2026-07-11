package physx.extensions;

import physx.NativeObject;
import physx.geometry.PxGeometry;
import physx.physics.PxMaterial;
import physx.physics.PxRigidActor;
import physx.physics.PxShape;
import physx.physics.PxShapeFlags;

public class PxRigidActorExt extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxRigidActorExt() {
   }

   private static native int __sizeOf();

   public static PxRigidActorExt wrapPointer(long address) {
      return address != 0L ? new PxRigidActorExt(address) : null;
   }

   public static PxRigidActorExt arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxRigidActorExt(long address) {
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

   public static PxShape createExclusiveShape(PxRigidActor actor, PxGeometry geometry, PxMaterial material) {
      return PxShape.wrapPointer(_createExclusiveShape(actor.getAddress(), geometry.getAddress(), material.getAddress()));
   }

   private static native long _createExclusiveShape(long var0, long var2, long var4);

   public static PxShape createExclusiveShape(PxRigidActor actor, PxGeometry geometry, PxMaterial material, PxShapeFlags flags) {
      return PxShape.wrapPointer(_createExclusiveShape(actor.getAddress(), geometry.getAddress(), material.getAddress(), flags.getAddress()));
   }

   private static native long _createExclusiveShape(long var0, long var2, long var4, long var6);
}
