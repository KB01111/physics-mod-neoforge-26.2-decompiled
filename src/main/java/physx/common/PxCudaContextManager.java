package physx.common;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxCudaContextManager extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxCudaContextManager() {
   }

   private static native int __sizeOf();

   public static PxCudaContextManager wrapPointer(long address) {
      return address != 0L ? new PxCudaContextManager(address) : null;
   }

   public static PxCudaContextManager arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxCudaContextManager(long address) {
      super(address);
   }

   public void acquireContext() {
      this.checkNotNull();
      _acquireContext(this.address);
   }

   private static native void _acquireContext(long var0);

   public void releaseContext() {
      this.checkNotNull();
      _releaseContext(this.address);
   }

   private static native void _releaseContext(long var0);

   public CUcontext getContext() {
      this.checkNotNull();
      return CUcontext.wrapPointer(_getContext(this.address));
   }

   private static native long _getContext(long var0);

   public PxCudaContext getCudaContext() {
      this.checkNotNull();
      return PxCudaContext.wrapPointer(_getCudaContext(this.address));
   }

   private static native long _getCudaContext(long var0);

   public boolean contextIsValid() {
      this.checkNotNull();
      return _contextIsValid(this.address);
   }

   private static native boolean _contextIsValid(long var0);

   public boolean supportsArchSM10() {
      this.checkNotNull();
      return _supportsArchSM10(this.address);
   }

   private static native boolean _supportsArchSM10(long var0);

   public boolean supportsArchSM11() {
      this.checkNotNull();
      return _supportsArchSM11(this.address);
   }

   private static native boolean _supportsArchSM11(long var0);

   public boolean supportsArchSM12() {
      this.checkNotNull();
      return _supportsArchSM12(this.address);
   }

   private static native boolean _supportsArchSM12(long var0);

   public boolean supportsArchSM13() {
      this.checkNotNull();
      return _supportsArchSM13(this.address);
   }

   private static native boolean _supportsArchSM13(long var0);

   public boolean supportsArchSM20() {
      this.checkNotNull();
      return _supportsArchSM20(this.address);
   }

   private static native boolean _supportsArchSM20(long var0);

   public boolean supportsArchSM30() {
      this.checkNotNull();
      return _supportsArchSM30(this.address);
   }

   private static native boolean _supportsArchSM30(long var0);

   public boolean supportsArchSM35() {
      this.checkNotNull();
      return _supportsArchSM35(this.address);
   }

   private static native boolean _supportsArchSM35(long var0);

   public boolean supportsArchSM50() {
      this.checkNotNull();
      return _supportsArchSM50(this.address);
   }

   private static native boolean _supportsArchSM50(long var0);

   public boolean supportsArchSM52() {
      this.checkNotNull();
      return _supportsArchSM52(this.address);
   }

   private static native boolean _supportsArchSM52(long var0);

   public boolean supportsArchSM60() {
      this.checkNotNull();
      return _supportsArchSM60(this.address);
   }

   private static native boolean _supportsArchSM60(long var0);

   public boolean isIntegrated() {
      this.checkNotNull();
      return _isIntegrated(this.address);
   }

   private static native boolean _isIntegrated(long var0);

   public boolean canMapHostMemory() {
      this.checkNotNull();
      return _canMapHostMemory(this.address);
   }

   private static native boolean _canMapHostMemory(long var0);

   public int getDriverVersion() {
      this.checkNotNull();
      return _getDriverVersion(this.address);
   }

   private static native int _getDriverVersion(long var0);

   public long getDeviceTotalMemBytes() {
      this.checkNotNull();
      return _getDeviceTotalMemBytes(this.address);
   }

   private static native long _getDeviceTotalMemBytes(long var0);

   public int getMultiprocessorCount() {
      this.checkNotNull();
      return _getMultiprocessorCount(this.address);
   }

   private static native int _getMultiprocessorCount(long var0);

   public int getClockRate() {
      this.checkNotNull();
      return _getClockRate(this.address);
   }

   private static native int _getClockRate(long var0);

   public int getSharedMemPerBlock() {
      this.checkNotNull();
      return _getSharedMemPerBlock(this.address);
   }

   private static native int _getSharedMemPerBlock(long var0);

   public int getSharedMemPerMultiprocessor() {
      this.checkNotNull();
      return _getSharedMemPerMultiprocessor(this.address);
   }

   private static native int _getSharedMemPerMultiprocessor(long var0);

   public int getMaxThreadsPerBlock() {
      this.checkNotNull();
      return _getMaxThreadsPerBlock(this.address);
   }

   private static native int _getMaxThreadsPerBlock(long var0);

   public String getDeviceName() {
      this.checkNotNull();
      return _getDeviceName(this.address);
   }

   private static native String _getDeviceName(long var0);

   public CUdevice getDevice() {
      this.checkNotNull();
      return CUdevice.wrapPointer(_getDevice(this.address));
   }

   private static native long _getDevice(long var0);

   public void setUsingConcurrentStreams(boolean flag) {
      this.checkNotNull();
      _setUsingConcurrentStreams(this.address, flag);
   }

   private static native void _setUsingConcurrentStreams(long var0, boolean var2);

   public boolean getUsingConcurrentStreams() {
      this.checkNotNull();
      return _getUsingConcurrentStreams(this.address);
   }

   private static native boolean _getUsingConcurrentStreams(long var0);

   public int usingDedicatedGPU() {
      this.checkNotNull();
      return _usingDedicatedGPU(this.address);
   }

   private static native int _usingDedicatedGPU(long var0);

   public CUmodule getCuModules() {
      this.checkNotNull();
      return CUmodule.wrapPointer(_getCuModules(this.address));
   }

   private static native long _getCuModules(long var0);

   public void release() {
      this.checkNotNull();
      _release(this.address);
   }

   private static native void _release(long var0);

   static {
      PlatformChecks.requirePlatform(3, "physx.common.PxCudaContextManager");
   }
}
