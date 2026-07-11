package net.diebuddies.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.mobs.MobSetting;
import net.diebuddies.physics.vines.AdjustableUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public final class ConfigMobs {
   private static final String DIR = "config/physicsmod";
   public static final MobSetting PARENT_MOB_SETTING = new MobSetting();
   private static final String CONFIG = "physics_mobs_client_config.json";
   public static Map<String, MobSetting> customizedMobs = new Object2ObjectOpenHashMap();

   public static void init() {
   }

   public static MobSetting getMobSetting(EntityType<?> type) {
      return getMobSetting(EntityType.getKey(type).toString());
   }

   public static MobSetting getMobSetting(String type) {
      MobSetting val = customizedMobs.get(type);
      if (val == null) {
         val = PARENT_MOB_SETTING;
      }

      return val;
   }

   public static MobSetting getMobSetting(Entity entity) {
      return getMobSetting(entity.getType());
   }

   private static JsonObject createConfig() {
      JsonObject config = new JsonObject();
      JsonArray array = new JsonArray();

      for (EntityType type : PhysicsMod.renderers.keySet()) {
         String id = EntityType.getKey(type).toString();
         MobSetting setting = customizedMobs.get(id);
         if (setting == null) {
            setting = PARENT_MOB_SETTING;
         }

         JsonObject obj = new JsonObject();
         obj.add("identifier", new JsonPrimitive(id));
         AdjustableUtil.writeObject(obj, setting);
         array.add(obj);
      }

      config.add("customizedMobs", array);
      return config;
   }

   public static void resetMobs() {
      for (Entry<String, MobSetting> entry : customizedMobs.entrySet()) {
         customizedMobs.put(entry.getKey(), PARENT_MOB_SETTING);
      }

      save();
   }

   public static void save() {
      File directory = new File("config/physicsmod");
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File configFile = new File("config/physicsmod/physics_mobs_client_config.json");
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
      JsonObject config = createConfig();
      File directory = new File("config/physicsmod");
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File configFile = new File("config/physicsmod/physics_mobs_client_config.json");
      if (!configFile.exists()) {
         try {
            configFile.createNewFile();

            try (Writer writer = new FileWriter(configFile)) {
               Gson gson = new GsonBuilder().setPrettyPrinting().create();
               gson.toJson(config, writer);
            }
         } catch (IOException var11) {
            var11.printStackTrace();
         }
      } else {
         Gson gson = new Gson();

         try {
            config = (JsonObject)gson.fromJson(new FileReader(configFile), JsonObject.class);
         } catch (JsonIOException | FileNotFoundException | JsonSyntaxException var9) {
            var9.printStackTrace();
         }
      }

      try {
         JsonArray array = config.get("customizedMobs").getAsJsonArray();

         for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            String identifier = obj.get("identifier").getAsString();
            MobSetting setting = (MobSetting)AdjustableUtil.readObject(MobSetting.class, obj);
            customizedMobs.put(identifier, setting);
         }
      } catch (Exception var12) {
      }
   }
}
