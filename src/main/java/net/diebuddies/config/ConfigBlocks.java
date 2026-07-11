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
import net.diebuddies.physics.settings.blocks.BlockSetting;
import net.diebuddies.physics.vines.AdjustableUtil;
import net.minecraft.world.level.block.Block;

public final class ConfigBlocks {
   private static final String DIR = "config/physicsmod";
   public static final BlockSetting PARENT_BLOCK_SETTING = new BlockSetting();
   private static final String CONFIG = "physics_blocks_client_config.json";
   public static Map<String, BlockSetting> customizedBlocks = new Object2ObjectOpenHashMap();

   public static void reload() {
      JsonObject config = createConfig();
      File directory = new File("config/physicsmod");
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File configFile = new File("config/physicsmod/physics_blocks_client_config.json");
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
         JsonArray array = config.get("customizedBlocks").getAsJsonArray();

         for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            String identifier = obj.get("identifier").getAsString();
            BlockSetting setting = (BlockSetting)AdjustableUtil.readObject(BlockSetting.class, obj);
            customizedBlocks.put(identifier, setting);
         }
      } catch (Exception var12) {
      }
   }

   public static void init() {
   }

   public static BlockSetting getBlockSetting(Block block) {
      BlockSetting val = customizedBlocks.get(PhysicsMod.registeredBlocks.get(block));
      if (val == null) {
         val = PARENT_BLOCK_SETTING;
      }

      return val;
   }

   private static JsonObject createConfig() {
      JsonObject config = new JsonObject();
      JsonArray array = new JsonArray();

      for (String id : PhysicsMod.registeredBlocks.values()) {
         BlockSetting setting = customizedBlocks.get(id);
         if (setting == null) {
            setting = PARENT_BLOCK_SETTING;
         }

         JsonObject obj = new JsonObject();
         obj.add("identifier", new JsonPrimitive(id));
         AdjustableUtil.writeObject(obj, setting);
         array.add(obj);
      }

      config.add("customizedBlocks", array);
      return config;
   }

   public static void resetBlocks() {
      for (Entry<String, BlockSetting> entry : customizedBlocks.entrySet()) {
         customizedBlocks.put(entry.getKey(), PARENT_BLOCK_SETTING);
      }

      save();
   }

   public static void save() {
      File directory = new File("config/physicsmod");
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File configFile = new File("config/physicsmod/physics_blocks_client_config.json");
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
