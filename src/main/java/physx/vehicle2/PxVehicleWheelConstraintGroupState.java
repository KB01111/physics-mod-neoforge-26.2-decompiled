package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleWheelConstraintGroupState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleWheelConstraintGroupState wrapPointer(long address) {
      return address != 0L ? new PxVehicleWheelConstraintGroupState(address) : null;
   }

   public static PxVehicleWheelConstraintGroupState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleWheelConstraintGroupState(long address) {
      super(address);
   }

   public PxVehicleWheelConstraintGroupState() {
      this.address = _PxVehicleWheelConstraintGroupState();
   }

   private static native long _PxVehicleWheelConstraintGroupState();

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

   public int getNbGroups() {
      this.checkNotNull();
      return _getNbGroups(this.address);
   }

   private static native int _getNbGroups(long var0);

   public void setNbGroups(int value) {
      this.checkNotNull();
      _setNbGroups(this.address, value);
   }

   private static native void _setNbGroups(long var0, int var2);

   public int getNbWheelsPerGroup(int index) {
      this.checkNotNull();
      return _getNbWheelsPerGroup(this.address, index);
   }

   private static native int _getNbWheelsPerGroup(long var0, int var2);

   public void setNbWheelsPerGroup(int index, int value) {
      this.checkNotNull();
      _setNbWheelsPerGroup(this.address, index, value);
   }

   private static native void _setNbWheelsPerGroup(long var0, int var2, int var3);

   public int getGroupToWheelIds(int index) {
      this.checkNotNull();
      return _getGroupToWheelIds(this.address, index);
   }

   private static native int _getGroupToWheelIds(long var0, int var2);

   public void setGroupToWheelIds(int index, int value) {
      this.checkNotNull();
      _setGroupToWheelIds(this.address, index, value);
   }

   private static native void _setGroupToWheelIds(long var0, int var2, int var3);

   public int getWheelIdsInGroupOrder(int index) {
      this.checkNotNull();
      return _getWheelIdsInGroupOrder(this.address, index);
   }

   private static native int _getWheelIdsInGroupOrder(long var0, int var2);

   public void setWheelIdsInGroupOrder(int index, int value) {
      this.checkNotNull();
      _setWheelIdsInGroupOrder(this.address, index, value);
   }

   private static native void _setWheelIdsInGroupOrder(long var0, int var2, int var3);

   public float getWheelMultipliersInGroupOrder(int index) {
      this.checkNotNull();
      return _getWheelMultipliersInGroupOrder(this.address, index);
   }

   private static native float _getWheelMultipliersInGroupOrder(long var0, int var2);

   public void setWheelMultipliersInGroupOrder(int index, float value) {
      this.checkNotNull();
      _setWheelMultipliersInGroupOrder(this.address, index, value);
   }

   private static native void _setWheelMultipliersInGroupOrder(long var0, int var2, float var3);

   public int getNbWheelsInGroups() {
      this.checkNotNull();
      return _getNbWheelsInGroups(this.address);
   }

   private static native int _getNbWheelsInGroups(long var0);

   public void setNbWheelsInGroups(int value) {
      this.checkNotNull();
      _setNbWheelsInGroups(this.address, value);
   }

   private static native void _setNbWheelsInGroups(long var0, int var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);

   public int getNbConstraintGroups() {
      this.checkNotNull();
      return _getNbConstraintGroups(this.address);
   }

   private static native int _getNbConstraintGroups(long var0);

   public int getNbWheelsInConstraintGroup(int i) {
      this.checkNotNull();
      return _getNbWheelsInConstraintGroup(this.address, i);
   }

   private static native int _getNbWheelsInConstraintGroup(long var0, int var2);

   public int getWheelInConstraintGroup(int j, int i) {
      this.checkNotNull();
      return _getWheelInConstraintGroup(this.address, j, i);
   }

   private static native int _getWheelInConstraintGroup(long var0, int var2, int var3);

   public float getMultiplierInConstraintGroup(int j, int i) {
      this.checkNotNull();
      return _getMultiplierInConstraintGroup(this.address, j, i);
   }

   private static native float _getMultiplierInConstraintGroup(long var0, int var2, int var3);
}
