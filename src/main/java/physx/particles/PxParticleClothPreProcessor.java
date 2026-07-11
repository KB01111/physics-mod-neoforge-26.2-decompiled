package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxParticleClothPreProcessor extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxParticleClothPreProcessor() {
   }

   private static native int __sizeOf();

   public static PxParticleClothPreProcessor wrapPointer(long address) {
      return address != 0L ? new PxParticleClothPreProcessor(address) : null;
   }

   public static PxParticleClothPreProcessor arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxParticleClothPreProcessor(long address) {
      super(address);
   }

   public void release() {
      this.checkNotNull();
      _release(this.address);
   }

   private static native void _release(long var0);

   public void partitionSprings(PxParticleClothDesc clothDesc, PxPartitionedParticleCloth output) {
      this.checkNotNull();
      _partitionSprings(this.address, clothDesc.getAddress(), output.getAddress());
   }

   private static native void _partitionSprings(long var0, long var2, long var4);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothPreProcessor");
   }
}
