package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxDiffuseParticleParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxDiffuseParticleParams wrapPointer(long address) {
      return address != 0L ? new PxDiffuseParticleParams(address) : null;
   }

   public static PxDiffuseParticleParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxDiffuseParticleParams(long address) {
      super(address);
   }

   public static PxDiffuseParticleParams createAt(long address) {
      __placement_new_PxDiffuseParticleParams(address);
      PxDiffuseParticleParams createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxDiffuseParticleParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxDiffuseParticleParams(address);
      PxDiffuseParticleParams createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxDiffuseParticleParams(long var0);

   public PxDiffuseParticleParams() {
      this.address = _PxDiffuseParticleParams();
   }

   private static native long _PxDiffuseParticleParams();

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

   public float getThreshold() {
      this.checkNotNull();
      return _getThreshold(this.address);
   }

   private static native float _getThreshold(long var0);

   public void setThreshold(float value) {
      this.checkNotNull();
      _setThreshold(this.address, value);
   }

   private static native void _setThreshold(long var0, float var2);

   public float getLifetime() {
      this.checkNotNull();
      return _getLifetime(this.address);
   }

   private static native float _getLifetime(long var0);

   public void setLifetime(float value) {
      this.checkNotNull();
      _setLifetime(this.address, value);
   }

   private static native void _setLifetime(long var0, float var2);

   public float getAirDrag() {
      this.checkNotNull();
      return _getAirDrag(this.address);
   }

   private static native float _getAirDrag(long var0);

   public void setAirDrag(float value) {
      this.checkNotNull();
      _setAirDrag(this.address, value);
   }

   private static native void _setAirDrag(long var0, float var2);

   public float getBubbleDrag() {
      this.checkNotNull();
      return _getBubbleDrag(this.address);
   }

   private static native float _getBubbleDrag(long var0);

   public void setBubbleDrag(float value) {
      this.checkNotNull();
      _setBubbleDrag(this.address, value);
   }

   private static native void _setBubbleDrag(long var0, float var2);

   public float getBuoyancy() {
      this.checkNotNull();
      return _getBuoyancy(this.address);
   }

   private static native float _getBuoyancy(long var0);

   public void setBuoyancy(float value) {
      this.checkNotNull();
      _setBuoyancy(this.address, value);
   }

   private static native void _setBuoyancy(long var0, float var2);

   public float getKineticEnergyWeight() {
      this.checkNotNull();
      return _getKineticEnergyWeight(this.address);
   }

   private static native float _getKineticEnergyWeight(long var0);

   public void setKineticEnergyWeight(float value) {
      this.checkNotNull();
      _setKineticEnergyWeight(this.address, value);
   }

   private static native void _setKineticEnergyWeight(long var0, float var2);

   public float getPressureWeight() {
      this.checkNotNull();
      return _getPressureWeight(this.address);
   }

   private static native float _getPressureWeight(long var0);

   public void setPressureWeight(float value) {
      this.checkNotNull();
      _setPressureWeight(this.address, value);
   }

   private static native void _setPressureWeight(long var0, float var2);

   public float getDivergenceWeight() {
      this.checkNotNull();
      return _getDivergenceWeight(this.address);
   }

   private static native float _getDivergenceWeight(long var0);

   public void setDivergenceWeight(float value) {
      this.checkNotNull();
      _setDivergenceWeight(this.address, value);
   }

   private static native void _setDivergenceWeight(long var0, float var2);

   public float getCollisionDecay() {
      this.checkNotNull();
      return _getCollisionDecay(this.address);
   }

   private static native float _getCollisionDecay(long var0);

   public void setCollisionDecay(float value) {
      this.checkNotNull();
      _setCollisionDecay(this.address, value);
   }

   private static native void _setCollisionDecay(long var0, float var2);

   public boolean getUseAccurateVelocity() {
      this.checkNotNull();
      return _getUseAccurateVelocity(this.address);
   }

   private static native boolean _getUseAccurateVelocity(long var0);

   public void setUseAccurateVelocity(boolean value) {
      this.checkNotNull();
      _setUseAccurateVelocity(this.address, value);
   }

   private static native void _setUseAccurateVelocity(long var0, boolean var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxDiffuseParticleParams");
   }
}
