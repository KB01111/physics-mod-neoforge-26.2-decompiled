package net.diebuddies.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import net.diebuddies.physics.DynamicsWorld;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.OceanLayer;
import net.diebuddies.physics.settings.blocks.BlockPhysicsType;
import net.diebuddies.physics.settings.blocks.BlockSetting;
import net.diebuddies.physics.settings.mobs.MobPhysicsType;
import net.diebuddies.physics.settings.mobs.MobSetting;
import net.diebuddies.physics.vines.AdjustableUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.joml.Vector3f;

public final class ConfigClient {
   public static final int PLAYER_SIMULATION_QUALITY = 90;
   public static final int CLOTH_SIMULATION_QUALITY = 45;
   public static final int FISHING_ROD_SIMULATION_QUALITY = 20;
   public static final int LEASH_SIMULATION_QUALITY = 20;
   public static final int BANNER_SIMULATION_QUALITY = 25;
   public static final int SNOWBALL_VOXEL = 0;
   public static final int SNOWBALL_ROUND = 1;
   public static final int SNOWBALL_CLASSIC = 2;
   public static final int ENDERPEARL_VOXEL = 0;
   public static final int ENDERPEARL_ROUND = 1;
   public static final int ENDERPEARL_CLASSIC = 2;
   public static final int EGG_VOXEL = 0;
   public static final int EGG_ROUND = 1;
   public static final int EGG_CLASSIC = 2;
   public static final int IMPACT_SHATTER = 0;
   public static final int IMPACT_BOUNCE = 1;
   public static final int IMPACT_DISAPPEAR = 2;
   public static final int SNOW_ROUND = 0;
   public static final int SNOW_CUBE = 1;
   public static final int SNOW_CHUNK_SIZE_MEDIUM = 0;
   public static final int SNOW_CHUNK_SIZE_HIGH = 1;
   public static final int SMOKE_QUALITY_LOW = 0;
   public static final int SMOKE_QUALITY_MEDIUM = 1;
   public static final int SMOKE_QUALITY_HIGH = 2;
   private static final String DIR = "config/physicsmod";
   private static final String CONFIG = "physics_client_config.json";
   public static Map<String, Vector3f> customizedGravities = new Object2ObjectOpenHashMap();
   public static Map<String, Vector3f> customizedBuoyancies = new Object2ObjectOpenHashMap();
   public static boolean blockPhysics = true;
   public static boolean mobPhysics = true;
   public static boolean vinePhysics = true;
   public static boolean capePhysics = true;
   public static boolean itemPhysics = true;
   public static boolean fishingRodPhysics = true;
   public static boolean leashPhysics = true;
   public static boolean bannerPhysics = true;
   public static boolean liquidPhysics = true;
   public static boolean snowPhysics = true;
   public static boolean smokePhysics = true;
   public static boolean smokeVolumetricPhysics = false;
   public static boolean guiPhysics = false;
   public static boolean windPhysics = true;
   public static boolean weatherParticles = true;
   public static boolean oceanPhysics = true;
   public static boolean crackPhysicsParticles = true;
   public static boolean sprintingPhysicsParticles = true;
   public static boolean eatingPhysicsParticles = true;
   public static boolean serverBlockPhysicsParticles = true;
   public static boolean minecraftBlockBreakParticles = false;
   public static boolean itemBreakPhysics = true;
   public static int snowballModel = 0;
   public static int enderpearlModel = 0;
   public static int eggModel = 0;
   public static int maxPhysicsObjects = 10000;
   public static int cpuThreads = Math.max(1, Runtime.getRuntime().availableProcessors() / 4);
   public static BlockSetting blockSetting = new BlockSetting(BlockPhysicsType.FRACTURED, 4.0, 3.0, 1.0, ConfigAnimations.DEFAULT_ANIMATION);
   public static MobSetting mobSetting = new MobSetting(MobPhysicsType.RAGDOLL, 4.0, 3.0, ConfigAnimations.DEFAULT_ANIMATION);
   public static double particleLifetimeVines = 4.0;
   public static double particleLifetimeVarianceVines = 3.0;
   public static double particleLifetimeItems = 3.0;
   public static double particleLifetimeVarianceItems = 3.0;
   public static double particleLifetimeParticles = 0.1;
   public static double particleLifetimeVarianceParticles = 3.0;
   public static double particleLifetimeLiquids = 6.0;
   public static double particleLifetimeVarianceLiquids = 3.0;
   public static double particleLifetimeLiquidsCuda = 6.0;
   public static double particleLifetimeVarianceLiquidsCuda = 3.0;
   public static boolean gravityChanged = false;
   public static double bannerPhysicsRange = 48.0;
   public static volatile boolean clothSmoothShading = false;
   public static double leashLength = 3.0;
   public static double fishingLineLength = 12.0;
   public static boolean showUpdateNotifications = true;
   public static boolean pvpServerCompatibility = false;
   public static boolean snowballShade = false;
   public static boolean enderpearlShade = false;
   public static boolean eggShade = false;
   public static int clothThreads = Math.max(1, Runtime.getRuntime().availableProcessors() / 8);
   public static float clothEntityRange = 48.0F;
   public static int liquidThreads = 2;
   public static volatile double vineRange = 32.0;
   public static float impactVolume = 1.0F;
   public static float windVolume = 1.0F;
   public static double blockPhysicsRange = 96.0;
   public static boolean snowTracks = true;
   public static boolean grassSnowy = true;
   public static volatile float snowLOD = 1.0F;
   public static double snowTrackDistance = 48.0;
   public static volatile int snowTrackEntities = 6;
   public static volatile int snowType = 0;
   public static volatile boolean snowSmoothShading = true;
   public static volatile int snowQuality = 0;
   public static volatile float snowThickness = 0.0F;
   public static int snowballImpact = 1;
   public static int enderpearlImpact = 1;
   public static int eggImpact = 1;
   public static double smokePhysicsRange = 100.0;
   public static double particleLifetimeSmoke = 60.0;
   public static double particleLifetimeSmokeCuda = 60.0;
   public static double particleLifetimeSmokeVolumetric = 60.0;
   public static double particleLifetimeVarianceSmoke = 15.0;
   public static double particleLifetimeVarianceSmokeCuda = 15.0;
   public static double particleLifetimeVarianceSmokeVolumetric = 15.0;
   public static double particleDespawnTimeSmoke = 3.0;
   public static double particleDespawnTimeSmokeCuda = 3.0;
   public static double particleDespawnTimeSmokeVolumetric = 0.0;
   public static double particleDespawnTimeVarianceSmoke = 12.0;
   public static double particleDespawnTimeVarianceSmokeCuda = 3.0;
   public static double particleDespawnTimeVarianceSmokeVolumetric = 2.5;
   public static float smokeColorRed = 0.56F;
   public static float smokeColorGreen = 0.56F;
   public static float smokeColorBlue = 0.56F;
   public static float smokeDenseColorRed = 0.364F;
   public static float smokeDenseColorGreen = 0.364F;
   public static float smokeDenseColorBlue = 0.364F;
   public static float smokeVolumeColorRed = 0.2F;
   public static float smokeVolumeColorGreen = 0.175F;
   public static float smokeVolumeColorBlue = 0.15F;
   public static float smokeDensity = 0.9F;
   public static float smokeBlaze = 0.0F;
   public static float smokeCampfire = 0.3F;
   public static float smokeFire = 0.3F;
   public static float smokeVolumeFire = 0.25F;
   public static float smokeVolumeRadius = 1.0F;
   public static float smokeVolumeShadowStrength = 0.5F;
   public static boolean smokeVolumeShadow = true;
   public static float smokeOther = 1.0F;
   public static float smokeEmberParticlesAmount = 1.0F;
   public static int smokeVolumeQuality = 1;
   public static int smokeParticleLimit = 6000;
   public static int smokeParticleLimitCuda = 50000;
   public static int maxLoadedDynamicBlocks = 20;
   public static float weatherClearStrength = 0.1F;
   public static float weatherRainStrength = 1.0F;
   public static float weatherThunderStrength = 1.4F;
   public static int weatherRainParticleAmount = 5;
   public static int weatherThunderParticleAmount = 2;
   public static float particleRainOpacity = 1.0F;
   public static float particleSnowOpacity = 1.0F;
   public static float particleDustOpacity = 1.0F;
   public static String verificationCode = "";
   public static boolean firstStartup;
   public static float oceanDetail = 1.0F;
   public static boolean oceanAdjustHitbox = false;
   public static float oceanWaveHeightMultiplier = 1.0F;
   public static byte oceanBlockRange = 32;
   public static float oceanWeatherClear = 0.0F;
   public static float oceanWeatherRain = 0.75F;
   public static float oceanWeatherThunder = 0.25F;
   public static float oceanBaseSpeed = 1.0F;
   public static float oceanHorizontalWaveScale = 1.0F;
   public static boolean oceanParticles = false;
   public static float oceanParticleAlpha = 0.5F;
   public static float oceanFoamAmount = 0.8F;
   public static float oceanFoamOpacity = 0.5F;
   public static boolean oceanStickyEntities = false;
   public static float oceanSplashVolume = 1.0F;
   public static float itemRotationSpeed = 1.0F;
   public static float jointBreakForce = 1.0F;
   public static float jointBlood = 1.0F;
   public static int mobRagdollLimit = 16;
   public static boolean clothForceArmor = false;
   public static boolean renderPhysicsDebugOverlay = false;
   public static boolean oceanRipples = true;
   public static boolean cudaLiquids = false;
   public static float cudaLiquidsParticleSize = 0.1F;
   public static float playbackSpeed = 1.0F;
   public static float oceanRainPuddleAmount = 0.5F;
   public static boolean cudaSmoke = false;
   public static int liquidCudaMaxParticles = 30000;
   public static int liquidCudaAmount = 4;
   public static float liquidParticleSize = 0.15F;
   public static int liquidAmount = 2;
   public static int liquidMaxParticles = 3000;
   private static boolean stored_vinePhysics = true;
   private static boolean stored_capePhysics = true;
   private static boolean stored_itemPhysics = true;
   private static boolean stored_fishingRodPhysics = true;
   private static boolean stored_leashPhysics = true;
   private static boolean stored_bannerPhysics = true;
   private static boolean stored_liquidPhysics = true;
   private static boolean stored_snowPhysics = true;
   private static boolean stored_smokePhysics = true;
   private static boolean stored_smokeVolumetricPhysics = true;
   private static boolean stored_guiPhysics = false;
   private static boolean stored_windPhysics = true;
   private static boolean stored_weatherParticles = true;
   private static boolean stored_oceanPhysics = true;
   private static boolean stored_crackPhysicsParticles = true;
   private static boolean stored_sprintingPhysicsParticles = true;
   private static boolean stored_eatingPhysicsParticles = true;
   private static boolean stored_serverBlockPhysicsParticles = true;
   private static boolean stored_minecraftBlockBreakParticles = false;
   private static boolean stored_itemBreakPhysics = false;
   private static int stored_snowballModel = 0;
   private static int stored_enderpearlModel = 0;
   private static int stored_eggModel = 0;
   private static boolean stored = false;

