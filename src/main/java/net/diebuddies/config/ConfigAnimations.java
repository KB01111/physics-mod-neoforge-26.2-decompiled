package net.diebuddies.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map.Entry;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.animation.AnimationType;
import net.diebuddies.physics.animation.CurveType;
import net.diebuddies.physics.vines.AdjustableUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;

public final class ConfigAnimations {
   private static final String DIR = "config/physicsmod";
   public static Animation DEFAULT_ANIMATION;
   private static final String CONFIG = "physics_animations_client_config.json";
   public static Long2ObjectMap<Animation> animations = new Long2ObjectOpenHashMap();

   public static void loadDefaultConfigSettings() {
      DEFAULT_ANIMATION = new Animation("default", CurveType.Linear, 0.5F);
      animations.put(0L, DEFAULT_ANIMATION);
      Animation vanish = new Animation("vanish", CurveType.Ease_out, 0.5F);
      vanish.despawnType = AnimationType.Vanish;
      animations.put(1L, vanish);
      Animation bounce = new Animation("bounce", CurveType.Bounce, 0.6F);
      bounce.despawnType = AnimationType.Shrink;
      animations.put(2L, bounce);
      Animation explosion = new Animation("explosion", CurveType.Ease_in, 0.5F);
      explosion.despawnType = AnimationType.Shrink_and_Vanish;
      explosion.addParticleSpawn(ParticleTypes.LAVA, 4, 0.5, 1.0, 0.0, 0.0, 0.0, 0.1, SoundEvents.LAVA_EXTINGUISH);
      explosion.addParticleSpawn(ParticleTypes.POOF, 7, 0.5, 1.0, 0.0, 0.0, 0.0, 0.1, null);
      animations.put(3L, explosion);
      Animation poof = new Animation("poof", CurveType.Linear, 0.0F);
      poof.despawnType = AnimationType.Shrink;
      poof.addParticleSpawn(ParticleTypes.POOF, 6, 0.8, 1.0, 0.0, 0.0, 0.0, 0.2, SoundEvents.FOX_SNIFF);
      animations.put(4L, poof);
      Animation portal = new Animation("portal", CurveType.Linear, 0.0F);
      portal.despawnType = AnimationType.Shrink;
      portal.addParticleSpawn(ParticleTypes.REVERSE_PORTAL, 16, 0.8, 1.0, 0.0, 0.0, 0.0, 0.1, SoundEvents.PORTAL_AMBIENT);
      animations.put(5L, portal);
      Animation splash = new Animation("splash", CurveType.Linear, 0.0F);
      splash.despawnType = AnimationType.Shrink;
      splash.addParticleSpawn(ParticleTypes.SPLASH, 16, 0.7, 1.0, 0.0, 0.0, 0.0, 0.1, SoundEvents.DOLPHIN_SPLASH);
      animations.put(6L, splash);
   }

   public static void reload() {
      loadDefaultConfigSettings();
      JsonObject config = createConfig();
      File directory = new File("config/physicsmod");
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File configFile = new File("config/physicsmod/physics_animations_client_config.json");
      if (!configFile.exists()) {
         try {
            configFile.createNewFile();

            try (Writer writer = new FileWriter(configFile)) {
               Gson gson = new GsonBuilder().setPrettyPrinting().create();
               gson.toJson(config, writer);
            }
         } catch (IOException var12) {
            var12.printStackTrace();
         }
      } else {
         Gson gson = new Gson();

         try {
            config = (JsonObject)gson.fromJson(new FileReader(configFile), JsonObject.class);
         } catch (JsonIOException | FileNotFoundException | JsonSyntaxException var10) {
            var10.printStackTrace();
         }
      }

      try {
         animations.clear();
         JsonArray array = config.get("customizedAnimations").getAsJsonArray();

         for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            long identifier = obj.get("identifier").getAsLong();
            Animation animation = (Animation)AdjustableUtil.readObject(Animation.class, obj);
            animations.put(identifier, animation);
         }
      } catch (Exception var13) {
         loadDefaultConfigSettings();
         save();
      }
   }

   public static void init() {
   }

   private static JsonObject createConfig() {
      JsonObject config = new JsonObject();
      JsonArray array = new JsonArray();
      ObjectIterator var2 = animations.entrySet().iterator();

      while (var2.hasNext()) {
         Entry<Long, Animation> entry = (Entry<Long, Animation>)var2.next();
         Long id = entry.getKey();
         Animation animation = entry.getValue();
         JsonObject obj = new JsonObject();
         obj.add("identifier", new JsonPrimitive(id));
         AdjustableUtil.writeObject(obj, animation);
         array.add(obj);
      }

      config.add("customizedAnimations", array);
      return config;
   }

   public static void save() {
      File directory = new File("config/physicsmod");
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File configFile = new File("config/physicsmod/physics_animations_client_config.json");
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
