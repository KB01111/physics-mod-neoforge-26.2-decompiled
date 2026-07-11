package net.diebuddies.jbox2d.common;

public class Settings {
   public static final float EPSILON = 1.1920929E-7F;
   public static final float PI = (float) Math.PI;
   public static boolean FAST_ABS = true;
   public static boolean FAST_FLOOR = true;
   public static boolean FAST_CEIL = true;
   public static boolean FAST_ROUND = true;
   public static boolean FAST_ATAN2 = true;
   public static boolean FAST_POW = true;
   public static int CONTACT_STACK_INIT_SIZE = 3;
   public static int maxManifoldPoints = 2;
   public static int maxPolygonVertices = 8;
   public static float aabbExtension = 0.1F;
   public static float aabbMultiplier = 2.0F;
   public static float linearSlop = 0.005F;
   public static float angularSlop = 0.03490659F;
   public static float polygonRadius = 2.0F * linearSlop;
   public static int maxSubSteps = 8;
   public static int maxTOIContacts = 32;
   public static float velocityThreshold = 1.0F;
   public static float maxLinearCorrection = 0.2F;
   public static float maxAngularCorrection = 0.13962635F;
   public static float maxTranslation = 2.0F;
   public static float maxTranslationSquared = maxTranslation * maxTranslation;
   public static float maxRotation = (float) (Math.PI / 2);
   public static float maxRotationSquared = maxRotation * maxRotation;
   public static float baumgarte = 0.2F;
   public static float toiBaugarte = 0.75F;
   public static float timeToSleep = 0.5F;
   public static float linearSleepTolerance = 0.01F;
   public static float angularSleepTolerance = 0.03490659F;
   public static final int invalidParticleIndex = -1;
   public static final float particleStride = 0.75F;
   public static final float minParticleWeight = 1.0F;
   public static final float maxParticleWeight = 5.0F;
   public static final int maxTriadDistance = 2;
   public static final int maxTriadDistanceSquared = 4;
   public static final int minParticleBufferCapacity = 256;

   public static float mixFriction(float friction1, float friction2) {
      return MathUtils.sqrt(friction1 * friction2);
   }

   public static float mixRestitution(float restitution1, float restitution2) {
      return restitution1 > restitution2 ? restitution1 : restitution2;
   }
}