   public static void reload() {
      JsonObject config = createConfig();
      File directory = new File("config/physicsmod");
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File configFile = new File("config/physicsmod/physics_client_config.json");
      if (!configFile.exists()) {
         firstStartup = true;

         try {
            configFile.createNewFile();

            try (Writer writer = new FileWriter(configFile)) {
               Gson gson = new GsonBuilder().setPrettyPrinting().create();
               gson.toJson(config, writer);
            }
         } catch (IOException var151) {
            var151.printStackTrace();
         }
      } else {
         Gson gson = new Gson();

         try {
            config = (JsonObject)gson.fromJson(new FileReader(configFile), JsonObject.class);
         } catch (JsonIOException | FileNotFoundException | JsonSyntaxException var149) {
            var149.printStackTrace();
         }

         firstStartup = false;
      }

      try {
         maxPhysicsObjects = config.get("maxPhysicsObjects").getAsInt();
      } catch (Exception var147) {
      }

      try {
         particleLifetimeVines = config.get("particleLifetimeVines").getAsDouble();
      } catch (Exception var146) {
      }

      try {
         particleLifetimeItems = config.get("particleLifetimeItems").getAsDouble();
      } catch (Exception var145) {
      }

      try {
         particleLifetimeParticles = config.get("particleLifetimeParticles").getAsDouble();
      } catch (Exception var144) {
      }

      try {
         particleLifetimeLiquids = config.get("particleLifetimeLiquids").getAsDouble();
      } catch (Exception var143) {
      }

      try {
         particleLifetimeVarianceVines = config.get("particleLifetimeVarianceVines").getAsDouble();
      } catch (Exception var142) {
      }

      try {
         particleLifetimeVarianceItems = config.get("particleLifetimeVarianceItems").getAsDouble();
      } catch (Exception var141) {
      }

      try {
         particleLifetimeVarianceParticles = config.get("particleLifetimeVarianceParticles").getAsDouble();
      } catch (Exception var140) {
      }

      try {
         particleLifetimeVarianceLiquids = config.get("particleLifetimeVarianceLiquids").getAsDouble();
      } catch (Exception var139) {
      }

      try {
         particleLifetimeSmoke = config.get("particleLifetimeSmoke").getAsDouble();
      } catch (Exception var138) {
      }

      try {
         particleLifetimeSmokeCuda = config.get("particleLifetimeSmokeCuda").getAsDouble();
      } catch (Exception var137) {
      }

      try {
         particleLifetimeSmokeVolumetric = config.get("particleLifetimeSmokeVolumetric").getAsDouble();
      } catch (Exception var136) {
      }

      try {
         particleLifetimeVarianceSmoke = config.get("particleLifetimeVarianceSmoke").getAsDouble();
      } catch (Exception var135) {
      }

      try {
         particleLifetimeVarianceSmokeCuda = config.get("particleLifetimeVarianceSmokeCuda").getAsDouble();
      } catch (Exception var134) {
      }

      try {
         particleLifetimeVarianceSmokeVolumetric = config.get("particleLifetimeVarianceSmokeVolumetric").getAsDouble();
      } catch (Exception var133) {
      }

      try {
         particleDespawnTimeSmoke = config.get("particleDespawnTimeSmoke").getAsDouble();
      } catch (Exception var132) {
      }

      try {
         particleDespawnTimeSmokeCuda = config.get("particleDespawnTimeSmokeCuda").getAsDouble();
      } catch (Exception var131) {
      }

      try {
         particleDespawnTimeSmokeVolumetric = config.get("particleDespawnTimeSmokeVolumetric").getAsDouble();
      } catch (Exception var130) {
      }

      try {
         particleDespawnTimeVarianceSmoke = config.get("particleDespawnTimeVarianceSmoke").getAsDouble();
      } catch (Exception var129) {
      }

      try {
         particleDespawnTimeVarianceSmokeCuda = config.get("particleDespawnTimeVarianceSmokeCuda").getAsDouble();
      } catch (Exception var128) {
      }

      try {
         particleDespawnTimeVarianceSmokeVolumetric = config.get("particleDespawnTimeVarianceSmokeVolumetric").getAsDouble();
      } catch (Exception var127) {
      }

      try {
         cpuThreads = config.get("cpuThreads").getAsInt();
      } catch (Exception var126) {
      }

      try {
         itemPhysics = config.get("itemPhysics").getAsBoolean();
      } catch (Exception var125) {
      }

      try {
         minecraftBlockBreakParticles = config.get("minecraftBlockBreakParticles").getAsBoolean();
      } catch (Exception var124) {
      }

      try {
         vinePhysics = config.get("vinePhysics").getAsBoolean();
      } catch (Exception var123) {
      }

      try {
         capePhysics = config.get("capePhysics").getAsBoolean();
      } catch (Exception var122) {
      }

      try {
         fishingRodPhysics = config.get("fishingRodPhysics").getAsBoolean();
      } catch (Exception var121) {
      }

      try {
         leashPhysics = config.get("leashPhysics").getAsBoolean();
      } catch (Exception var120) {
      }

      try {
         bannerPhysics = config.get("bannerPhysics").getAsBoolean();
      } catch (Exception var119) {
      }

      try {
         clothSmoothShading = config.get("clothSmoothShading").getAsBoolean();
      } catch (Exception var118) {
      }

      try {
         showUpdateNotifications = config.get("showUpdateNotifications").getAsBoolean();
      } catch (Exception var117) {
      }

      try {
         clothThreads = config.get("clothThreads").getAsInt();
      } catch (Exception var116) {
      }

      try {
         leashLength = config.get("leashLength").getAsDouble();
      } catch (Exception var115) {
      }

      try {
         fishingLineLength = config.get("fishingLineLength").getAsDouble();
      } catch (Exception var114) {
      }

      try {
         pvpServerCompatibility = config.get("pvpServerCompatibility").getAsBoolean();
      } catch (Exception var113) {
      }

      try {
         snowballModel = config.get("snowballModel").getAsInt();
      } catch (Exception var112) {
      }

      try {
         snowballImpact = config.get("snowballImpact").getAsInt();
      } catch (Exception var111) {
      }

      try {
         snowballShade = config.get("snowballShade").getAsBoolean();
      } catch (Exception var110) {
      }

      try {
         enderpearlModel = config.get("enderpearlModel").getAsInt();
      } catch (Exception var109) {
      }

      try {
         enderpearlImpact = config.get("enderpearlImpact").getAsInt();
      } catch (Exception var108) {
      }

      try {
         enderpearlShade = config.get("enderpearlShade").getAsBoolean();
      } catch (Exception var107) {
      }

      try {
         eggModel = config.get("eggModel").getAsInt();
      } catch (Exception var106) {
      }

      try {
         eggImpact = config.get("eggImpact").getAsInt();
      } catch (Exception var105) {
      }

      try {
         eggShade = config.get("eggShade").getAsBoolean();
      } catch (Exception var104) {
      }

      try {
         crackPhysicsParticles = config.get("crackPhysicsParticles").getAsBoolean();
      } catch (Exception var103) {
      }

      try {
         liquidPhysics = config.get("liquidPhysics").getAsBoolean();
      } catch (Exception var102) {
      }

      try {
         liquidThreads = config.get("liquidThreads").getAsInt();
      } catch (Exception var101) {
      }

      try {
         bannerPhysicsRange = config.get("bannerPhysicsRange").getAsDouble();
      } catch (Exception var100) {
      }

      try {
         impactVolume = config.get("soundVolume").getAsFloat();
      } catch (Exception var99) {
      }

      try {
         blockPhysicsRange = config.get("blockPhysicsRange").getAsDouble();
      } catch (Exception var98) {
      }

      try {
         vineRange = config.get("vineRange").getAsDouble();
      } catch (Exception var97) {
      }

      try {
         snowPhysics = config.get("snowPhysics").getAsBoolean();
      } catch (Exception var96) {
      }

      try {
         snowTracks = config.get("snowTracks").getAsBoolean();
      } catch (Exception var95) {
      }

      try {
         snowTrackEntities = config.get("snowTrackEntities").getAsInt();
      } catch (Exception var94) {
      }

      try {
         snowTrackDistance = config.get("snowTrackDistance").getAsDouble();
      } catch (Exception var93) {
      }

      try {
         snowThickness = config.get("snowThickness").getAsFloat();
      } catch (Exception var92) {
      }

      try {
         grassSnowy = config.get("grassSnowy").getAsBoolean();
      } catch (Exception var91) {
      }

      try {
         snowType = config.get("snowType").getAsInt();
      } catch (Exception var90) {
      }

      try {
         snowSmoothShading = config.get("snowSmoothShading").getAsBoolean();
      } catch (Exception var89) {
      }

      try {
         snowQuality = config.get("snowChunkSize").getAsInt();
      } catch (Exception var88) {
      }

      try {
         blockSetting = (BlockSetting)AdjustableUtil.readObject(BlockSetting.class, config.get("blockSettings").getAsJsonObject());
      } catch (Exception var87) {
      }

      try {
         mobSetting = (MobSetting)AdjustableUtil.readObject(MobSetting.class, config.get("mobSettings").getAsJsonObject());
      } catch (Exception var86) {
      }

      try {
         sprintingPhysicsParticles = config.get("sprintingPhysicsParticles").getAsBoolean();
      } catch (Exception var85) {
      }

      try {
         eatingPhysicsParticles = config.get("eatingPhysicsParticles").getAsBoolean();
      } catch (Exception var84) {
      }

      try {
         serverBlockPhysicsParticles = config.get("serverBlockPhysicsParticles").getAsBoolean();
      } catch (Exception var83) {
      }

      try {
         smokePhysics = config.get("smokePhysics").getAsBoolean();
      } catch (Exception var82) {
      }

      try {
         smokeVolumetricPhysics = config.get("smokeVolumetricPhysics").getAsBoolean();
      } catch (Exception var81) {
      }

      try {
         smokeParticleLimit = config.get("smokeParticleLimit").getAsInt();
      } catch (Exception var80) {
      }

      try {
         verificationCode = config.get("verificationCode").getAsString();
      } catch (Exception var79) {
      }

      try {
         smokeColorRed = config.get("smokeColorRed").getAsFloat();
      } catch (Exception var78) {
      }

      try {
         smokeColorGreen = config.get("smokeColorGreen").getAsFloat();
      } catch (Exception var77) {
      }

      try {
         smokeColorBlue = config.get("smokeColorBlue").getAsFloat();
      } catch (Exception var76) {
      }

      try {
         smokeDenseColorRed = config.get("smokeDenseColorRed").getAsFloat();
      } catch (Exception var75) {
      }

      try {
         smokeDenseColorGreen = config.get("smokeDenseColorGreen").getAsFloat();
      } catch (Exception var74) {
      }

      try {
         smokeDenseColorBlue = config.get("smokeDenseColorBlue").getAsFloat();
      } catch (Exception var73) {
      }

      try {
         smokeVolumeColorRed = config.get("smokeVolumeColorRed").getAsFloat();
      } catch (Exception var72) {
      }

      try {
         smokeVolumeColorGreen = config.get("smokeVolumeColorGreen").getAsFloat();
      } catch (Exception var71) {
      }

      try {
         smokeVolumeColorBlue = config.get("smokeVolumeColorBlue").getAsFloat();
      } catch (Exception var70) {
      }

      try {
         smokeVolumeFire = config.get("smokeVolumeFire").getAsFloat();
      } catch (Exception var69) {
      }

      try {
         smokeVolumeRadius = config.get("smokeVolumeRadius").getAsFloat();
      } catch (Exception var68) {
      }

      try {
         smokeBlaze = config.get("smokeBlaze").getAsFloat();
      } catch (Exception var67) {
      }

      try {
         smokeFire = config.get("smokeFire").getAsFloat();
      } catch (Exception var66) {
      }

      try {
         smokeCampfire = config.get("smokeCampfire").getAsFloat();
      } catch (Exception var65) {
      }

      try {
         smokeOther = config.get("smokeOther").getAsFloat();
      } catch (Exception var64) {
      }

      try {
         smokeDensity = config.get("smokeDensity").getAsFloat();
      } catch (Exception var63) {
      }

      try {
         smokePhysicsRange = config.get("smokePhysicsRange").getAsDouble();
      } catch (Exception var62) {
      }

      try {
         maxLoadedDynamicBlocks = config.get("maxLoadedDynamicBlocks").getAsInt();
      } catch (Exception var61) {
      }

      try {
         windPhysics = config.get("windPhysics").getAsBoolean();
      } catch (Exception var60) {
      }

      try {
         weatherParticles = config.get("weatherParticles").getAsBoolean();
      } catch (Exception var59) {
      }

      try {
         weatherClearStrength = config.get("weatherClearStrength").getAsFloat();
      } catch (Exception var58) {
      }

      try {
         weatherRainStrength = config.get("weatherRainStrength").getAsFloat();
      } catch (Exception var57) {
      }

      try {
         weatherThunderStrength = config.get("weatherThunderStrength").getAsFloat();
      } catch (Exception var56) {
      }

      try {
         windVolume = config.get("windVolume").getAsFloat();
      } catch (Exception var55) {
      }

      try {
         weatherRainParticleAmount = config.get("weatherRainParticleAmount").getAsInt();
      } catch (Exception var54) {
      }

      try {
         weatherThunderParticleAmount = config.get("weatherThunderParticleAmount").getAsInt();
      } catch (Exception var53) {
      }

      try {
         oceanPhysics = config.get("oceanPhysics").getAsBoolean();
      } catch (Exception var52) {
      }

      try {
         oceanAdjustHitbox = config.get("oceanAdjustHitbox").getAsBoolean();
      } catch (Exception var51) {
      }

      try {
         oceanDetail = config.get("oceanDetail").getAsFloat();
      } catch (Exception var50) {
      }

      try {
         oceanWaveHeightMultiplier = config.get("oceanWaveHeightMultiplier").getAsFloat();
      } catch (Exception var49) {
      }

      try {
         oceanBlockRange = config.get("oceanBlockRange").getAsByte();
         OceanLayer.updateRange(oceanBlockRange);
      } catch (Exception var48) {
      }

      try {
         oceanWeatherClear = config.get("oceanWeatherClear").getAsFloat();
      } catch (Exception var47) {
      }

      try {
         oceanWeatherRain = config.get("oceanWeatherRain").getAsFloat();
      } catch (Exception var46) {
      }

      try {
         oceanWeatherThunder = config.get("oceanWeatherThunder").getAsFloat();
      } catch (Exception var45) {
      }

      try {
         oceanBaseSpeed = config.get("oceanBaseSpeed").getAsFloat();
      } catch (Exception var44) {
      }

      try {
         oceanHorizontalWaveScale = config.get("oceanHorizontalWaveScale").getAsFloat();
      } catch (Exception var43) {
      }

      try {
         oceanParticles = config.get("oceanParticles").getAsBoolean();
      } catch (Exception var42) {
      }

      try {
         oceanParticleAlpha = config.get("oceanParticleAlpha").getAsFloat();
      } catch (Exception var41) {
      }

      try {
         oceanStickyEntities = config.get("oceanStickyEntities").getAsBoolean();
      } catch (Exception var40) {
      }

      try {
         oceanSplashVolume = config.get("oceanSplashVolumeNew").getAsFloat();
      } catch (Exception var39) {
      }

      try {
         snowLOD = config.get("snowLOD").getAsFloat();
      } catch (Exception var38) {
      }

      try {
         particleRainOpacity = config.get("particleRainOpacity").getAsFloat();
      } catch (Exception var37) {
      }

      try {
         particleSnowOpacity = config.get("particleSnowOpacity").getAsFloat();
      } catch (Exception var36) {
      }

      try {
         particleDustOpacity = config.get("particleDustOpacity").getAsFloat();
      } catch (Exception var35) {
      }

      try {
         itemRotationSpeed = config.get("itemRotationSpeed").getAsFloat();
      } catch (Exception var34) {
      }

      try {
         jointBreakForce = config.get("jointBreakForce").getAsFloat();
      } catch (Exception var33) {
      }

      try {
         jointBlood = config.get("jointBlood").getAsFloat();
      } catch (Exception var32) {
      }

      try {
         mobRagdollLimit = config.get("mobRagdollLimit").getAsInt();
      } catch (Exception var31) {
      }

      try {
         clothEntityRange = config.get("clothEntityRange").getAsFloat();
      } catch (Exception var30) {
      }

      try {
         clothForceArmor = config.get("clothForceArmor").getAsBoolean();
      } catch (Exception var29) {
      }

      try {
         oceanRipples = config.get("oceanPuddles").getAsBoolean();
      } catch (Exception var28) {
      }

      try {
         oceanFoamAmount = config.get("oceanFoamAmount").getAsFloat();
      } catch (Exception var27) {
      }

      try {
         oceanFoamOpacity = config.get("oceanFoamOpacity").getAsFloat();
      } catch (Exception var26) {
      }

      try {
         cudaLiquids = config.get("cudaLiquids").getAsBoolean();
      } catch (Exception var25) {
      }

      try {
         cudaLiquidsParticleSize = config.get("cudaLiquidsParticleSize").getAsFloat();
      } catch (Exception var24) {
      }

      try {
         playbackSpeed = config.get("playbackSpeed").getAsFloat();
      } catch (Exception var23) {
      }

      try {
         oceanRainPuddleAmount = config.get("oceanRainPuddleAmount").getAsFloat();
      } catch (Exception var22) {
      }

      try {
         itemBreakPhysics = config.get("itemBreakPhysics").getAsBoolean();
      } catch (Exception var21) {
      }

      try {
         cudaSmoke = config.get("cudaSmoke").getAsBoolean();
      } catch (Exception var20) {
      }

      try {
         smokeParticleLimitCuda = config.get("smokeParticleLimitCuda").getAsInt();
      } catch (Exception var19) {
      }

      try {
         liquidCudaMaxParticles = config.get("liquidCudaMaxParticles").getAsInt();
      } catch (Exception var18) {
      }

      try {
         liquidCudaAmount = config.get("liquidCudaAmount").getAsInt();
      } catch (Exception var17) {
      }

      try {
         liquidParticleSize = config.get("liquidParticleSize").getAsFloat();
      } catch (Exception var16) {
      }

      try {
         liquidAmount = config.get("liquidAmount").getAsInt();
      } catch (Exception var15) {
      }

      try {
         liquidMaxParticles = config.get("liquidMaxParticles").getAsInt();
      } catch (Exception var14) {
      }

      try {
         particleLifetimeLiquidsCuda = config.get("particleLifetimeLiquidsCuda").getAsDouble();
      } catch (Exception var13) {
      }

      try {
         particleLifetimeVarianceLiquidsCuda = config.get("particleLifetimeVarianceLiquidsCuda").getAsDouble();
      } catch (Exception var12) {
      }

      try {
         smokeVolumeShadowStrength = config.get("smokeVolumeShadowStrength").getAsFloat();
      } catch (Exception var11) {
      }

      try {
         smokeVolumeQuality = config.get("smokeVolumeQuality").getAsInt();
      } catch (Exception var10) {
      }

      try {
         smokeEmberParticlesAmount = config.get("smokeEmberParticlesAmount").getAsFloat();
      } catch (Exception var9) {
      }

      try {
         smokeVolumeShadow = config.get("smokeVolumeShadow").getAsBoolean();
      } catch (Exception var8) {
      }

      try {
         get(config, customizedGravities, "customizedGravities");
      } catch (Exception var7) {
      }

      try {
         get(config, customizedBuoyancies, "customizedBuoyancies");
      } catch (Exception var6) {
      }

      if (blockSetting.animation == null) {
         blockSetting.animation = ConfigAnimations.DEFAULT_ANIMATION;
      }

      if (mobSetting.animation == null) {
         mobSetting.animation = ConfigAnimations.DEFAULT_ANIMATION;
      }
   }

