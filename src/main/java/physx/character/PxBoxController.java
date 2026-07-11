package physx.character;

public class PxBoxController extends PxController {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxBoxController() {
   }

   private static native int __sizeOf();

   public static PxBoxController wrapPointer(long address) {
      return address != 0L ? new PxBoxController(address) : null;
   }

   public static PxBoxController arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxBoxController(long address) {
      super(address);
   }

   public float getHalfHeight() {
      this.checkNotNull();
      return _getHalfHeight(this.address);
   }

   private static native float _getHalfHeight(long var0);

   public float getHalfSideExtent() {
      this.checkNotNull();
      return _getHalfSideExtent(this.address);
   }

   private static native float _getHalfSideExtent(long var0);

   public float getHalfForwardExtent() {
      this.checkNotNull();
      return _getHalfForwardExtent(this.address);
   }

   private static native float _getHalfForwardExtent(long var0);

   public boolean setHalfHeight(float halfHeight) {
      this.checkNotNull();
      return _setHalfHeight(this.address, halfHeight);
   }

   private static native boolean _setHalfHeight(long var0, float var2);

   public boolean setHalfSideExtent(float halfSideExtent) {
      this.checkNotNull();
      return _setHalfSideExtent(this.address, halfSideExtent);
   }

   private static native boolean _setHalfSideExtent(long var0, float var2);

   public boolean setHalfForwardExtent(float halfForwardExtent) {
      this.checkNotNull();
      return _setHalfForwardExtent(this.address, halfForwardExtent);
   }

   private static native boolean _setHalfForwardExtent(long var0, float var2);
}
