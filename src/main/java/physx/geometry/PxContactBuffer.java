package physx.geometry;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxVec3;

public class PxContactBuffer extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxContactBuffer() {
   }

   private static native int __sizeOf();

   public static PxContactBuffer wrapPointer(long address) {
      return address != 0L ? new PxContactBuffer(address) : null;
   }

   public static PxContactBuffer arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxContactBuffer(long address) {
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

   public PxContactPoint getContacts(int index) {
      this.checkNotNull();
      return PxContactPoint.wrapPointer(_getContacts(this.address, index));
   }

   private static native long _getContacts(long var0, int var2);

   public void setContacts(int index, PxContactPoint value) {
      this.checkNotNull();
      _setContacts(this.address, index, value.getAddress());
   }

   private static native void _setContacts(long var0, int var2, long var3);

   public int getCount() {
      this.checkNotNull();
      return _getCount(this.address);
   }

   private static native int _getCount(long var0);

   public void setCount(int value) {
      this.checkNotNull();
      _setCount(this.address, value);
   }

   private static native void _setCount(long var0, int var2);

   public int getPad() {
      this.checkNotNull();
      return _getPad(this.address);
   }

   private static native int _getPad(long var0);

   public void setPad(int value) {
      this.checkNotNull();
      _setPad(this.address, value);
   }

   private static native void _setPad(long var0, int var2);

   public static int getMAX_CONTACTS() {
      return _getMAX_CONTACTS();
   }

   private static native int _getMAX_CONTACTS();

   public void reset() {
      this.checkNotNull();
      _reset(this.address);
   }

   private static native void _reset(long var0);

   public boolean contact(PxVec3 worldPoint, PxVec3 worldNormalIn, float separation) {
      this.checkNotNull();
      return _contact(this.address, worldPoint.getAddress(), worldNormalIn.getAddress(), separation);
   }

   private static native boolean _contact(long var0, long var2, long var4, float var6);

   public boolean contact(PxVec3 worldPoint, PxVec3 worldNormalIn, float separation, int faceIndex1) {
      this.checkNotNull();
      return _contact(this.address, worldPoint.getAddress(), worldNormalIn.getAddress(), separation, faceIndex1);
   }

   private static native boolean _contact(long var0, long var2, long var4, float var6, int var7);

   public boolean contact(PxContactPoint pt) {
      this.checkNotNull();
      return _contact(this.address, pt.getAddress());
   }

   private static native boolean _contact(long var0, long var2);

   public PxContactPoint contact() {
      this.checkNotNull();
      PlatformChecks.requirePlatform(15, "physx.geometry.PxContactBuffer");
      return PxContactPoint.wrapPointer(_contact(this.address));
   }

   private static native long _contact(long var0);
}
