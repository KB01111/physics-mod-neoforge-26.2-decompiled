package physx.physics;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxArticulationAttachment extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxArticulationAttachment() {
   }

   private static native int __sizeOf();

   public static PxArticulationAttachment wrapPointer(long address) {
      return address != 0L ? new PxArticulationAttachment(address) : null;
   }

   public static PxArticulationAttachment arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxArticulationAttachment(long address) {
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

   public void setRestLength(float restLength) {
      this.checkNotNull();
      _setRestLength(this.address, restLength);
   }

   private static native void _setRestLength(long var0, float var2);

   public float getRestLength() {
      this.checkNotNull();
      return _getRestLength(this.address);
   }

   private static native float _getRestLength(long var0);

   public void setLimitParameters(PxArticulationTendonLimit parameters) {
      this.checkNotNull();
      _setLimitParameters(this.address, parameters.getAddress());
   }

   private static native void _setLimitParameters(long var0, long var2);

   public PxArticulationTendonLimit getLimitParameters() {
      this.checkNotNull();
      return PxArticulationTendonLimit.wrapPointer(_getLimitParameters(this.address));
   }

   private static native long _getLimitParameters(long var0);

   public void setRelativeOffset(PxVec3 offset) {
      this.checkNotNull();
      _setRelativeOffset(this.address, offset.getAddress());
   }

   private static native void _setRelativeOffset(long var0, long var2);

   public PxVec3 getRelativeOffset() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getRelativeOffset(this.address));
   }

   private static native long _getRelativeOffset(long var0);

   public void setCoefficient(float coefficient) {
      this.checkNotNull();
      _setCoefficient(this.address, coefficient);
   }

   private static native void _setCoefficient(long var0, float var2);

   public float getCoefficient() {
      this.checkNotNull();
      return _getCoefficient(this.address);
   }

   private static native float _getCoefficient(long var0);

   public PxArticulationLink getLink() {
      this.checkNotNull();
      return PxArticulationLink.wrapPointer(_getLink(this.address));
   }

   private static native long _getLink(long var0);

   public PxArticulationAttachment getParent() {
      this.checkNotNull();
      return wrapPointer(_getParent(this.address));
   }

   private static native long _getParent(long var0);

   public boolean isLeaf() {
      this.checkNotNull();
      return _isLeaf(this.address);
   }

   private static native boolean _isLeaf(long var0);

   public PxArticulationSpatialTendon getTendon() {
      this.checkNotNull();
      return PxArticulationSpatialTendon.wrapPointer(_getTendon(this.address));
   }

   private static native long _getTendon(long var0);

   public void release() {
      this.checkNotNull();
      _release(this.address);
   }

   private static native void _release(long var0);
}
