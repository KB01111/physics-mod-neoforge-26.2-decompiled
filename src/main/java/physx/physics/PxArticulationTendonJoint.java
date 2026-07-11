package physx.physics;

import physx.NativeObject;

public class PxArticulationTendonJoint extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxArticulationTendonJoint() {
   }

   private static native int __sizeOf();

   public static PxArticulationTendonJoint wrapPointer(long address) {
      return address != 0L ? new PxArticulationTendonJoint(address) : null;
   }

   public static PxArticulationTendonJoint arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxArticulationTendonJoint(long address) {
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

   public NativeObject getUserData() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_getUserData(this.address));
   }

   private static native long _getUserData(long var0);

   public void setUserData(NativeObject value) {
      this.checkNotNull();
      _setUserData(this.address, value.getAddress());
   }

   private static native void _setUserData(long var0, long var2);

   public void setCoefficient(PxArticulationAxisEnum axis, float coefficient, float recipCoefficient) {
      this.checkNotNull();
      _setCoefficient(this.address, axis.value, coefficient, recipCoefficient);
   }

   private static native void _setCoefficient(long var0, int var2, float var3, float var4);

   public PxArticulationLink getLink() {
      this.checkNotNull();
      return PxArticulationLink.wrapPointer(_getLink(this.address));
   }

   private static native long _getLink(long var0);

   public PxArticulationTendonJoint getParent() {
      this.checkNotNull();
      return wrapPointer(_getParent(this.address));
   }

   private static native long _getParent(long var0);

   public PxArticulationFixedTendon getTendon() {
      this.checkNotNull();
      return PxArticulationFixedTendon.wrapPointer(_getTendon(this.address));
   }

   private static native long _getTendon(long var0);

   public void release() {
      this.checkNotNull();
      _release(this.address);
   }

   private static native void _release(long var0);
}
