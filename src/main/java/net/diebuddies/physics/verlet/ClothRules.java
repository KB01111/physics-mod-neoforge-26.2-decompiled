package net.diebuddies.physics.verlet;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Set;

public class ClothRules {
   public static final String UNKNOWN_CATEGORY = "Unknown";
   public static final String BREAKS_ALL = "all";
   private String category;
   private Set<String> allowedParts = new ObjectOpenHashSet();
   private Set<String> breaks = new ObjectOpenHashSet();
   private Set<String> hiddenParts = new ObjectOpenHashSet();
   private Set<String> ignoreParts = new ObjectOpenHashSet();
   private Set<String> hideArmor = new ObjectOpenHashSet();
   private String specialTexture;
   private boolean dynamic = true;
   private boolean local;

   public ClothRules() {
      this.category = "Unknown";
   }

   public boolean canUseOn(String part) {
      return this.allowedParts.isEmpty() ? true : this.allowedParts.contains(part);
   }

   public boolean isBreaking(Cloth cloth) {
      ClothRules rules = cloth.rules;
      return this.breaks.contains(rules.getCategory()) || this.breaks.contains("all");
   }

   public static ClothRules load(File file, boolean local) {
      if (!file.exists()) {
         return new ClothRules();
      } else {
         Gson gson = new Gson();
         ClothRules rules = new ClothRules();
         rules.local = local;

         try {
            JsonObject config = (JsonObject)gson.fromJson(new FileReader(file), JsonObject.class);
            if (config.has("parts")) {
               JsonArray parts = config.get("parts").getAsJsonArray();

               for (int i = 0; i < parts.size(); i++) {
                  rules.allowedParts.add(parts.get(i).getAsString());
               }
            }

            if (config.has("dynamic")) {
               rules.dynamic = config.get("dynamic").getAsBoolean();
            }

            if (config.has("category")) {
               rules.category = config.get("category").getAsString();
            }

            if (config.has("breaks")) {
               JsonArray breaks = config.get("breaks").getAsJsonArray();

               for (int i = 0; i < breaks.size(); i++) {
                  rules.breaks.add(breaks.get(i).getAsString());
               }
            }

            if (config.has("hideParts")) {
               JsonArray hideParts = config.get("hideParts").getAsJsonArray();

               for (int i = 0; i < hideParts.size(); i++) {
                  rules.hiddenParts.add(hideParts.get(i).getAsString());
               }
            }

            if (config.has("ignoreParts")) {
               JsonArray ignoreParts = config.get("ignoreParts").getAsJsonArray();

               for (int i = 0; i < ignoreParts.size(); i++) {
                  rules.ignoreParts.add(ignoreParts.get(i).getAsString());
               }
            }

            if (config.has("hideArmor")) {
               JsonArray hideArmor = config.get("hideArmor").getAsJsonArray();

               for (int i = 0; i < hideArmor.size(); i++) {
                  rules.hideArmor.add(hideArmor.get(i).getAsString());
               }
            }

            if (config.has("specialTexture")) {
               rules.specialTexture = config.get("specialTexture").getAsString();
            }
         } catch (JsonIOException | FileNotFoundException | JsonSyntaxException var7) {
            var7.printStackTrace();
         }

         return rules;
      }
   }

   public String getSpecialTexture() {
      return this.specialTexture;
   }

   public boolean isDynamic() {
      return this.dynamic;
   }

   public Set<String> getAllowedParts() {
      return this.allowedParts;
   }

   public String getCategory() {
      return this.category;
   }

   public Set<String> getHiddenParts() {
      return this.hiddenParts;
   }

   public Set<String> getIgnoreParts() {
      return this.ignoreParts;
   }

   public Set<String> getHiddenArmorPieces() {
      return this.hideArmor;
   }

   public boolean isLocal() {
      return this.local;
   }
}