   public static boolean areSnowPhysicsEnabled() {
      return snowPhysics && !StarterClient.immersivePortals && !StarterClient.farsight;
   }

   public static boolean areOceanPhysicsEnabled() {
      return oceanPhysics && !StarterClient.immersivePortals && !StarterClient.farsight;
   }

   public static boolean areDynamicBlockPhysicsEnabled() {
      return vinePhysics && !StarterClient.immersivePortals && !StarterClient.farsight;
   }

   public static boolean areVolumetricSmokePhysicsEnabled() {
      return smokeVolumetricPhysics;
   }

   public static void init() {
   }

   private static void get(JsonObject config, Map<String, Vector3f> map, String name) {
      JsonArray array = config.get(name).getAsJsonArray();

      for (int i = 0; i < array.size() / 4; i++) {
         String id = array.get(i * 4).getAsString();
         Float x = array.get(i * 4 + 1).getAsFloat();
         Float y = array.get(i * 4 + 2).getAsFloat();
         Float z = array.get(i * 4 + 3).getAsFloat();
         map.put(id, new Vector3f(x, y, z));
      }
   }

   public static Vector3f getGravity(String id) {
      Vector3f val = customizedGravities.get(id);
      if (val == null) {
         val = new Vector3f(DynamicsWorld.DEFAULT_GRAVITY);
      }

      return val;
   }

