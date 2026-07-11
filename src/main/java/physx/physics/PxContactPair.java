package physx.physics;

import physx.NativeObject;

public class PxContactPair extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxContactPair() {
   }

   private static native int __sizeOf();

   public static PxContactPair wrapPointer(long address) {
      return address != 0L ? new PxContactPair(address) : null;
   }

   public static PxContactPair arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxContactPair(long address) {
      super(address);
   }

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

   public PxShape getShapes(int index) {
      this.checkNotNull();
      return PxShape.wrapPointer(_getShapes(this.address, index));
   }

   private static native long _getShapes(long var0, int var2);

   public void setShapes(int index, PxShape value) {
      this.checkNotNull();
      _setShapes(this.address, index, value.getAddress());
   }

   private static native void _setShapes(long var0, int var2, long var3);

   public byte getContactCount() {
      this.checkNotNull();
      return _getContactCount(this.address);
   }

   private static native byte _getContactCount(long var0);

   public void setContactCount(byte value) {
      this.checkNotNull();
      _setContactCount(this.address, value);
   }

   private static native void _setContactCount(long var0, byte var2);

   public byte getPatchCount() {
      this.checkNotNull();
      return _getPatchCount(this.address);
   }

   private static native byte _getPatchCount(long var0);

   public void setPatchCount(byte value) {
      this.checkNotNull();
      _setPatchCount(this.address, value);
   }

   private static native void _setPatchCount(long var0, byte var2);

   public PxContactPairFlags getFlags() {
      this.checkNotNull();
      return PxContactPairFlags.wrapPointer(_getFlags(this.address));
   }

   private static native long _getFlags(long var0);

   public void setFlags(PxContactPairFlags value) {
      this.checkNotNull();
      _setFlags(this.address, value.getAddress());
   }

   private static native void _setFlags(long var0, long var2);

   public PxPairFlags getEvents() {
      this.checkNotNull();
      return PxPairFlags.wrapPointer(_getEvents(this.address));
   }

   private static native long _getEvents(long var0);

   public void setEvents(PxPairFlags value) {
      this.checkNotNull();
      _setEvents(this.address, value.getAddress());
   }

   private static native void _setEvents(long var0, long var2);

   public int extractContacts(PxContactPairPoint userBuffer, int bufferSize) {
      this.checkNotNull();
      return _extractContacts(this.address, userBuffer.getAddress(), bufferSize);
   }

   private static native int _extractContacts(long var0, long var2, int var4);
}
