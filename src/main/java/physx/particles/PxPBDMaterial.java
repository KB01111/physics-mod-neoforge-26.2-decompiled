package physx.particles;

import physx.PlatformChecks;

public class PxPBDMaterial extends PxParticleMaterial {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxPBDMaterial() {
   }

   private static native int __sizeOf();

   public static PxPBDMaterial wrapPointer(long address) {
      return address != 0L ? new PxPBDMaterial(address) : null;
   }

   public static PxPBDMaterial arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxPBDMaterial(long address) {
      super(address);
   }

   public void setViscosity(float viscosity) {
      this.checkNotNull();
      _setViscosity(this.address, viscosity);
   }

   private static native void _setViscosity(long var0, float var2);

   public float getViscosity() {
      this.checkNotNull();
      return _getViscosity(this.address);
   }

   private static native float _getViscosity(long var0);

   public void setVorticityConfinement(float vorticityConfinement) {
      this.checkNotNull();
      _setVorticityConfinement(this.address, vorticityConfinement);
   }

   private static native void _setVorticityConfinement(long var0, float var2);

   public float getVorticityConfinement() {
      this.checkNotNull();
      return _getVorticityConfinement(this.address);
   }

   private static native float _getVorticityConfinement(long var0);

   public void setSurfaceTension(float surfaceTension) {
      this.checkNotNull();
      _setSurfaceTension(this.address, surfaceTension);
   }

   private static native void _setSurfaceTension(long var0, float var2);

   public float getSurfaceTension() {
      this.checkNotNull();
      return _getSurfaceTension(this.address);
   }

   private static native float _getSurfaceTension(long var0);

   public void setCohesion(float cohesion) {
      this.checkNotNull();
      _setCohesion(this.address, cohesion);
   }

   private static native void _setCohesion(long var0, float var2);

   public float getCohesion() {
      this.checkNotNull();
      return _getCohesion(this.address);
   }

   private static native float _getCohesion(long var0);

   @Deprecated
   public void setLift(float lift) {
      this.checkNotNull();
      _setLift(this.address, lift);
   }

   private static native void _setLift(long var0, float var2);

   @Deprecated
   public float getLift() {
      this.checkNotNull();
      return _getLift(this.address);
   }

   private static native float _getLift(long var0);

   @Deprecated
   public void setDrag(float drag) {
      this.checkNotNull();
      _setDrag(this.address, drag);
   }

   private static native void _setDrag(long var0, float var2);

   @Deprecated
   public float getDrag() {
      this.checkNotNull();
      return _getDrag(this.address);
   }

   private static native float _getDrag(long var0);

   public void setCFLCoefficient(float coefficient) {
      this.checkNotNull();
      _setCFLCoefficient(this.address, coefficient);
   }

   private static native void _setCFLCoefficient(long var0, float var2);

   public float getCFLCoefficient() {
      this.checkNotNull();
      return _getCFLCoefficient(this.address);
   }

   private static native float _getCFLCoefficient(long var0);

   public void setParticleFrictionScale(float scale) {
      this.checkNotNull();
      _setParticleFrictionScale(this.address, scale);
   }

   private static native void _setParticleFrictionScale(long var0, float var2);

   public float getParticleFrictionScale() {
      this.checkNotNull();
      return _getParticleFrictionScale(this.address);
   }

   private static native float _getParticleFrictionScale(long var0);

   public void setParticleAdhesionScale(float adhesion) {
      this.checkNotNull();
      _setParticleAdhesionScale(this.address, adhesion);
   }

   private static native void _setParticleAdhesionScale(long var0, float var2);

   public float getParticleAdhesionScale() {
      this.checkNotNull();
      return _getParticleAdhesionScale(this.address);
   }

   private static native float _getParticleAdhesionScale(long var0);

   @Override
   public String getConcreteTypeName() {
      this.checkNotNull();
      return _getConcreteTypeName(this.address);
   }

   private static native String _getConcreteTypeName(long var0);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxPBDMaterial");
   }
}
