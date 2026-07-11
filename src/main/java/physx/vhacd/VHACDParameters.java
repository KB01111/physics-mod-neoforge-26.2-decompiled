package physx.vhacd;

import physx.NativeObject;
import physx.PlatformChecks;

public class VHACDParameters extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static VHACDParameters wrapPointer(long address) {
      return address != 0L ? new VHACDParameters(address) : null;
   }

   public static VHACDParameters arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected VHACDParameters(long address) {
      super(address);
   }

   public static VHACDParameters createAt(long address) {
      __placement_new_VHACDParameters(address);
      VHACDParameters createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> VHACDParameters createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_VHACDParameters(address);
      VHACDParameters createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_VHACDParameters(long var0);

   public VHACDParameters() {
      this.address = _VHACDParameters();
   }

   private static native long _VHACDParameters();

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

   public int getM_maxConvexHulls() {
      this.checkNotNull();
      return _getM_maxConvexHulls(this.address);
   }

   private static native int _getM_maxConvexHulls(long var0);

   public void setM_maxConvexHulls(int value) {
      this.checkNotNull();
      _setM_maxConvexHulls(this.address, value);
   }

   private static native void _setM_maxConvexHulls(long var0, int var2);

   public int getM_resolution() {
      this.checkNotNull();
      return _getM_resolution(this.address);
   }

   private static native int _getM_resolution(long var0);

   public void setM_resolution(int value) {
      this.checkNotNull();
      _setM_resolution(this.address, value);
   }

   private static native void _setM_resolution(long var0, int var2);

   public double getM_minimumVolumePercentErrorAllowed() {
      this.checkNotNull();
      return _getM_minimumVolumePercentErrorAllowed(this.address);
   }

   private static native double _getM_minimumVolumePercentErrorAllowed(long var0);

   public void setM_minimumVolumePercentErrorAllowed(double value) {
      this.checkNotNull();
      _setM_minimumVolumePercentErrorAllowed(this.address, value);
   }

   private static native void _setM_minimumVolumePercentErrorAllowed(long var0, double var2);

   public int getM_maxRecursionDepth() {
      this.checkNotNull();
      return _getM_maxRecursionDepth(this.address);
   }

   private static native int _getM_maxRecursionDepth(long var0);

   public void setM_maxRecursionDepth(int value) {
      this.checkNotNull();
      _setM_maxRecursionDepth(this.address, value);
   }

   private static native void _setM_maxRecursionDepth(long var0, int var2);

   public boolean getM_shrinkWrap() {
      this.checkNotNull();
      return _getM_shrinkWrap(this.address);
   }

   private static native boolean _getM_shrinkWrap(long var0);

   public void setM_shrinkWrap(boolean value) {
      this.checkNotNull();
      _setM_shrinkWrap(this.address, value);
   }

   private static native void _setM_shrinkWrap(long var0, boolean var2);

   public int getM_maxNumVerticesPerCH() {
      this.checkNotNull();
      return _getM_maxNumVerticesPerCH(this.address);
   }

   private static native int _getM_maxNumVerticesPerCH(long var0);

   public void setM_maxNumVerticesPerCH(int value) {
      this.checkNotNull();
      _setM_maxNumVerticesPerCH(this.address, value);
   }

   private static native void _setM_maxNumVerticesPerCH(long var0, int var2);

   public boolean getM_asyncACD() {
      this.checkNotNull();
      return _getM_asyncACD(this.address);
   }

   private static native boolean _getM_asyncACD(long var0);

   public void setM_asyncACD(boolean value) {
      this.checkNotNull();
      _setM_asyncACD(this.address, value);
   }

   private static native void _setM_asyncACD(long var0, boolean var2);

   public int getM_minEdgeLength() {
      this.checkNotNull();
      return _getM_minEdgeLength(this.address);
   }

   private static native int _getM_minEdgeLength(long var0);

   public void setM_minEdgeLength(int value) {
      this.checkNotNull();
      _setM_minEdgeLength(this.address, value);
   }

   private static native void _setM_minEdgeLength(long var0, int var2);

   public boolean getM_findBestPlane() {
      this.checkNotNull();
      return _getM_findBestPlane(this.address);
   }

   private static native boolean _getM_findBestPlane(long var0);

   public void setM_findBestPlane(boolean value) {
      this.checkNotNull();
      _setM_findBestPlane(this.address, value);
   }

   private static native void _setM_findBestPlane(long var0, boolean var2);

   static {
      PlatformChecks.requirePlatform(15, "physx.vhacd.VHACDParameters");
   }
}
