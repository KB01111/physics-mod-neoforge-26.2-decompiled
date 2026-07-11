package net.diebuddies.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public final class ConfigServer {
   private static final String DIR = "config/physicsmod";
   private static final String CONFIG = "physics_server_config.json";
   public static volatile boolean collapse = true;
   public static volatile boolean dropBlocks = true;
   public static volatile int maxCollapseObjects = 100;
   public static volatile int collapseSpeed = 10;

   public static void init() {
   }

   private static JsonObject createConfig() {
      JsonObject config = new JsonObject();
      config.add("collapse", new JsonPrimitive(collapse));
      config.add("maxCollapseObjects", new JsonPrimitive(maxCollapseObjects));
      config.add("collapseSpeed", new JsonPrimitive(collapseSpeed));
      config.add("dropBlocks", new JsonPrimitive(dropBlocks));
      return config;
   }

   public static void save() {
      File directory = new File("config/physicsmod");
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File configFile = new File("config/physicsmod/physics_server_config.json");
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

      File configFile = new File("config/physicsmod/physics_server_config.json");
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
         collapse = config.get("collapse").getAsBoolean();
         maxCollapseObjects = config.get("maxCollapseObjects").getAsInt();
         collapseSpeed = config.get("collapseSpeed").getAsInt();
         dropBlocks = config.get("dropBlocks").getAsBoolean();
      } catch (Exception var6) {
      }
   }
}
