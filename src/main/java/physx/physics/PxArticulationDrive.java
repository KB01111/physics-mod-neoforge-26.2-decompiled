package physx.physics;

import physx.NativeObject;

public class PxArticulationDrive extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxArticulationDrive wrapPointer(long address) {
      return address != 0L ? new PxArticulationDrive(address) : null;
   }

   public static PxArticulationDrive arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxArticulationDrive(long address) {
      super(address);
   }

   public static PxArticulationDrive createAt(long address) {
      __placement_new_PxArticulationDrive(address);
      PxArticulationDrive createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxArticulationDrive createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxArticulationDrive(address);
      PxArticulationDrive createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxArticulationDrive(long var0);

   public static PxArticulationDrive createAt(long address, float stiffness, float damping, float maxForce, PxArticulationDriveTypeEnum driveType) {
      __placement_new_PxArticulationDrive(address, stiffness, damping, maxForce, driveType.value);
      PxArticulationDrive createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxArticulationDrive createAt(
      T allocator, NativeObject.Allocator<T> allocate, float stiffness, float damping, float maxForce, PxArticulationDriveTypeEnum driveType
   ) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxArticulationDrive(address, stiffness, damping, maxForce, driveType.value);
      PxArticulationDrive createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxArticulationDrive(long var0, float var2, float var3, float var4, int var5);

   public PxArticulationDrive() {
      this.address = _PxArticulationDrive();
   }

   private static native long _PxArticulationDrive();

   public PxArticulationDrive(float stiffness, float damping, float maxForce, PxArticulationDriveTypeEnum driveType) {
      this.address = _PxArticulationDrive(stiffness, damping, maxForce, driveType.value);
   }

   private static native long _PxArticulationDrive(float var0, float var1, float var2, int var3);

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

   public float getStiffness() {
      this.checkNotNull();
      return _getStiffness(this.address);
   }

   private static native float _getStiffness(long var0);

   public void setStiffness(float value) {
      this.checkNotNull();
      _setStiffness(this.address, value);
   }

   private static native void _setStiffness(long var0, float var2);

   public float getDamping() {
      this.checkNotNull();
      return _getDamping(this.address);
   }

   private static native float _getDamping(long var0);

   public void setDamping(float value) {
      this.checkNotNull();
      _setDamping(this.address, value);
   }

   private static native void _setDamping(long var0, float var2);

   public float getMaxForce() {
      this.checkNotNull();
      return _getMaxForce(this.address);
   }

   private static native float _getMaxForce(long var0);

   public void setMaxForce(float value) {
      this.checkNotNull();
      _setMaxForce(this.address, value);
   }

   private static native void _setMaxForce(long var0, float var2);

   public PxArticulationDriveTypeEnum getDriveType() {
      this.checkNotNull();
      return PxArticulationDriveTypeEnum.forValue(_getDriveType(this.address));
   }

   private static native int _getDriveType(long var0);

   public void setDriveType(PxArticulationDriveTypeEnum value) {
      this.checkNotNull();
      _setDriveType(this.address, value.value);
   }

   private static native void _setDriveType(long var0, int var2);
}
