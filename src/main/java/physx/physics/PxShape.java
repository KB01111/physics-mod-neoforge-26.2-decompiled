package physx.physics;

import physx.NativeObject;
import physx.common.PxRefCounted;
import physx.common.PxTransform;
import physx.geometry.PxGeometry;
import physx.support.PxMaterialPtr;

public class PxShape extends PxRefCounted {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxShape() {
   }

   private static native int __sizeOf();

   public static PxShape wrapPointer(long address) {
      return address != 0L ? new PxShape(address) : null;
   }

   public static PxShape arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxShape(long address) {
      super(address);
   }

   public NativeObject getUserData() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_getUserData(this.address));
   }

   private static native long _getUserData(long var0);

   public void setUserData(NativeObject value) {
      this.checkNotNull();
      _setUserData(this.address, value.getAddress());
   }

   private static native void _setUserData(long var0, long var2);

   public void setGeometry(PxGeometry geometry) {
      this.checkNotNull();
      _setGeometry(this.address, geometry.getAddress());
   }

   private static native void _setGeometry(long var0, long var2);

   public PxGeometry getGeometry() {
      this.checkNotNull();
      return PxGeometry.wrapPointer(_getGeometry(this.address));
   }

   private static native long _getGeometry(long var0);

   public PxRigidActor getActor() {
      this.checkNotNull();
      return PxRigidActor.wrapPointer(_getActor(this.address));
   }

   private static native long _getActor(long var0);

   public void setMaterials(PxMaterialPtr materials, short materialCount) {
      this.checkNotNull();
      _setMaterials(this.address, materials.getAddress(), materialCount);
   }

   private static native void _setMaterials(long var0, long var2, short var4);

   public short getNbMaterials() {
      this.checkNotNull();
      return _getNbMaterials(this.address);
   }

   private static native short _getNbMaterials(long var0);

   public int getMaterials(PxMaterialPtr userBuffer, int bufferSize, int startIndex) {
      this.checkNotNull();
      return _getMaterials(this.address, userBuffer.getAddress(), bufferSize, startIndex);
   }

   private static native int _getMaterials(long var0, long var2, int var4, int var5);

   public PxBaseMaterial getMaterialFromInternalFaceIndex(int faceIndex) {
      this.checkNotNull();
      return PxBaseMaterial.wrapPointer(_getMaterialFromInternalFaceIndex(this.address, faceIndex));
   }

   private static native long _getMaterialFromInternalFaceIndex(long var0, int var2);

   public void setContactOffset(float contactOffset) {
      this.checkNotNull();
      _setContactOffset(this.address, contactOffset);
   }

   private static native void _setContactOffset(long var0, float var2);

   public float getContactOffset() {
      this.checkNotNull();
      return _getContactOffset(this.address);
   }

   private static native float _getContactOffset(long var0);

   public void setRestOffset(float restOffset) {
      this.checkNotNull();
      _setRestOffset(this.address, restOffset);
   }

   private static native void _setRestOffset(long var0, float var2);

   public float getRestOffset() {
      this.checkNotNull();
      return _getRestOffset(this.address);
   }

   private static native float _getRestOffset(long var0);

   public void setTorsionalPatchRadius(float radius) {
      this.checkNotNull();
      _setTorsionalPatchRadius(this.address, radius);
   }

   private static native void _setTorsionalPatchRadius(long var0, float var2);

   public float getTorsionalPatchRadius() {
      this.checkNotNull();
      return _getTorsionalPatchRadius(this.address);
   }

   private static native float _getTorsionalPatchRadius(long var0);

   public void setMinTorsionalPatchRadius(float radius) {
      this.checkNotNull();
      _setMinTorsionalPatchRadius(this.address, radius);
   }

   private static native void _setMinTorsionalPatchRadius(long var0, float var2);

   public float getMinTorsionalPatchRadius() {
      this.checkNotNull();
      return _getMinTorsionalPatchRadius(this.address);
   }

   private static native float _getMinTorsionalPatchRadius(long var0);

   public void setFlag(PxShapeFlagEnum flag, boolean value) {
      this.checkNotNull();
      _setFlag(this.address, flag.value, value);
   }

   private static native void _setFlag(long var0, int var2, boolean var3);

   public void setFlags(PxShapeFlags inFlags) {
      this.checkNotNull();
      _setFlags(this.address, inFlags.getAddress());
   }

   private static native void _setFlags(long var0, long var2);

   public PxShapeFlags getFlags() {
      this.checkNotNull();
      return PxShapeFlags.wrapPointer(_getFlags(this.address));
   }

   private static native long _getFlags(long var0);

   public boolean isExclusive() {
      this.checkNotNull();
      return _isExclusive(this.address);
   }

   private static native boolean _isExclusive(long var0);

   public void setName(String name) {
      this.checkNotNull();
      _setName(this.address, name);
   }

   private static native void _setName(long var0, String var2);

   public String getName() {
      this.checkNotNull();
      return _getName(this.address);
   }

   private static native String _getName(long var0);

   public void setLocalPose(PxTransform pose) {
      this.checkNotNull();
      _setLocalPose(this.address, pose.getAddress());
   }

   private static native void _setLocalPose(long var0, long var2);

   public PxTransform getLocalPose() {
      this.checkNotNull();
      return PxTransform.wrapPointer(_getLocalPose(this.address));
   }

   private static native long _getLocalPose(long var0);

   public void setSimulationFilterData(PxFilterData data) {
      this.checkNotNull();
      _setSimulationFilterData(this.address, data.getAddress());
   }

   private static native void _setSimulationFilterData(long var0, long var2);

   public PxFilterData getSimulationFilterData() {
      this.checkNotNull();
      return PxFilterData.wrapPointer(_getSimulationFilterData(this.address));
   }

   private static native long _getSimulationFilterData(long var0);

   public void setQueryFilterData(PxFilterData data) {
      this.checkNotNull();
      _setQueryFilterData(this.address, data.getAddress());
   }

   private static native void _setQueryFilterData(long var0, long var2);

   public PxFilterData getQueryFilterData() {
      this.checkNotNull();
      return PxFilterData.wrapPointer(_getQueryFilterData(this.address));
   }

   private static native long _getQueryFilterData(long var0);
}
