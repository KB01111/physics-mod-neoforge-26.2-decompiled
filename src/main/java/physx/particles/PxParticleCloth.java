package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxParticleCloth extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxParticleCloth wrapPointer(long address) {
      return address != 0L ? new PxParticleCloth(address) : null;
   }

   public static PxParticleCloth arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxParticleCloth(long address) {
      super(address);
   }

   public static PxParticleCloth createAt(long address) {
      __placement_new_PxParticleCloth(address);
      PxParticleCloth createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxParticleCloth createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxParticleCloth(address);
      PxParticleCloth createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxParticleCloth(long var0);

   public PxParticleCloth() {
      this.address = _PxParticleCloth();
   }

   private static native long _PxParticleCloth();

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

   public int getStartVertexIndex() {
      this.checkNotNull();
      return _getStartVertexIndex(this.address);
   }

   private static native int _getStartVertexIndex(long var0);

   public void setStartVertexIndex(int value) {
      this.checkNotNull();
      _setStartVertexIndex(this.address, value);
   }

   private static native void _setStartVertexIndex(long var0, int var2);

   public int getNumVertices() {
      this.checkNotNull();
      return _getNumVertices(this.address);
   }

   private static native int _getNumVertices(long var0);

   public void setNumVertices(int value) {
      this.checkNotNull();
      _setNumVertices(this.address, value);
   }

   private static native void _setNumVertices(long var0, int var2);

   public float getClothBlendScale() {
      this.checkNotNull();
      return _getClothBlendScale(this.address);
   }

   private static native float _getClothBlendScale(long var0);

   public void setClothBlendScale(float value) {
      this.checkNotNull();
      _setClothBlendScale(this.address, value);
   }

   private static native void _setClothBlendScale(long var0, float var2);

   public float getRestVolume() {
      this.checkNotNull();
      return _getRestVolume(this.address);
   }

   private static native float _getRestVolume(long var0);

   public void setRestVolume(float value) {
      this.checkNotNull();
      _setRestVolume(this.address, value);
   }

   private static native void _setRestVolume(long var0, float var2);

   public float getPressure() {
      this.checkNotNull();
      return _getPressure(this.address);
   }

   private static native float _getPressure(long var0);

   public void setPressure(float value) {
      this.checkNotNull();
      _setPressure(this.address, value);
   }

   private static native void _setPressure(long var0, float var2);

   public int getStartTriangleIndex() {
      this.checkNotNull();
      return _getStartTriangleIndex(this.address);
   }

   private static native int _getStartTriangleIndex(long var0);

   public void setStartTriangleIndex(int value) {
      this.checkNotNull();
      _setStartTriangleIndex(this.address, value);
   }

   private static native void _setStartTriangleIndex(long var0, int var2);

   public int getNumTriangles() {
      this.checkNotNull();
      return _getNumTriangles(this.address);
   }

   private static native int _getNumTriangles(long var0);

   public void setNumTriangles(int value) {
      this.checkNotNull();
      _setNumTriangles(this.address, value);
   }

   private static native void _setNumTriangles(long var0, int var2);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleCloth");
   }
}
