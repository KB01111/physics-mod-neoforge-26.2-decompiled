package net.diebuddies.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.verlet.Cloth;
import net.diebuddies.util.HttpRequest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import org.apache.commons.io.FileUtils;
import org.jspecify.annotations.Nullable;

public final class ConfigCloth {
   private static final String DIR = "config/physicsmod";
   private static final String CONFIG = "physics_cloth_config.json";
   public static final String YOURSELF = "physicsmod:yourself";
   public static final String OTHER_PLAYER = "physicsmod:player:";
   public static final String ALL_PLAYER = "minecraft:player";
   private static volatile String version = "";
   private static Map<String, Map<String, ConfigCloth.ClothList>> entityCustomizations = new Object2ObjectOpenHashMap();
   public static volatile boolean clothUpToDate = false;
   public static volatile boolean isChangingPlayer = false;

   public static Map<String, ConfigCloth.ClothList> getCustomizationParts(Entity entity) {
      EntityType<?> entityType = entity.getType();
      String uuid = entity.getStringUUID();
      Map<String, ConfigCloth.ClothList> customization = entityCustomizations.get(uuid);
      if (customization != null) {
         return customization;
      } else if (entity == Minecraft.getInstance().player) {
         return entityCustomizations.get("physicsmod:yourself");
      } else if (entityType == EntityTypes.PLAYER && entity instanceof AbstractClientPlayer player) {
         customization = entityCustomizations.get("physicsmod:player:" + player.getGameProfile().name());
         return customization == null ? entityCustomizations.get("minecraft:player") : customization;
      } else {
         String entityLocation = EntityType.getKey(entityType).toString();
         return entityCustomizations.get(entityLocation);
      }
   }

   public static Map<String, Map<String, ConfigCloth.ClothList>> getEntityCustomizations() {
      return entityCustomizations;
   }

   public static Map<String, ConfigCloth.ClothList> getCustomizationParts(String selection) {
      return entityCustomizations.get(selection);
   }

   public static void setCustomizationParts(String selection, Map<String, ConfigCloth.ClothList> parts) {
      if (parts.isEmpty()) {
         entityCustomizations.remove(selection);
      } else {
         entityCustomizations.put(selection, parts);
      }
   }

   @Nullable
   public static ClothList getPart(Entity entity, String part) {
      Map<String, ConfigCloth.ClothList> customizations = getCustomizationParts(entity);
      return customizations == null ? null : customizations.get(part);
   }

