package physx.extensions;

import physx.NativeObject;
import physx.common.PxInputData;
import physx.support.PxU8Ptr;

public class PxDefaultMemoryInputData extends PxInputData {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxDefaultMemoryInputData() {
   }

   private static native int __sizeOf();

   public static PxDefaultMemoryInputData wrapPointer(long address) {
      return address != 0L ? new PxDefaultMemoryInputData(address) : null;
   }

   public static PxDefaultMemoryInputData arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxDefaultMemoryInputData(long address) {
      super(address);
   }

   public PxDefaultMemoryInputData(PxU8Ptr data, int length) {
      this.address = _PxDefaultMemoryInputData(data.getAddress(), length);
   }

   private static native long _PxDefaultMemoryInputData(long var0, int var2);

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

   public int read(NativeObject dest, int count) {
      this.checkNotNull();
      return _read(this.address, dest.getAddress(), count);
   }

   private static native int _read(long var0, long var2, int var4);

   public int getLength() {
      this.checkNotNull();
      return _getLength(this.address);
   }

   private static native int _getLength(long var0);

   public void seek(int pos) {
      this.checkNotNull();
      _seek(this.address, pos);
   }

   private static native void _seek(long var0, int var2);

   public int tell() {
      this.checkNotNull();
      return _tell(this.address);
   }

   private static native int _tell(long var0);
}
