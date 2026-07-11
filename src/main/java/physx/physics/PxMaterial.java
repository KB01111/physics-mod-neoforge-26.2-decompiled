package physx.physics;

import physx.NativeObject;

public class PxMaterial extends PxBaseMaterial {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxMaterial() {
   }

   private static native int __sizeOf();

   public static PxMaterial wrapPointer(long address) {
      return address != 0L ? new PxMaterial(address) : null;
   }

   public static PxMaterial arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxMaterial(long address) {
      super(address);
   }

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

   public void setDynamicFriction(float coef) {
      this.checkNotNull();
      _setDynamicFriction(this.address, coef);
   }

   private static native void _setDynamicFriction(long var0, float var2);

   public float getDynamicFriction() {
      this.checkNotNull();
      return _getDynamicFriction(this.address);
   }

   private static native float _getDynamicFriction(long var0);

   public void setStaticFriction(float coef) {
      this.checkNotNull();
      _setStaticFriction(this.address, coef);
   }

   private static native void _setStaticFriction(long var0, float var2);

   public float getStaticFriction() {
      this.checkNotNull();
      return _getStaticFriction(this.address);
   }

   private static native float _getStaticFriction(long var0);

   public void setRestitution(float coef) {
      this.checkNotNull();
      _setRestitution(this.address, coef);
   }

   private static native void _setRestitution(long var0, float var2);

   public float getRestitution() {
      this.checkNotNull();
      return _getRestitution(this.address);
   }

   private static native float _getRestitution(long var0);

   public void setFlag(PxMaterialFlagEnum flag, boolean b) {
      this.checkNotNull();
      _setFlag(this.address, flag.value, b);
   }

   private static native void _setFlag(long var0, int var2, boolean var3);

   public void setFlags(PxMaterialFlags flags) {
      this.checkNotNull();
      _setFlags(this.address, flags.getAddress());
   }

   private static native void _setFlags(long var0, long var2);

   public PxMaterialFlags getFlags() {
      this.checkNotNull();
      return PxMaterialFlags.wrapPointer(_getFlags(this.address));
   }

   private static native long _getFlags(long var0);

   public void setFrictionCombineMode(PxCombineModeEnum combMode) {
      this.checkNotNull();
      _setFrictionCombineMode(this.address, combMode.value);
   }

   private static native void _setFrictionCombineMode(long var0, int var2);

   public PxCombineModeEnum getFrictionCombineMode() {
      this.checkNotNull();
      return PxCombineModeEnum.forValue(_getFrictionCombineMode(this.address));
   }

   private static native int _getFrictionCombineMode(long var0);

   public void setRestitutionCombineMode(PxCombineModeEnum combMode) {
      this.checkNotNull();
      _setRestitutionCombineMode(this.address, combMode.value);
   }

   private static native void _setRestitutionCombineMode(long var0, int var2);

   public PxCombineModeEnum getRestitutionCombineMode() {
      this.checkNotNull();
      return PxCombineModeEnum.forValue(_getRestitutionCombineMode(this.address));
   }

   private static native int _getRestitutionCombineMode(long var0);
}
