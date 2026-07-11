package physx.particles;

import physx.PlatformChecks;
import physx.common.PxVec3;

public class PxPBDParticleSystem extends PxParticleSystem {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxPBDParticleSystem() {
   }

   private static native int __sizeOf();

   public static PxPBDParticleSystem wrapPointer(long address) {
      return address != 0L ? new PxPBDParticleSystem(address) : null;
   }

   public static PxPBDParticleSystem arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxPBDParticleSystem(long address) {
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

   public void setWind(PxVec3 wind) {
      this.checkNotNull();
      _setWind(this.address, wind.getAddress());
   }

   private static native void _setWind(long var0, long var2);

   public PxVec3 getWind() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getWind(this.address));
   }

   private static native long _getWind(long var0);

   public void setFluidBoundaryDensityScale(float fluidBoundaryDensityScale) {
      this.checkNotNull();
      _setFluidBoundaryDensityScale(this.address, fluidBoundaryDensityScale);
   }

   private static native void _setFluidBoundaryDensityScale(long var0, float var2);

   public float getFluidBoundaryDensityScale() {
      this.checkNotNull();
      return _getFluidBoundaryDensityScale(this.address);
   }

   private static native float _getFluidBoundaryDensityScale(long var0);

   public void setFluidRestOffset(float fluidRestOffset) {
      this.checkNotNull();
      _setFluidRestOffset(this.address, fluidRestOffset);
   }

   private static native void _setFluidRestOffset(long var0, float var2);

   public float getFluidRestOffset() {
      this.checkNotNull();
      return _getFluidRestOffset(this.address);
   }

   private static native float _getFluidRestOffset(long var0);

   public void setGridSizeX(int gridSizeX) {
      this.checkNotNull();
      _setGridSizeX(this.address, gridSizeX);
   }

   private static native void _setGridSizeX(long var0, int var2);

   public void setGridSizeY(int gridSizeY) {
      this.checkNotNull();
      _setGridSizeY(this.address, gridSizeY);
   }

   private static native void _setGridSizeY(long var0, int var2);

   public void setGridSizeZ(int gridSizeZ) {
      this.checkNotNull();
      _setGridSizeZ(this.address, gridSizeZ);
   }

   private static native void _setGridSizeZ(long var0, int var2);

   @Override
   public String getConcreteTypeName() {
      this.checkNotNull();
      return _getConcreteTypeName(this.address);
   }

   private static native String _getConcreteTypeName(long var0);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxPBDParticleSystem");
   }
}
