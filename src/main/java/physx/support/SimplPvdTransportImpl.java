package physx.support;

import physx.NativeObject;

public class SimplPvdTransportImpl extends SimplePvdTransport {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static SimplPvdTransportImpl wrapPointer(long address) {
      return address != 0L ? new SimplPvdTransportImpl(address) : null;
   }

   public static SimplPvdTransportImpl arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected SimplPvdTransportImpl(long address) {
      super(address);
   }

   protected SimplPvdTransportImpl() {
      this.address = this._SimplPvdTransportImpl();
   }

   private native long _SimplPvdTransportImpl();

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

   private boolean _connect() {
      return this.connect();
   }

   @Override
   public boolean connect() {
      return false;
   }

   private boolean _isConnected() {
      return this.isConnected();
   }

   @Override
   public boolean isConnected() {
      return false;
   }

   private void _disconnect() {
      this.disconnect();
   }

   @Override
   public void disconnect() {
   }

   private void _send(long inBytes, int inLength) {
      this.send(NativeObject.wrapPointer(inBytes), inLength);
   }

   @Override
   public void send(NativeObject inBytes, int inLength) {
   }

   private void _flush() {
      this.flush();
   }

   @Override
   public void flush() {
   }
}
