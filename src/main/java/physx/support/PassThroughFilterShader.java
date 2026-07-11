package physx.support;

import physx.physics.PxSimulationFilterShader;

public class PassThroughFilterShader extends PxSimulationFilterShader {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PassThroughFilterShader() {
   }

   private static native int __sizeOf();

   public static PassThroughFilterShader wrapPointer(long address) {
      return address != 0L ? new PassThroughFilterShader(address) : null;
   }

   public static PassThroughFilterShader arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PassThroughFilterShader(long address) {
      super(address);
   }

   @Override
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

   public int getOutputPairFlags() {
      this.checkNotNull();
      return _getOutputPairFlags(this.address);
   }

   private static native int _getOutputPairFlags(long var0);

   public void setOutputPairFlags(int value) {
      this.checkNotNull();
      _setOutputPairFlags(this.address, value);
   }

   private static native void _setOutputPairFlags(long var0, int var2);

   public int filterShader(
      int attributes0,
      int filterData0w0,
      int filterData0w1,
      int filterData0w2,
      int filterData0w3,
      int attributes1,
      int filterData1w0,
      int filterData1w1,
      int filterData1w2,
      int filterData1w3
   ) {
      this.checkNotNull();
      return _filterShader(
         this.address,
         attributes0,
         filterData0w0,
         filterData0w1,
         filterData0w2,
         filterData0w3,
         attributes1,
         filterData1w0,
         filterData1w1,
         filterData1w2,
         filterData1w3
      );
   }

   private static native int _filterShader(long var0, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11);
}
