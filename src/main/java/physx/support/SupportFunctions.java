package physx.support;

import physx.NativeObject;
import physx.physics.PxArticulationReducedCoordinate;
import physx.physics.PxConstraint;
import physx.physics.PxRigidActor;
import physx.physics.PxScene;
import physx.physics.PxShape;

public class SupportFunctions extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected SupportFunctions() {
   }

   private static native int __sizeOf();

   public static SupportFunctions wrapPointer(long address) {
      return address != 0L ? new SupportFunctions(address) : null;
   }

   public static SupportFunctions arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected SupportFunctions(long address) {
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

   public static PxShape PxActor_getShape(PxRigidActor actor, int index) {
      return PxShape.wrapPointer(_PxActor_getShape(actor.getAddress(), index));
   }

   private static native long _PxActor_getShape(long var0, int var2);

   public static PxArray_PxActorPtr PxScene_getActiveActors(PxScene scene) {
      return PxArray_PxActorPtr.wrapPointer(_PxScene_getActiveActors(scene.getAddress()));
   }

   private static native long _PxScene_getActiveActors(long var0);

   public static int PxArticulationReducedCoordinate_getMinSolverPositionIterations(PxArticulationReducedCoordinate articulation) {
      return _PxArticulationReducedCoordinate_getMinSolverPositionIterations(articulation.getAddress());
   }

   private static native int _PxArticulationReducedCoordinate_getMinSolverPositionIterations(long var0);

   public static int PxArticulationReducedCoordinate_getMinSolverVelocityIterations(PxArticulationReducedCoordinate articulation) {
      return _PxArticulationReducedCoordinate_getMinSolverVelocityIterations(articulation.getAddress());
   }

   private static native int _PxArticulationReducedCoordinate_getMinSolverVelocityIterations(long var0);

   public static NativeObject PxConstraint_getExternalReference(PxConstraint constraint, PxU32Ptr typeID) {
      return NativeObject.wrapPointer(_PxConstraint_getExternalReference(constraint.getAddress(), typeID.getAddress()));
   }

   private static native long _PxConstraint_getExternalReference(long var0, long var2);

   public static PxRigidActor PxConstraint_getActor0(PxConstraint constraint) {
      return PxRigidActor.wrapPointer(_PxConstraint_getActor0(constraint.getAddress()));
   }

   private static native long _PxConstraint_getActor0(long var0);

   public static PxRigidActor PxConstraint_getActor1(PxConstraint constraint) {
      return PxRigidActor.wrapPointer(_PxConstraint_getActor1(constraint.getAddress()));
   }

   private static native long _PxConstraint_getActor1(long var0);
}