   public static boolean hasCategory(Entity entity, String category) {
      Map<String, ConfigCloth.ClothList> customizations = getCustomizationParts(entity);
      if (customizations == null) {
         return false;
      } else {
         for (ConfigCloth.ClothList clothList : customizations.values()) {
            for (String clothPiece : clothList.clothPieces) {
               Cloth cloth = PhysicsMod.cloth.get(clothPiece);
               if (cloth != null && cloth.rules.getCategory().equals(category)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static Cloth getCategory(Entity entity, String category) {
      Map<String, ConfigCloth.ClothList> customizations = getCustomizationParts(entity);
      if (customizations == null) {
         return null;
      } else {
         for (ConfigCloth.ClothList clothList : customizations.values()) {
            for (String clothPiece : clothList.clothPieces) {
               Cloth cloth = PhysicsMod.cloth.get(clothPiece);
               if (cloth != null && cloth.rules.getCategory().equals(category)) {
                  return cloth;
               }
            }
         }

         return null;
      }
   }

   @Nullable
   public static String getCategory(String entity, String category) {
      Map<String, ConfigCloth.ClothList> customizations = getCustomizationParts(entity);
      if (customizations == null) {
         return null;
      } else {
         for (ConfigCloth.ClothList clothList : customizations.values()) {
            for (String clothPiece : clothList.clothPieces) {
               Cloth cloth = PhysicsMod.cloth.get(clothPiece);
               if (cloth != null && cloth.rules.getCategory().equals(category)) {
                  return clothPiece;
               }
            }
         }

         return null;
      }
   }

   public static Map<String, ConfigCloth.ClothList> resetCustomization(String entity) {
      return entityCustomizations.remove(entity);
   }

   public static void setCategory(String entity, String category, @Nullable String clothPiece) {
      Map<String, ConfigCloth.ClothList> customizations = getCustomizationParts(entity);
      if (customizations != null) {
         for (ConfigCloth.ClothList clothList : customizations.values()) {
            Iterator<String> it = clothList.clothPieces.iterator();

            while (it.hasNext()) {
               String piece = it.next();
               Cloth cloth = PhysicsMod.cloth.get(piece);
               if (cloth != null && cloth.rules.getCategory().equals(category)) {
                  it.remove();
               }
            }
         }
      }

      if (clothPiece != null) {
         customizations = entityCustomizations.computeIfAbsent(entity, key -> new Object2ObjectOpenHashMap());
         Cloth cloth = PhysicsMod.cloth.get(clothPiece);
         String part = cloth.rules.getAllowedParts().iterator().next();
         ConfigCloth.ClothList clothList = customizations.computeIfAbsent(part, key -> new ConfigCloth.ClothList());
         clothList.add(clothPiece);

         for (Entry<String, ConfigCloth.ClothList> entry : customizations.entrySet()) {
            ConfigCloth.ClothList list = entry.getValue();
            Iterator<String> it = list.getClothPieces().iterator();

            while (it.hasNext()) {
               String piece = it.next();
               Cloth toRemove = PhysicsMod.cloth.get(piece);
               if (toRemove != null && toRemove != cloth && (cloth.rules.isBreaking(toRemove) || toRemove.rules.isBreaking(cloth))) {
                  it.remove();
               }
            }
         }
      }

      cleanUpEntries();
   }

   private static void cleanUpEntries() {
      Iterator<Entry<String, Map<String, ConfigCloth.ClothList>>> itEntities = entityCustomizations.entrySet().iterator();

      while (itEntities.hasNext()) {
         Entry<String, Map<String, ConfigCloth.ClothList>> entryEntity = itEntities.next();
         String entity = entryEntity.getKey();
         Map<String, ConfigCloth.ClothList> customizations = entryEntity.getValue();
         Iterator<Entry<String, ConfigCloth.ClothList>> itMap = customizations.entrySet().iterator();

         while (itMap.hasNext()) {
            Entry<String, ConfigCloth.ClothList> entry = itMap.next();
            ConfigCloth.ClothList clothList = entry.getValue();
            if (clothList.getClothPieces().isEmpty()) {
               itMap.remove();
            }
         }

         if (customizations.isEmpty()) {
            itEntities.remove();
         }
      }
   }

   @Nullable
   public static ClothList getPart(String selection, String part) {
      Map<String, ConfigCloth.ClothList> customizations = entityCustomizations.get(selection);
      return customizations == null ? null : customizations.get(part);
   }

   public static void addPart(String entity, String part, String cloth) {
      Map<String, ConfigCloth.ClothList> customizations = entityCustomizations.computeIfAbsent(entity, key -> new Object2ObjectOpenHashMap());
      ConfigCloth.ClothList clothList = customizations.computeIfAbsent(part, key -> new ConfigCloth.ClothList());
      clothList.add(cloth);
   }

   public static void init() {
      downloadClothModels();
   }

   public static UUID getMinecraftUUID() {
      return Minecraft.getInstance().getUser() != null && Minecraft.getInstance().getUser().getProfileId() != null
         ? Minecraft.getInstance().getUser().getProfileId()
         : null;
   }

   public static String getMinecraftUUIDString() {
      return Minecraft.getInstance().getUser() != null && Minecraft.getInstance().getUser().getProfileId() != null
         ? Minecraft.getInstance().getUser().getProfileId().toString()
         : null;
   }

   private static void downloadClothModels() {
      Thread clothNetworking = new Thread(() -> {
         while (true) {
            try {
               String currentVersion = HttpRequest.get("http://customize.minecraftphysicsmod.com/version");
               File destDir = new File(".physics_mod_cache");
               if (version.equals(currentVersion) && destDir.exists()) {
                  clothUpToDate = true;
                  return;
               }

               version = currentVersion;

               while (true) {
                  try {
                     if (destDir.exists()) {
                        FileUtils.forceDelete(destDir);
                     }

                     if (!destDir.exists()) {
                        destDir.mkdirs();
                     }

                     try (InputStream stream = HttpRequest.getStream("http://customize.minecraftphysicsmod.com/cloth.zip")) {
                        byte[] buffer = new byte[1024];
                        ZipInputStream zis = new ZipInputStream(stream);

                        for (ZipEntry zipEntry = zis.getNextEntry(); zipEntry != null; zipEntry = zis.getNextEntry()) {
                           File newFile = newFile(destDir, zipEntry);
                           if (zipEntry.isDirectory()) {
                              if (!newFile.isDirectory() && !newFile.mkdirs()) {
                                 zis.close();
                                 throw new IOException("Failed to create directory " + newFile);
                              }
                           } else {
                              File parent = newFile.getParentFile();
                              if (!parent.isDirectory() && !parent.mkdirs()) {
                                 zis.close();
                                 throw new IOException("Failed to create directory " + parent);
                              }

                              FileOutputStream fos = new FileOutputStream(newFile);

                              int len;
                              while ((len = zis.read(buffer)) > 0) {
                                 fos.write(buffer, 0, len);
                              }

                              fos.close();
                           }
                        }

                        zis.closeEntry();
                        zis.close();
                     }

                     clothUpToDate = true;
                     PhysicsMod.reloadCloth = true;
                     return;
                  } catch (Exception var13) {
                     var13.printStackTrace();
                     Thread.sleep(120000L);
                  }
               }
            } catch (Exception var14) {
               var14.printStackTrace();

               try {
                  Thread.sleep(60000L);
               } catch (InterruptedException var10) {
                  var10.printStackTrace();
               }
            }
         }
      });
      clothNetworking.setName("Cloth Network Thread");
      clothNetworking.setDaemon(true);
      clothNetworking.start();
   }

   private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
      File destFile = new File(destinationDir, zipEntry.getName());
      String destDirPath = destinationDir.getCanonicalPath();
      String destFilePath = destFile.getCanonicalPath();
      if (!destFilePath.startsWith(destDirPath + File.separator)) {
         throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
      } else {
         return destFile;
      }
   }

   private static JsonObject createConfig() {
      JsonObject config = new JsonObject();
      JsonObject entityCustomizationsElement = new JsonObject();

      for (Entry<String, Map<String, ConfigCloth.ClothList>> entry : entityCustomizations.entrySet()) {
         Map<String, ConfigCloth.ClothList> partAndClothPieces = entry.getValue();
         JsonObject partAndClothElement = new JsonObject();

         for (Entry<String, ConfigCloth.ClothList> partAndClothPiece : partAndClothPieces.entrySet()) {
            JsonArray cloth = new JsonArray();

            for (String clothPiece : partAndClothPiece.getValue().getClothPieces()) {
               cloth.add(clothPiece);
            }

            partAndClothElement.add(partAndClothPiece.getKey(), cloth);
         }

         entityCustomizationsElement.add(entry.getKey(), partAndClothElement);
      }

      config.add("entityCustomizations", entityCustomizationsElement);
      config.add("version", new JsonPrimitive(version));
      return config;
   }

   public static void save() {
      File directory = new File("config/physicsmod");
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File configFile = new File("config/physicsmod/physics_cloth_config.json");
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

   public static String getPlayerUpdate() {
      Map<String, ConfigCloth.ClothList> customization = getCustomizationParts("physicsmod:yourself");
      JsonArray array = new JsonArray();
      if (customization != null) {
         for (Entry<String, ConfigCloth.ClothList> entry : customization.entrySet()) {
            for (String part : entry.getValue().getClothPieces()) {
               array.add(part);
            }
         }
      }

      return array.toString();
   }

   static {
      File directory = new File("config/physicsmod");
      if (!directory.exists()) {
         directory.mkdirs();
      }

      File configFile = new File("config/physicsmod/physics_cloth_config.json");
      JsonObject config;
      if (!configFile.exists()) {
         config = createConfig();

         try {
            configFile.createNewFile();

            try (Writer writer = new FileWriter(configFile)) {
               Gson gson = new GsonBuilder().setPrettyPrinting().create();
               gson.toJson(config, writer);
            }
         } catch (IOException var19) {
            var19.printStackTrace();
         }
      } else {
         Gson gson = new Gson();
         config = createConfig();

         try {
            config = (JsonObject)gson.fromJson(new FileReader(configFile), JsonObject.class);
         } catch (JsonIOException | FileNotFoundException | JsonSyntaxException var17) {
            var17.printStackTrace();
         }
      }

      try {
         JsonObject entityCustomizationsElement = config.get("entityCustomizations").getAsJsonObject();

         for (Entry<String, JsonElement> entry : entityCustomizationsElement.entrySet()) {
            String entity = entry.getKey();
            JsonObject partAndClothElement = entry.getValue().getAsJsonObject();
            Map<String, ConfigCloth.ClothList> partAndClothPieces = new Object2ObjectOpenHashMap();

            for (Entry<String, JsonElement> partEntry : partAndClothElement.entrySet()) {
               String part = partEntry.getKey();
               JsonArray cloth = partEntry.getValue().getAsJsonArray();
               ConfigCloth.ClothList clothList = new ConfigCloth.ClothList();

               for (int i = 0; i < cloth.size(); i++) {
                  clothList.add(cloth.get(i).getAsString());
               }

               partAndClothPieces.put(part, clothList);
            }

            if (!partAndClothPieces.isEmpty()) {
               entityCustomizations.put(entity, partAndClothPieces);
            }
         }
      } catch (Exception var20) {
      }

      try {
         version = config.get("version").getAsString();
      } catch (Exception var15) {
      }
   }

   public static class ClothList {
      private Set<String> clothPieces = new ObjectOpenHashSet();

      public ClothList(String clothPiece) {
         this.clothPieces.add(clothPiece);
      }

      public ClothList() {
      }

      public ConfigCloth.ClothList add(String clothPiece) {
         this.clothPieces.add(clothPiece);
         return this;
      }

      public Set<String> getClothPieces() {
         return this.clothPieces;
      }

      public ConfigCloth.ClothList copy() {
         ConfigCloth.ClothList list = new ConfigCloth.ClothList();

         for (String piece : this.clothPieces) {
            list.clothPieces.add(piece);
         }

         return list;
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.clothPieces);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            ConfigCloth.ClothList other = (ConfigCloth.ClothList)obj;
            return Objects.equals(this.clothPieces, other.clothPieces);
         }
      }
   }
}
