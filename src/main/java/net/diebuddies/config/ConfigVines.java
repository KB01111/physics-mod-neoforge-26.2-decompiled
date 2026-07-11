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
import net.diebuddies.physics.vines.AdjustableUtil;
import net.diebuddies.physics.vines.DoorSetting;
import net.diebuddies.physics.vines.DynamicSetting;
import net.diebuddies.physics.vines.HangingSignSetting;
import net.diebuddies.physics.vines.TrapdoorSetting;
import net.diebuddies.physics.vines.VineHelper;
import net.diebuddies.physics.vines.VineSetting;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.joml.Vector3f;

public final class ConfigVines {
   private static final String DIR = "config/physicsmod";
   private static final String CONFIG = "physics_vines_client_config.json";
   public static Map<Block, DynamicSetting> configSettings = new Object2ObjectOpenHashMap();

   public static void loadDefaultConfigSettings() {
      configSettings.clear();
      configSettings.put(Blocks.TWISTING_VINES, new VineSetting(true, true, false, new Vector3f(1.0F), 2400.0F, 10.0F, true, Blocks.TWISTING_VINES_PLANT));
      configSettings.put(Blocks.KELP, new VineSetting(true, false, false, new Vector3f(1.0F), 45.0F, 45.0F, true, Blocks.KELP_PLANT));
      configSettings.put(Blocks.WEEPING_VINES, new VineSetting(false, false, false, new Vector3f(1.0F), 45.0F, 45.0F, true, Blocks.WEEPING_VINES_PLANT));
      configSettings.put(Blocks.CAVE_VINES, new VineSetting(false, false, false, new Vector3f(1.0F), 45.0F, 45.0F, true, Blocks.CAVE_VINES_PLANT));
      configSettings.put(Blocks.SUGAR_CANE, new VineSetting(true, true, false, new Vector3f(1.0F), 2300.0F, 90.0F, true, null));
      configSettings.put(Blocks.VINE, new VineSetting(false, false, true, new Vector3f(1.0F), 45.0F, 45.0F, true, null));
      configSettings.put(Blocks.IRON_CHAIN, new VineSetting(false, false, true, new Vector3f(1.5F, 1.0F, 1.5F), 60.0F, 40.0F, true, Blocks.LANTERN));
      configSettings.put(Blocks.ROSE_BUSH, new VineSetting(true, true, false, new Vector3f(1.0F), 110.0F, 48.0F, true, null));
      configSettings.put(Blocks.TALL_GRASS, new VineSetting(true, true, false, new Vector3f(1.0F), 110.0F, 48.0F, true, null));
      configSettings.put(Blocks.LARGE_FERN, new VineSetting(true, true, false, new Vector3f(1.0F), 110.0F, 48.0F, true, null));
      configSettings.put(Blocks.PEONY, new VineSetting(true, true, false, new Vector3f(1.0F), 110.0F, 48.0F, true, null));
      configSettings.put(Blocks.LILAC, new VineSetting(true, true, false, new Vector3f(1.0F), 110.0F, 48.0F, true, null));
      configSettings.put(Blocks.SUNFLOWER, new VineSetting(true, true, false, new Vector3f(1.0F), 110.0F, 48.0F, true, null));
      configSettings.put(Blocks.TALL_SEAGRASS, new VineSetting(true, false, false, new Vector3f(1.0F), 110.0F, 48.0F, true, null));
      configSettings.put(Blocks.PALE_HANGING_MOSS, new VineSetting(false, false, false, new Vector3f(1.0F), 45.0F, 45.0F, true, null));
      float scale = 0.85F;
      float stiffness = 1580.0F;
      float damping = 8.0F;
      configSettings.put(Blocks.OAK_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.ACACIA_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.BIRCH_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.DARK_OAK_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.IRON_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.JUNGLE_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.SPRUCE_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.WARPED_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.CRIMSON_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.BAMBOO_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.MANGROVE_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.CHERRY_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_TRAPDOOR.weathering().unaffected(), new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_TRAPDOOR.weathering().exposed(), new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_TRAPDOOR.weathering().weathered(), new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_TRAPDOOR.weathering().oxidized(), new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_TRAPDOOR.waxed().unaffected(), new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_TRAPDOOR.waxed().exposed(), new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_TRAPDOOR.waxed().weathered(), new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_TRAPDOOR.waxed().oxidized(), new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.PALE_OAK_TRAPDOOR, new TrapdoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.OAK_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.ACACIA_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.BIRCH_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.DARK_OAK_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.IRON_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.JUNGLE_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.SPRUCE_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.WARPED_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.CRIMSON_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.BAMBOO_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.MANGROVE_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.CHERRY_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_DOOR.weathering().unaffected(), new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_DOOR.weathering().exposed(), new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_DOOR.weathering().weathered(), new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_DOOR.weathering().oxidized(), new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_DOOR.waxed().unaffected(), new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_DOOR.waxed().exposed(), new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_DOOR.waxed().weathered(), new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put((Block)Blocks.COPPER_DOOR.waxed().oxidized(), new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.PALE_OAK_DOOR, new DoorSetting(new Vector3f(scale), stiffness, damping, true, true));
      configSettings.put(Blocks.OAK_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9F, 0.9F, 0.9F), 45.0F, 45.0F, true));
      configSettings.put(Blocks.ACACIA_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9F, 0.9F, 0.9F), 45.0F, 45.0F, true));
      configSettings.put(Blocks.BIRCH_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9F, 0.9F, 0.9F), 45.0F, 45.0F, true));
      configSettings.put(Blocks.DARK_OAK_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9F, 0.9F, 0.9F), 45.0F, 45.0F, true));
      configSettings.put(Blocks.JUNGLE_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9F, 0.9F, 0.9F), 45.0F, 45.0F, true));
      configSettings.put(Blocks.SPRUCE_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9F, 0.9F, 0.9F), 45.0F, 45.0F, true));
      configSettings.put(Blocks.WARPED_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9F, 0.9F, 0.9F), 45.0F, 45.0F, true));
      configSettings.put(Blocks.CRIMSON_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9F, 0.9F, 0.9F), 45.0F, 45.0F, true));
      configSettings.put(Blocks.BAMBOO_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9F, 0.9F, 0.9F), 45.0F, 45.0F, true));
      configSettings.put(Blocks.MANGROVE_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9F, 0.9F, 0.9F), 45.0F, 45.0F, true));
      configSettings.put(Blocks.CHERRY_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9F, 0.9F, 0.9F), 45.0F, 45.0F, true));
      configSettings.put(Blocks.PALE_OAK_HANGING_SIGN, new HangingSignSetting(new Vector3f(0.9F, 0.9F, 0.9F), 45.0F, 45.0F, true));
   }

   public static void init() {
   }

   private static JsonObject createConfig() {
      JsonObject config = new JsonObject();
      JsonArray array = new JsonArray();

      for (Entry<Block, DynamicSetting> entry : configSettings.entrySet()) {
         String id = PhysicsMod.registeredBlocks.get(entry.getKey());
         if (id != null) {
            JsonObject obj = new JsonObject();
            obj.add("blockID", new JsonPrimitive(id));
            DynamicSetting setting = entry.getValue();
            AdjustableUtil.writeDynamicSetting(obj, setting);
            array.add(obj);
         }
      }

      config.add("customizedDynamicBlocks", array);
      return config;
   }

   public static void resetVines() {
      loadDefaultConfigSettings();
      VineHelper.initFromConfigSettings();
      save();
   }

   public static void save() {
      File directory = new File("config/physicsmod");
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File configFile = new File("config/physicsmod/physics_vines_client_config.json");
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

      File configFile = new File("config/physicsmod/physics_vines_client_config.json");
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
         configSettings.clear();
         JsonArray array = config.get("customizedDynamicBlocks").getAsJsonArray();

         for (int i = 0; i < array.size(); i++) {
            JsonObject obj = array.get(i).getAsJsonObject();
            Block block = PhysicsMod.invRegisteredBlocks.get(obj.get("blockID").getAsString());
            DynamicSetting setting = AdjustableUtil.readDynamicSetting(obj);
            configSettings.put(block, setting);
         }
      } catch (Exception var12) {
         loadDefaultConfigSettings();
         save();
      }

      VineHelper.initFromConfigSettings();
   }
}