   public static Vector3f getGravity(Identifier id) {
      return getGravity(id.toString());
   }

   public static Vector3f getBuoyancy(String id) {
      Vector3f val = customizedBuoyancies.get(id);
      if (val == null) {
         val = new Vector3f(DynamicsWorld.DEFAULT_BUOYANCY);
      }

      return val;
   }

   public static Vector3f getBuoyancy(Identifier id) {
      return getBuoyancy(id.toString());
   }

   public static void setGravity(String id, Vector3f gravity) {
      customizedGravities.put(id, gravity);
      gravityChanged = true;
   }

   public static void setBuoyancy(String id, Vector3f buoyancy) {
      customizedBuoyancies.put(id, buoyancy);
      gravityChanged = true;
   }

   public static boolean addGravityBuoyancyEntry(Identifier id) {
      if (!customizedGravities.containsKey(id.toString())) {
         customizedGravities.put(id.toString(), new Vector3f(DynamicsWorld.DEFAULT_GRAVITY));
         customizedBuoyancies.put(id.toString(), new Vector3f(DynamicsWorld.DEFAULT_BUOYANCY));
         return true;
      } else {
         return false;
      }
   }

   public static boolean hasItemPhysics() {
      return itemPhysics;
   }

   public static boolean cudaLiquids() {
      return cudaLiquids && StarterClient.cudaManager != null;
   }

