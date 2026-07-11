package physx;

import de.fabmax.physxjni.Loader;

public class NativeObject {
   static {
      Loader.load();
   }

   public static final int SIZEOF_POINTER = __sizeOfPointer();
   public static final int SIZEOF_BYTE = 1;
   public static final int SIZEOF_SHORT = 2;
   public static final int SIZEOF_INT = 4;
   public static final int SIZEOF_LONG = 8;
   public static final int SIZEOF_FLOAT = 4;
   public static final int SIZEOF_DOUBLE = 8;
   protected long address = 0L;
   protected boolean isExternallyAllocated = false;

   protected NativeObject() {
   }

   private static native int __sizeOfPointer();

   protected NativeObject(long address) {
      this.address = address;
   }

   public static NativeObject wrapPointer(long address) {
      return new NativeObject(address);
   }

   protected void checkNotNull() {
      if (this.address == 0L) {
         throw new NullPointerException("Native address of " + this + " is 0");
      }
   }

   public long getAddress() {
      return this.address;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof NativeObject)) {
         return false;
      } else {
         NativeObject that = (NativeObject)o;
         return this.address == that.address;
      }
   }

   @Override
   public int hashCode() {
      return (int)(this.address ^ this.address >>> 32);
   }

   @FunctionalInterface
   public interface Allocator<T> {
      long on(T var1, int var2, int var3);
   }
}
