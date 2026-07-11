package physx.extensions;

import physx.NativeObject;

public class PxJointLimitParameters extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxJointLimitParameters() {
   }

   private static native int __sizeOf();

   public static PxJointLimitParameters wrapPointer(long address) {
      return address != 0L ? new PxJointLimitParameters(address) : null;
   }

   public static PxJointLimitParameters arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxJointLimitParameters(long address) {
      super(address);
   }

   public float getRestitution() {
      this.checkNotNull();
      return _getRestitution(this.address);
   }

   private static native float _getRestitution(long var0);

   public void setRestitution(float value) {
      this.checkNotNull();
      _setRestitution(this.address, value);
   }

   private static native void _setRestitution(long var0, float var2);

   public float getBounceThreshold() {
      this.checkNotNull();
      return _getBounceThreshold(this.address);
   }

   private static native float _getBounceThreshold(long var0);

   public void setBounceThreshold(float value) {
      this.checkNotNull();
      _setBounceThreshold(this.address, value);
   }

   private static native void _setBounceThreshold(long var0, float var2);

   public float getStiffness() {
      this.checkNotNull();
      return _getStiffness(this.address);
   }

   private static native float _getStiffness(long var0);

   public void setStiffness(float value) {
      this.checkNotNull();
      _setStiffness(this.address, value);
   }

   private static native void _setStiffness(long var0, float var2);

   public float getDamping() {
      this.checkNotNull();
      return _getDamping(this.address);
   }

   private static native float _getDamping(long var0);

   public void setDamping(float value) {
      this.checkNotNull();
      _setDamping(this.address, value);
   }

   private static native void _setDamping(long var0, float var2);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);

   public boolean isSoft() {
      this.checkNotNull();
      return _isSoft(this.address);
   }

   private static native boolean _isSoft(long var0);
}