   public static boolean cudaSmoke() {
      return cudaSmoke && !areVolumetricSmokePhysicsEnabled() && StarterClient.cudaManager != null;
   }

   public static boolean useCuda() {
      return StarterClient.cudaAvailable && (cudaLiquids || cudaSmoke && !areVolumetricSmokePhysicsEnabled());
   }

   private static JsonObject createConfig() {
      JsonObject config = new JsonObject();
      config.add("maxPhysicsObjects", new JsonPrimitive(maxPhysicsObjects));
      config.add("particleLifetimeVines", new JsonPrimitive(particleLifetimeVines));
      config.add("particleLifetimeItems", new JsonPrimitive(particleLifetimeItems));
      config.add("particleLifetimeParticles", new JsonPrimitive(particleLifetimeParticles));
      config.add("particleLifetimeLiquids", new JsonPrimitive(particleLifetimeLiquids));
      config.add("particleLifetimeVarianceVines", new JsonPrimitive(particleLifetimeVarianceVines));
      config.add("particleLifetimeVarianceItems", new JsonPrimitive(particleLifetimeVarianceItems));
      config.add("particleLifetimeVarianceParticles", new JsonPrimitive(particleLifetimeVarianceParticles));
      config.add("particleLifetimeVarianceLiquids", new JsonPrimitive(particleLifetimeVarianceLiquids));
      config.add("particleLifetimeSmoke", new JsonPrimitive(particleLifetimeSmoke));
      config.add("particleLifetimeSmokeCuda", new JsonPrimitive(particleLifetimeSmokeCuda));
      config.add("particleLifetimeSmokeVolumetric", new JsonPrimitive(particleLifetimeSmokeVolumetric));
      config.add("particleLifetimeVarianceSmoke", new JsonPrimitive(particleLifetimeVarianceSmoke));
      config.add("particleLifetimeVarianceSmokeCuda", new JsonPrimitive(particleLifetimeVarianceSmokeCuda));
      config.add("particleLifetimeVarianceSmokeVolumetric", new JsonPrimitive(particleLifetimeVarianceSmokeVolumetric));
      config.add("particleDespawnTimeSmoke", new JsonPrimitive(particleDespawnTimeSmoke));
      config.add("particleDespawnTimeSmokeCuda", new JsonPrimitive(particleDespawnTimeSmokeCuda));
      config.add("particleDespawnTimeSmokeVolumetric", new JsonPrimitive(particleDespawnTimeSmokeVolumetric));
      config.add("particleDespawnTimeVarianceSmoke", new JsonPrimitive(particleDespawnTimeVarianceSmoke));
      config.add("particleDespawnTimeVarianceSmokeCuda", new JsonPrimitive(particleDespawnTimeVarianceSmokeCuda));
      config.add("particleDespawnTimeVarianceSmokeVolumetric", new JsonPrimitive(particleDespawnTimeVarianceSmokeVolumetric));
      config.add("cpuThreads", new JsonPrimitive(cpuThreads));
      config.add("itemPhysics", new JsonPrimitive(itemPhysics));
      config.add("minecraftBlockBreakParticles", new JsonPrimitive(minecraftBlockBreakParticles));
      config.add("vinePhysics", new JsonPrimitive(vinePhysics));
      config.add("capePhysics", new JsonPrimitive(capePhysics));
      config.add("fishingRodPhysics", new JsonPrimitive(fishingRodPhysics));
      config.add("leashPhysics", new JsonPrimitive(leashPhysics));
      config.add("bannerPhysics", new JsonPrimitive(bannerPhysics));
      config.add("clothSmoothShading", new JsonPrimitive(clothSmoothShading));
      config.add("showUpdateNotifications", new JsonPrimitive(showUpdateNotifications));
      config.add("clothThreads", new JsonPrimitive(clothThreads));
      config.add("leashLength", new JsonPrimitive(leashLength));
      config.add("fishingLineLength", new JsonPrimitive(fishingLineLength));
      config.add("pvpServerCompatibility", new JsonPrimitive(pvpServerCompatibility));
      config.add("snowballModel", new JsonPrimitive(snowballModel));
      config.add("snowballImpact", new JsonPrimitive(snowballImpact));
      config.add("snowballShade", new JsonPrimitive(snowballShade));
      config.add("enderpearlModel", new JsonPrimitive(enderpearlModel));
      config.add("enderpearlImpact", new JsonPrimitive(enderpearlImpact));
      config.add("enderpearlShade", new JsonPrimitive(enderpearlShade));
      config.add("eggModel", new JsonPrimitive(eggModel));
      config.add("eggImpact", new JsonPrimitive(eggImpact));
      config.add("eggShade", new JsonPrimitive(eggShade));
      config.add("crackPhysicsParticles", new JsonPrimitive(crackPhysicsParticles));
      config.add("liquidPhysics", new JsonPrimitive(liquidPhysics));
      config.add("liquidThreads", new JsonPrimitive(liquidThreads));
      config.add("bannerPhysicsRange", new JsonPrimitive(bannerPhysicsRange));
      config.add("soundVolume", new JsonPrimitive(impactVolume));
      config.add("blockPhysicsRange", new JsonPrimitive(blockPhysicsRange));
      config.add("vineRange", new JsonPrimitive(vineRange));
      config.add("snowPhysics", new JsonPrimitive(snowPhysics));
      config.add("snowTracks", new JsonPrimitive(snowTracks));
      config.add("snowTrackEntities", new JsonPrimitive(snowTrackEntities));
      config.add("snowTrackDistance", new JsonPrimitive(snowTrackDistance));
      config.add("snowThickness", new JsonPrimitive(snowThickness));
      config.add("grassSnowy", new JsonPrimitive(grassSnowy));
      config.add("snowType", new JsonPrimitive(snowType));
      config.add("snowSmoothShading", new JsonPrimitive(snowSmoothShading));
      config.add("snowChunkSize", new JsonPrimitive(snowQuality));
      config.add("blockSettings", AdjustableUtil.writeObject(new JsonObject(), blockSetting));
      config.add("mobSettings", AdjustableUtil.writeObject(new JsonObject(), mobSetting));
      config.add("sprintingPhysicsParticles", new JsonPrimitive(sprintingPhysicsParticles));
      config.add("eatingPhysicsParticles", new JsonPrimitive(eatingPhysicsParticles));
      config.add("serverBlockPhysicsParticles", new JsonPrimitive(serverBlockPhysicsParticles));
      config.add("smokePhysics", new JsonPrimitive(smokePhysics));
      config.add("smokeVolumetricPhysics", new JsonPrimitive(smokeVolumetricPhysics));
      config.add("smokeParticleLimit", new JsonPrimitive(smokeParticleLimit));
      config.add("verificationCode", new JsonPrimitive(verificationCode));
      config.add("smokeColorRed", new JsonPrimitive(smokeColorRed));
      config.add("smokeColorGreen", new JsonPrimitive(smokeColorGreen));
      config.add("smokeColorBlue", new JsonPrimitive(smokeColorBlue));
      config.add("smokeDenseColorRed", new JsonPrimitive(smokeDenseColorRed));
      config.add("smokeDenseColorGreen", new JsonPrimitive(smokeDenseColorGreen));
      config.add("smokeDenseColorBlue", new JsonPrimitive(smokeDenseColorBlue));
      config.add("smokeVolumeColorRed", new JsonPrimitive(smokeVolumeColorRed));
      config.add("smokeVolumeColorGreen", new JsonPrimitive(smokeVolumeColorGreen));
      config.add("smokeVolumeColorBlue", new JsonPrimitive(smokeVolumeColorBlue));
      config.add("smokeVolumeFire", new JsonPrimitive(smokeVolumeFire));
      config.add("smokeVolumeRadius", new JsonPrimitive(smokeVolumeRadius));
      config.add("smokeDensity", new JsonPrimitive(smokeDensity));
      config.add("smokePhysicsRange", new JsonPrimitive(smokePhysicsRange));
      config.add("smokeBlaze", new JsonPrimitive(smokeBlaze));
      config.add("smokeCampfire", new JsonPrimitive(smokeCampfire));
      config.add("smokeFire", new JsonPrimitive(smokeFire));
      config.add("smokeOther", new JsonPrimitive(smokeOther));
      config.add("maxLoadedDynamicBlocks", new JsonPrimitive(maxLoadedDynamicBlocks));
      config.add("windPhysics", new JsonPrimitive(windPhysics));
      config.add("weatherParticles", new JsonPrimitive(weatherParticles));
      config.add("weatherClearStrength", new JsonPrimitive(weatherClearStrength));
      config.add("weatherRainStrength", new JsonPrimitive(weatherRainStrength));
      config.add("weatherThunderStrength", new JsonPrimitive(weatherThunderStrength));
      config.add("windVolume", new JsonPrimitive(windVolume));
      config.add("weatherRainParticleAmount", new JsonPrimitive(weatherRainParticleAmount));
      config.add("weatherThunderParticleAmount", new JsonPrimitive(weatherThunderParticleAmount));
      config.add("oceanPhysics", new JsonPrimitive(oceanPhysics));
      config.add("oceanAdjustHitbox", new JsonPrimitive(oceanAdjustHitbox));
      config.add("oceanDetail", new JsonPrimitive(oceanDetail));
      config.add("oceanWaveHeightMultiplier", new JsonPrimitive(oceanWaveHeightMultiplier));
      config.add("oceanBlockRange", new JsonPrimitive(oceanBlockRange));
      config.add("oceanWeatherClear", new JsonPrimitive(oceanWeatherClear));
      config.add("oceanWeatherRain", new JsonPrimitive(oceanWeatherRain));
      config.add("oceanWeatherThunder", new JsonPrimitive(oceanWeatherThunder));
      config.add("oceanBaseSpeed", new JsonPrimitive(oceanBaseSpeed));
      config.add("oceanHorizontalWaveScale", new JsonPrimitive(oceanHorizontalWaveScale));
      config.add("oceanParticles", new JsonPrimitive(oceanParticles));
      config.add("oceanParticleAlpha", new JsonPrimitive(oceanParticleAlpha));
      config.add("oceanStickyEntities", new JsonPrimitive(oceanStickyEntities));
      config.add("oceanSplashVolumeNew", new JsonPrimitive(oceanSplashVolume));
      config.add("snowLOD", new JsonPrimitive(snowLOD));
      config.add("particleRainOpacity", new JsonPrimitive(particleRainOpacity));
      config.add("particleSnowOpacity", new JsonPrimitive(particleSnowOpacity));
      config.add("particleDustOpacity", new JsonPrimitive(particleDustOpacity));
      config.add("itemRotationSpeed", new JsonPrimitive(itemRotationSpeed));
      config.add("jointBreakForce", new JsonPrimitive(jointBreakForce));
      config.add("jointBlood", new JsonPrimitive(jointBlood));
      config.add("mobRagdollLimit", new JsonPrimitive(mobRagdollLimit));
      config.add("clothEntityRange", new JsonPrimitive(clothEntityRange));
      config.add("clothForceArmor", new JsonPrimitive(clothForceArmor));
      config.add("oceanPuddles", new JsonPrimitive(oceanRipples));
      config.add("oceanFoamAmount", new JsonPrimitive(oceanFoamAmount));
      config.add("oceanFoamOpacity", new JsonPrimitive(oceanFoamOpacity));
      config.add("cudaLiquids", new JsonPrimitive(cudaLiquids));
      config.add("cudaLiquidsParticleSize", new JsonPrimitive(cudaLiquidsParticleSize));
      config.add("playbackSpeed", new JsonPrimitive(playbackSpeed));
      config.add("oceanRainPuddleAmount", new JsonPrimitive(oceanRainPuddleAmount));
      config.add("itemBreakPhysics", new JsonPrimitive(itemBreakPhysics));
      config.add("cudaSmoke", new JsonPrimitive(cudaSmoke));
      config.add("smokeParticleLimitCuda", new JsonPrimitive(smokeParticleLimitCuda));
      config.add("liquidCudaMaxParticles", new JsonPrimitive(liquidCudaMaxParticles));
      config.add("liquidCudaAmount", new JsonPrimitive(liquidCudaAmount));
      config.add("liquidParticleSize", new JsonPrimitive(liquidParticleSize));
      config.add("liquidAmount", new JsonPrimitive(liquidAmount));
      config.add("liquidMaxParticles", new JsonPrimitive(liquidMaxParticles));
      config.add("particleLifetimeLiquidsCuda", new JsonPrimitive(particleLifetimeLiquidsCuda));
      config.add("particleLifetimeVarianceLiquidsCuda", new JsonPrimitive(particleLifetimeVarianceLiquidsCuda));
      config.add("smokeVolumeShadowStrength", new JsonPrimitive(smokeVolumeShadowStrength));
      config.add("smokeVolumeQuality", new JsonPrimitive(smokeVolumeQuality));
      config.add("smokeEmberParticlesAmount", new JsonPrimitive(smokeEmberParticlesAmount));
      config.add("smokeVolumeShadow", new JsonPrimitive(smokeVolumeShadow));
      add(config, customizedGravities, "customizedGravities");
      add(config, customizedBuoyancies, "customizedBuoyancies");
      return config;
   }

