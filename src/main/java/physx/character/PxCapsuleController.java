package physx.character;

public class PxCapsuleController extends PxController {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxCapsuleController() {
   }

   private static native int __sizeOf();

   public static PxCapsuleController wrapPointer(long address) {
      return address != 0L ? new PxCapsuleController(address) : null;
   }

   public static PxCapsuleController arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxCapsuleController(long address) {
      super(address);
   }

   public float getRadius() {
      this.checkNotNull();
      return _getRadius(this.address);
   }

   private static native float _getRadius(long var0);

   public boolean setRadius(float radius) {
      this.checkNotNull();
      return _setRadius(this.address, radius);
   }

   private static native boolean _setRadius(long var0, float var2);

   public float getHeight() {
      this.checkNotNull();
      return _getHeight(this.address);
   }

   private static native float _getHeight(long var0);

   public boolean setHeight(float height) {
      this.checkNotNull();
      return _setHeight(this.address, height);
   }

   private static native boolean _setHeight(long var0, float var2);

   public PxCapsuleClimbingModeEnum getClimbingMode() {
      this.checkNotNull();
      return PxCapsuleClimbingModeEnum.forValue(_getClimbingMode(this.address));
   }

   private static native int _getClimbingMode(long var0);

   public boolean setClimbingMode(PxCapsuleClimbingModeEnum mode) {
      this.checkNotNull();
      return _setClimbingMode(this.address, mode.value);
   }

   private static native boolean _setClimbingMode(long var0, int var2);
}
