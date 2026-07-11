package physx.physics;

import physx.common.PxBase;
import physx.geometry.PxBVH;

public class PxAggregate extends PxBase {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxAggregate() {
   }

   private static native int __sizeOf();

   public static PxAggregate wrapPointer(long address) {
      return address != 0L ? new PxAggregate(address) : null;
   }

   public static PxAggregate arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxAggregate(long address) {
      super(address);
   }

   public boolean addActor(PxActor actor) {
      this.checkNotNull();
      return _addActor(this.address, actor.getAddress());
   }

   private static native boolean _addActor(long var0, long var2);

   public boolean addActor(PxActor actor, PxBVH bvh) {
      this.checkNotNull();
      return _addActor(this.address, actor.getAddress(), bvh.getAddress());
   }

   private static native boolean _addActor(long var0, long var2, long var4);

   public boolean removeActor(PxActor actor) {
      this.checkNotNull();
      return _removeActor(this.address, actor.getAddress());
   }

   private static native boolean _removeActor(long var0, long var2);

   public boolean addArticulation(PxArticulationReducedCoordinate articulation) {
      this.checkNotNull();
      return _addArticulation(this.address, articulation.getAddress());
   }

   private static native boolean _addArticulation(long var0, long var2);

   public boolean removeArticulation(PxArticulationReducedCoordinate articulation) {
      this.checkNotNull();
      return _removeArticulation(this.address, articulation.getAddress());
   }

   private static native boolean _removeArticulation(long var0, long var2);

   public int getNbActors() {
      this.checkNotNull();
      return _getNbActors(this.address);
   }

   private static native int _getNbActors(long var0);

   public int getMaxNbActors() {
      this.checkNotNull();
      return _getMaxNbActors(this.address);
   }

   private static native int _getMaxNbActors(long var0);

   public int getMaxNbShapes() {
      this.checkNotNull();
      return _getMaxNbShapes(this.address);
   }

   private static native int _getMaxNbShapes(long var0);

   public PxScene getScene() {
      this.checkNotNull();
      return PxScene.wrapPointer(_getScene(this.address));
   }

   private static native long _getScene(long var0);

   public boolean getSelfCollision() {
      this.checkNotNull();
      return _getSelfCollision(this.address);
   }

   private static native boolean _getSelfCollision(long var0);
}