   private static void add(JsonObject config, Map<String, Vector3f> map, String name) {
      JsonArray array = new JsonArray();

      for (Entry<String, Vector3f> entry : map.entrySet()) {
         array.add(entry.getKey());
         Vector3f val = entry.getValue();
         array.add(val.x);
         array.add(val.y);
         array.add(val.z);
      }

      config.add(name, array);
   }

   public static void resetOptions() {
      itemPhysics = true;
      maxPhysicsObjects = 10000;
      cpuThreads = Math.max(1, Runtime.getRuntime().availableProcessors() / 4);
      minecraftBlockBreakParticles = false;
      vinePhysics = true;
      save();
   }

   public static void resetGravities() {
      for (Entry<String, Vector3f> entry : customizedGravities.entrySet()) {
         customizedGravities.put(entry.getKey(), new Vector3f(DynamicsWorld.DEFAULT_GRAVITY));
      }

      for (Entry<String, Vector3f> entry : customizedBuoyancies.entrySet()) {
         customizedBuoyancies.put(entry.getKey(), new Vector3f(DynamicsWorld.DEFAULT_BUOYANCY));
      }

      gravityChanged = true;
      save();
   }

   public static void resetWeatherSettings() {
      windPhysics = true;
      weatherParticles = true;
      weatherClearStrength = 0.1F;
      weatherRainStrength = 1.0F;
      weatherThunderStrength = 1.4F;
      weatherRainParticleAmount = 5;
      weatherThunderParticleAmount = 2;
      particleRainOpacity = 1.0F;
      particleSnowOpacity = 1.0F;
      particleDustOpacity = 1.0F;
      save();
   }

