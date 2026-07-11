package physx.common;

import physx.NativeObject;

public class PxCollection extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxCollection() {
   }

   private static native int __sizeOf();

   public static PxCollection wrapPointer(long address) {
      return address != 0L ? new PxCollection(address) : null;
   }

   public static PxCollection arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxCollection(long address) {
      super(address);
   }

   public void add(PxBase obj) {
      this.checkNotNull();
      _add(this.address, obj.getAddress());
   }

   private static native void _add(long var0, long var2);

   public void add(PxBase obj, long id) {
      this.checkNotNull();
      _add(this.address, obj.getAddress(), id);
   }

   private static native void _add(long var0, long var2, long var4);

   public void remove(PxBase obj) {
      this.checkNotNull();
      _remove(this.address, obj.getAddress());
   }

   private static native void _remove(long var0, long var2);

   public boolean contains(PxBase obj) {
      this.checkNotNull();
      return _contains(this.address, obj.getAddress());
   }

   private static native boolean _contains(long var0, long var2);

   public void addId(PxBase obj, long id) {
      this.checkNotNull();
      _addId(this.address, obj.getAddress(), id);
   }

   private static native void _addId(long var0, long var2, long var4);

   public void removeId(long id) {
      this.checkNotNull();
      _removeId(this.address, id);
   }

   private static native void _removeId(long var0, long var2);

   public int getNbObjects() {
      this.checkNotNull();
      return _getNbObjects(this.address);
   }

   private static native int _getNbObjects(long var0);

   public PxBase getObject(int index) {
      this.checkNotNull();
      return PxBase.wrapPointer(_getObject(this.address, index));
   }

   private static native long _getObject(long var0, int var2);

   public PxBase find(long id) {
      this.checkNotNull();
      return PxBase.wrapPointer(_find(this.address, id));
   }

   private static native long _find(long var0, long var2);

   public int getNbIds() {
      this.checkNotNull();
      return _getNbIds(this.address);
   }

   private static native int _getNbIds(long var0);

   public long getId(PxBase obj) {
      this.checkNotNull();
      return _getId(this.address, obj.getAddress());
   }

   private static native long _getId(long var0, long var2);

   public void release() {
      this.checkNotNull();
      _release(this.address);
   }

   private static native void _release(long var0);
}
