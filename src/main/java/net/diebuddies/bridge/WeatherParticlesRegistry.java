package net.diebuddies.bridge;

import net.diebuddies.minecraft.weather.DustParticle;
import net.diebuddies.minecraft.weather.RainParticle;
import net.diebuddies.minecraft.weather.SnowParticle;
import net.diebuddies.mixins.MixinParticleResourcesAccessor;
import net.diebuddies.physics.ocean.ExplosionOceanSplashParticle;
import net.diebuddies.physics.ocean.OceanSplashParticle;
import net.diebuddies.physics.ocean.SmallOceanSplashParticle;
import net.diebuddies.physics.smoke.EmberParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleResources.MutableSpriteSet;
import net.minecraft.client.particle.ParticleResources.SpriteParticleRegistration;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

public class WeatherParticlesRegistry {
   public static final Identifier RAIN_RESOURCE = Identifier.fromNamespaceAndPath("physicsmod", "rain");
   public static final Identifier SNOW_RESOURCE = Identifier.fromNamespaceAndPath("physicsmod", "snow");
   public static final Identifier DUST_RESOURCE = Identifier.fromNamespaceAndPath("physicsmod", "dust");
   public static final Identifier SPLASH_RESOURCE = Identifier.fromNamespaceAndPath("physicsmod", "splash");
   public static final Identifier SPLASH_SMALL_RESOURCE = Identifier.fromNamespaceAndPath("physicsmod", "splash_small");
   public static final Identifier SPLASH_EXPLOSION_RESOURCE = Identifier.fromNamespaceAndPath("physicsmod", "splash_explosion");
   public static final Identifier EMBER_RESOURCE = Identifier.fromNamespaceAndPath("physicsmod", "ember");

   public static void register(IEventBus modEventBus) {
      modEventBus.register(WeatherParticlesRegistry.class);
   }

   @SubscribeEvent
   public static void registerParticles(RegisterParticleProvidersEvent event) {
      registerSpriteSet(RAIN_RESOURCE, sprite -> new RainParticle.Provider(sprite));
      registerSpriteSet(SNOW_RESOURCE, sprite -> new SnowParticle.Provider(sprite));
      registerSpriteSet(DUST_RESOURCE, sprite -> new DustParticle.Provider(sprite));
      registerSpriteSet(SPLASH_RESOURCE, sprite -> new OceanSplashParticle.Provider(sprite));
      registerSpriteSet(SPLASH_SMALL_RESOURCE, sprite -> new SmallOceanSplashParticle.Provider(sprite));
      registerSpriteSet(SPLASH_EXPLOSION_RESOURCE, sprite -> new ExplosionOceanSplashParticle.Provider(sprite));
      registerSpriteSet(EMBER_RESOURCE, sprite -> new EmberParticle.Provider(sprite));
   }

   private static <T extends ParticleOptions> void registerSpriteSet(Identifier resource, SpriteParticleRegistration<T> registration) {
      MixinParticleResourcesAccessor particleEngine = (MixinParticleResourcesAccessor)Minecraft.getInstance().particleEngine.resourceManager;
      MutableSpriteSet spriteSet = new MutableSpriteSet();
      particleEngine.getSpriteSets().put(resource, spriteSet);
      particleEngine.getParticleProviders().put(resource, registration.create(spriteSet));
   }
}