   public static void resetSmokeSettings() {
      smokePhysics = true;
      smokePhysicsRange = 100.0;
      particleLifetimeSmoke = 60.0;
      particleLifetimeSmokeCuda = 60.0;
      particleLifetimeSmokeVolumetric = 60.0;
      particleLifetimeVarianceSmoke = 15.0;
      particleLifetimeVarianceSmokeCuda = 15.0;
      particleLifetimeVarianceSmokeVolumetric = 15.0;
      particleDespawnTimeSmoke = 3.0;
      particleDespawnTimeSmokeCuda = 3.0;
      particleDespawnTimeSmokeVolumetric = 0.0;
      particleDespawnTimeVarianceSmoke = 12.0;
      particleDespawnTimeVarianceSmokeCuda = 3.0;
      particleDespawnTimeVarianceSmokeVolumetric = 2.5;
      smokeColorRed = 0.56F;
      smokeColorGreen = 0.56F;
      smokeColorBlue = 0.56F;
      smokeDenseColorRed = 0.364F;
      smokeDenseColorGreen = 0.364F;
      smokeDenseColorBlue = 0.364F;
      smokeDensity = 0.9F;
      smokeParticleLimit = 6000;
      smokeParticleLimitCuda = 50000;
      smokeBlaze = 0.0F;
      smokeCampfire = 0.3F;
      smokeFire = 0.3F;
      smokeOther = 1.0F;
      smokeVolumeColorRed = 0.2F;
      smokeVolumeColorGreen = 0.175F;
      smokeVolumeColorBlue = 0.15F;
      smokeVolumeFire = 0.25F;
      smokeVolumeRadius = 1.0F;
      smokeVolumeShadowStrength = 0.5F;
      smokeVolumeQuality = 1;
      smokeVolumeShadow = true;
      smokeEmberParticlesAmount = 1.0F;
      save();
   }

