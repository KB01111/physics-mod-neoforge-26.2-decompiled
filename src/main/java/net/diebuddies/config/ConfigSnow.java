package net.diebuddies.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.vines.VineHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public final class ConfigSnow {
   private static final String DIR = "config/physicsmod";
   private static final String CONFIG = "physics_snow_client_config.json";
   public static Set<Block> activeBlocks = new ReferenceOpenHashSet();

   public static void loadDefaultConfigSettings() {
      activeBlocks.clear();
      activeBlocks.add(Blocks.SNOW);
      activeBlocks.add(Blocks.SNOW_BLOCK);
      activeBlocks.add(Blocks.POWDER_SNOW);
   }

   public static void init() {
   }

   private static JsonObject createConfig() {
      JsonObject config = new JsonObject();
      JsonArray array = new JsonArray();

      for (Block block : activeBlocks) {
         String id = PhysicsMod.registeredBlocks.get(block);
         if (id != null) {
            JsonObject obj = new JsonObject();
            obj.add("blockID", new JsonPrimitive(id));
            array.add(obj);
         }
      }

      config.add("customizedSnowBlocks", array);
      return config;
   }

   public static void resetSnow() {
      loadDefaultConfigSettings();
      save();
   }

   public static void save() {
      File directory = new File("config/physicsmod");
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File configFile = new File("config/physicsmod/physics_snow_client_config.json");
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
      loadDefaultConfigSettings();
      JsonObject config = createConfig();
      File directory = new File("config/physicsmod");
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File configFile = new File("config/physicsmod/physics_snow_client_config.json");
      if (!configFile.exists()) {
         try {
            configFile.createNewFile();

            try (Writer writer = new FileWriter(configFile)) {
               Gson gson = new GsonBuilder().setPrettyPrinting().create();
               gson.toJson(config, writer);
            }
         } catch (IOException var10) {
            var10.printStackTrace();
         }
      } else {
         Gson gson = new Gson();

         try {
            config = (JsonObject)gson.fromJson(new FileReader(configFile), JsonObject.class);
         } catch (JsonIOException | FileNotFoundException | JsonSyntaxException var8) {
            var8.printStackTrace();
         }
      }

      try {
         activeBlocks.clear();
         JsonArray array = config.get("customizedSnowBlocks").getAsJsonArray();

         for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            Block block = PhysicsMod.invRegisteredBlocks.get(obj.get("blockID").getAsString());
            activeBlocks.add(block);
         }
      } catch (Exception var11) {
         loadDefaultConfigSettings();
         save();
      }

      VineHelper.initFromConfigSettings();
   }
}
