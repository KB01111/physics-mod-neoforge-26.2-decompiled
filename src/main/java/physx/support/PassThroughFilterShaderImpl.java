package physx.support;

public class PassThroughFilterShaderImpl extends PassThroughFilterShader {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PassThroughFilterShaderImpl wrapPointer(long address) {
      return address != 0L ? new PassThroughFilterShaderImpl(address) : null;
   }

   public static PassThroughFilterShaderImpl arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PassThroughFilterShaderImpl(long address) {
      super(address);
   }

   protected PassThroughFilterShaderImpl() {
      this.address = this._PassThroughFilterShaderImpl();
   }

   private native long _PassThroughFilterShaderImpl();

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

   private int _filterShader(
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
      return this.filterShader(
         attributes0, filterData0w0, filterData0w1, filterData0w2, filterData0w3, attributes1, filterData1w0, filterData1w1, filterData1w2, filterData1w3
      );
   }

   @Override
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
      return 0;
   }
}