   public static void resetLiquidSettings() {
      liquidPhysics = true;
      particleLifetimeLiquids = 6.0;
      particleLifetimeVarianceLiquids = 3.0;
      liquidThreads = 2;
      cudaLiquidsParticleSize = 0.1F;
      liquidCudaMaxParticles = 30000;
      liquidCudaAmount = 4;
      liquidParticleSize = 0.15F;
      liquidAmount = 2;
      liquidMaxParticles = 3000;
      particleLifetimeLiquidsCuda = 6.0;
      particleLifetimeVarianceLiquidsCuda = 3.0;
      liquidCudaMaxParticles = 10000;
      save();
   }

   public static void resetOceanSettings() {
      oceanPhysics = true;
      oceanDetail = 1.0F;
      oceanAdjustHitbox = false;
      oceanWaveHeightMultiplier = 1.0F;
      oceanBlockRange = 32;
      oceanWeatherClear = 0.0F;
      oceanWeatherRain = 0.75F;
      oceanWeatherThunder = 0.25F;
      oceanBaseSpeed = 1.0F;
      oceanHorizontalWaveScale = 1.0F;
      oceanParticles = false;
      oceanParticleAlpha = 0.5F;
      oceanStickyEntities = false;
      oceanRipples = true;
      oceanFoamAmount = 0.8F;
      oceanFoamOpacity = 0.5F;
      oceanRainPuddleAmount = 0.5F;
      save();
   }

   public static void resetSnowSettings() {
      snowPhysics = true;
      snowTracks = true;
      grassSnowy = true;
      snowLOD = 1.0F;
      snowTrackDistance = 48.0;
      snowTrackEntities = 6;
      snowType = 0;
      snowSmoothShading = true;
      snowQuality = 0;
      snowThickness = 0.0F;
      save();
   }

   public static void toggleSettings() {
      if (!stored) {
         stored_vinePhysics = vinePhysics;
         stored_capePhysics = capePhysics;
         stored_itemPhysics = itemPhysics;
         stored_fishingRodPhysics = fishingRodPhysics;
         stored_leashPhysics = leashPhysics;
         stored_bannerPhysics = bannerPhysics;
         stored_liquidPhysics = liquidPhysics;
         stored_snowPhysics = snowPhysics;
         stored_smokePhysics = smokePhysics;
         stored_smokeVolumetricPhysics = smokeVolumetricPhysics;
         stored_guiPhysics = guiPhysics;
         stored_windPhysics = windPhysics;
         stored_weatherParticles = weatherParticles;
         stored_oceanPhysics = oceanPhysics;
         stored_crackPhysicsParticles = crackPhysicsParticles;
         stored_sprintingPhysicsParticles = sprintingPhysicsParticles;
         stored_eatingPhysicsParticles = eatingPhysicsParticles;
         stored_serverBlockPhysicsParticles = serverBlockPhysicsParticles;
         stored_minecraftBlockBreakParticles = minecraftBlockBreakParticles;
         stored_snowballModel = snowballModel;
         stored_enderpearlModel = enderpearlModel;
         stored_eggModel = eggModel;
         stored_itemBreakPhysics = itemBreakPhysics;
         vinePhysics = false;
         capePhysics = false;
         itemPhysics = false;
         fishingRodPhysics = false;
         leashPhysics = false;
         bannerPhysics = false;
         liquidPhysics = false;
         snowPhysics = false;
         smokePhysics = false;
         smokeVolumetricPhysics = false;
         windPhysics = false;
         weatherParticles = false;
         oceanPhysics = false;
         crackPhysicsParticles = false;
         sprintingPhysicsParticles = false;
         eatingPhysicsParticles = false;
         serverBlockPhysicsParticles = false;
         minecraftBlockBreakParticles = false;
         snowballModel = 2;
         enderpearlModel = 2;
         eggModel = 2;
         blockPhysics = false;
         mobPhysics = false;
         itemBreakPhysics = false;
      } else {
         vinePhysics = stored_vinePhysics;
         capePhysics = stored_capePhysics;
         itemPhysics = stored_itemPhysics;
         fishingRodPhysics = stored_fishingRodPhysics;
         leashPhysics = stored_leashPhysics;
         bannerPhysics = stored_bannerPhysics;
         liquidPhysics = stored_liquidPhysics;
         snowPhysics = stored_snowPhysics;
         smokePhysics = stored_smokePhysics;
         smokeVolumetricPhysics = stored_smokeVolumetricPhysics;
         guiPhysics = stored_guiPhysics;
         windPhysics = stored_windPhysics;
         weatherParticles = stored_weatherParticles;
         oceanPhysics = stored_oceanPhysics;
         crackPhysicsParticles = stored_crackPhysicsParticles;
         sprintingPhysicsParticles = stored_sprintingPhysicsParticles;
         eatingPhysicsParticles = stored_eatingPhysicsParticles;
         serverBlockPhysicsParticles = stored_serverBlockPhysicsParticles;
         minecraftBlockBreakParticles = stored_minecraftBlockBreakParticles;
         snowballModel = stored_snowballModel;
         enderpearlModel = stored_enderpearlModel;
         eggModel = stored_eggModel;
         itemBreakPhysics = stored_itemBreakPhysics;
         blockPhysics = true;
         mobPhysics = true;
      }

      stored = !stored;
      ObjectIterator var0 = PhysicsMod.getInstances().values().iterator();

      while (var0.hasNext()) {
         PhysicsMod mod = (PhysicsMod)var0.next();
         mod.getPhysicsWorld().destroy();
      }

      PhysicsMod.getInstances().clear();
      Minecraft.getInstance().levelExtractor.allChanged();
   }

   public static void save() {
      File directory = new File("config/physicsmod");
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File configFile = new File("config/physicsmod/physics_client_config.json");
      if (configFile.exists()) {
         configFile.delete();
      }

      JsonObject config = createConfig();

      try {
         configFile.createNewFile();

         try (Writer writer = new FileWriter(configFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(config, writer);
         }
      } catch (IOException var8) {
         var8.printStackTrace();
      }
   }

   static {
      reload();
   }
}
