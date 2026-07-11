package physx.vehicle2;

import physx.NativeObject;
import physx.support.PxU32ConstPtr;

public class PxVehicleTankDriveDifferentialParams extends PxVehicleMultiWheelDriveDifferentialParams {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleTankDriveDifferentialParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleTankDriveDifferentialParams(address) : null;
   }

   public static PxVehicleTankDriveDifferentialParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleTankDriveDifferentialParams(long address) {
      super(address);
   }

   public static PxVehicleTankDriveDifferentialParams createAt(long address) {
      __placement_new_PxVehicleTankDriveDifferentialParams(address);
      PxVehicleTankDriveDifferentialParams createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxVehicleTankDriveDifferentialParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxVehicleTankDriveDifferentialParams(address);
      PxVehicleTankDriveDifferentialParams createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxVehicleTankDriveDifferentialParams(long var0);

   public PxVehicleTankDriveDifferentialParams() {
      this.address = _PxVehicleTankDriveDifferentialParams();
   }

   private static native long _PxVehicleTankDriveDifferentialParams();

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

   public int getNbTracks() {
      this.checkNotNull();
      return _getNbTracks(this.address);
   }

   private static native int _getNbTracks(long var0);

   public void setNbTracks(int value) {
      this.checkNotNull();
      _setNbTracks(this.address, value);
   }

   private static native void _setNbTracks(long var0, int var2);

   public int getThrustIdPerTrack(int index) {
      this.checkNotNull();
      return _getThrustIdPerTrack(this.address, index);
   }

   private static native int _getThrustIdPerTrack(long var0, int var2);

   public void setThrustIdPerTrack(int index, int value) {
      this.checkNotNull();
      _setThrustIdPerTrack(this.address, index, value);
   }

   private static native void _setThrustIdPerTrack(long var0, int var2, int var3);

   public int getNbWheelsPerTrack(int index) {
      this.checkNotNull();
      return _getNbWheelsPerTrack(this.address, index);
   }

   private static native int _getNbWheelsPerTrack(long var0, int var2);

   public void setNbWheelsPerTrack(int index, int value) {
      this.checkNotNull();
      _setNbWheelsPerTrack(this.address, index, value);
   }

   private static native void _setNbWheelsPerTrack(long var0, int var2, int var3);

   public int getTrackToWheelIds(int index) {
      this.checkNotNull();
      return _getTrackToWheelIds(this.address, index);
   }

   private static native int _getTrackToWheelIds(long var0, int var2);

   public void setTrackToWheelIds(int index, int value) {
      this.checkNotNull();
      _setTrackToWheelIds(this.address, index, value);
   }

   private static native void _setTrackToWheelIds(long var0, int var2, int var3);

   public int getWheelIdsInTrackOrder(int index) {
      this.checkNotNull();
      return _getWheelIdsInTrackOrder(this.address, index);
   }

   private static native int _getWheelIdsInTrackOrder(long var0, int var2);

   public void setWheelIdsInTrackOrder(int index, int value) {
      this.checkNotNull();
      _setWheelIdsInTrackOrder(this.address, index, value);
   }

   private static native void _setWheelIdsInTrackOrder(long var0, int var2, int var3);

   @Override
   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);

   public int getNbWheelsInTrack(int i) {
      this.checkNotNull();
      return _getNbWheelsInTrack(this.address, i);
   }

   private static native int _getNbWheelsInTrack(long var0, int var2);

   public PxU32ConstPtr getWheelsInTrack(int i) {
      this.checkNotNull();
      return PxU32ConstPtr.wrapPointer(_getWheelsInTrack(this.address, i));
   }

   private static native long _getWheelsInTrack(long var0, int var2);

   public int getWheelInTrack(int j, int i) {
      this.checkNotNull();
      return _getWheelInTrack(this.address, j, i);
   }

   private static native int _getWheelInTrack(long var0, int var2, int var3);

   public int getThrustControllerIndex(int i) {
      this.checkNotNull();
      return _getThrustControllerIndex(this.address, i);
   }

   private static native int _getThrustControllerIndex(long var0, int var2);

   public PxVehicleTankDriveDifferentialParams transformAndScale(
      PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale
   ) {
      this.checkNotNull();
      return wrapPointer(_transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
   }

   private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);
}
