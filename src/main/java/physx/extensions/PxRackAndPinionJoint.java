package physx.extensions;

import physx.common.PxBase;

public class PxRackAndPinionJoint extends PxJoint {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxRackAndPinionJoint() {
   }

   private static native int __sizeOf();

   public static PxRackAndPinionJoint wrapPointer(long address) {
      return address != 0L ? new PxRackAndPinionJoint(address) : null;
   }

   public static PxRackAndPinionJoint arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxRackAndPinionJoint(long address) {
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

   public boolean setJoints(PxBase hinge, PxBase prismatic) {
      this.checkNotNull();
      return _setJoints(this.address, hinge.getAddress(), prismatic.getAddress());
   }

   private static native boolean _setJoints(long var0, long var2, long var4);

   public void setRatio(float ratio) {
      this.checkNotNull();
      _setRatio(this.address, ratio);
   }

   private static native void _setRatio(long var0, float var2);

   public float getRatio() {
      this.checkNotNull();
      return _getRatio(this.address);
   }

   private static native float _getRatio(long var0);

   public boolean setData(int nbRackTeeth, int nbPinionTeeth, float rackLength) {
      this.checkNotNull();
      return _setData(this.address, nbRackTeeth, nbPinionTeeth, rackLength);
   }

   private static native boolean _setData(long var0, int var2, int var3, float var4);
}
