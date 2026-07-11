package net.diebuddies.minecraft.weather;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class WeatherEffects {
   public static final double MAX_DISTANCE = 14.0;
   public static final double MAX_DISTANCE_SQUARED = 196.0;
   public static final int SPAWN_RADIUS = 14;
   public static final double SPAWN_RADIUS_SQUARED = 196.0;
   public static final int BLOCK_CHECK_RADIUS = 6;
   public static final int BLOCK_CHECK_RADIUS_SQUARED = 36;
   public static final int SOUND_WIND_RADIUS = 6;
   public static final int SOUND_WIND_RADIUS_SQUARED = 36;
   public static volatile int aliveParticles = 0;
   public static boolean invalidateLight = false;
   public static final SimpleParticleType PHYSICS_RAIN = new SimpleParticleType(true) {
   };
   public static final SimpleParticleType PHYSICS_SNOW = new SimpleParticleType(true) {
   };
   public static final SimpleParticleType PHYSICS_DUST = new SimpleParticleType(true) {
   };
   public static final SimpleParticleType PHYSICS_SPLASH = new SimpleParticleType(true) {
   };
   public static final SimpleParticleType PHYSICS_SPLASH_SMALL = new SimpleParticleType(true) {
   };
   public static final SimpleParticleType PHYSICS_SPLASH_EXPLOSION = new SimpleParticleType(true) {
   };
   public static final SimpleParticleType PHYSICS_EMBER = new SimpleParticleType(true) {
   };
   public static final Identifier WIND_SOUND_ID = Identifier.fromNamespaceAndPath("physicsmod", "wind");
   public static final SoundEvent WIND_SOUND_EVENT = SoundEvent.createVariableRangeEvent(WIND_SOUND_ID);
   public static final Identifier SPLASH_SOUND_ID = Identifier.fromNamespaceAndPath("physicsmod", "splash_sound");
   public static final SoundEvent SPLASH_SOUND_EVENT = SoundEvent.createVariableRangeEvent(SPLASH_SOUND_ID);
}
