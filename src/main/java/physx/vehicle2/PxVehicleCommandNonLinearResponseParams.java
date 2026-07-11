package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleCommandNonLinearResponseParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleCommandNonLinearResponseParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleCommandNonLinearResponseParams(address) : null;
   }

   public static PxVehicleCommandNonLinearResponseParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleCommandNonLinearResponseParams(long address) {
      super(address);
   }

   public PxVehicleCommandNonLinearResponseParams() {
      this.address = _PxVehicleCommandNonLinearResponseParams();
   }

   private static native long _PxVehicleCommandNonLinearResponseParams();

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

   public float getSpeedResponses(int index) {
      this.checkNotNull();
      return _getSpeedResponses(this.address, index);
   }

   private static native float _getSpeedResponses(long var0, int var2);

   public void setSpeedResponses(int index, float value) {
      this.checkNotNull();
      _setSpeedResponses(this.address, index, value);
   }

   private static native void _setSpeedResponses(long var0, int var2, float var3);

   public short getNbSpeedResponses() {
      this.checkNotNull();
      return _getNbSpeedResponses(this.address);
   }

   private static native short _getNbSpeedResponses(long var0);

   public void setNbSpeedResponses(short value) {
      this.checkNotNull();
      _setNbSpeedResponses(this.address, value);
   }

   private static native void _setNbSpeedResponses(long var0, short var2);

   public short getSpeedResponsesPerCommandValue(int index) {
      this.checkNotNull();
      return _getSpeedResponsesPerCommandValue(this.address, index);
   }

   private static native short _getSpeedResponsesPerCommandValue(long var0, int var2);

   public void setSpeedResponsesPerCommandValue(int index, short value) {
      this.checkNotNull();
      _setSpeedResponsesPerCommandValue(this.address, index, value);
   }

   private static native void _setSpeedResponsesPerCommandValue(long var0, int var2, short var3);

   public short getNbSpeedResponsesPerCommandValue(int index) {
      this.checkNotNull();
      return _getNbSpeedResponsesPerCommandValue(this.address, index);
   }

   private static native short _getNbSpeedResponsesPerCommandValue(long var0, int var2);

   public void setNbSpeedResponsesPerCommandValue(int index, short value) {
      this.checkNotNull();
      _setNbSpeedResponsesPerCommandValue(this.address, index, value);
   }

   private static native void _setNbSpeedResponsesPerCommandValue(long var0, int var2, short var3);

   public float getCommandValues(int index) {
      this.checkNotNull();
      return _getCommandValues(this.address, index);
   }

   private static native float _getCommandValues(long var0, int var2);

   public void setCommandValues(int index, float value) {
      this.checkNotNull();
      _setCommandValues(this.address, index, value);
   }

   private static native void _setCommandValues(long var0, int var2, float var3);

   public short getNbCommandValues() {
      this.checkNotNull();
      return _getNbCommandValues(this.address);
   }

   private static native short _getNbCommandValues(long var0);

   public void setNbCommandValues(short value) {
      this.checkNotNull();
      _setNbCommandValues(this.address, value);
   }

   private static native void _setNbCommandValues(long var0, short var2);

   public void clear() {
      this.checkNotNull();
      _clear(this.address);
   }

   private static native void _clear(long var0);

   public boolean addResponse(PxVehicleCommandValueResponseTable commandValueSpeedResponses) {
      this.checkNotNull();
      return _addResponse(this.address, commandValueSpeedResponses.getAddress());
   }

   private static native boolean _addResponse(long var0, long var2);
}
