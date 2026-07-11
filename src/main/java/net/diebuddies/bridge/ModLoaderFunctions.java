package net.diebuddies.bridge;

import java.nio.file.Path;
import net.neoforged.fml.loading.FMLLoader;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class ModLoaderFunctions {
   public static String getModID() {
      return FMLLoader.getCurrent().getLoadingModList().getModFileById("physicsmod").versionString();
   }

   public static boolean isModLoaded(String modID) {
      if (modID.equalsIgnoreCase("optifine") || modID.equalsIgnoreCase("optifabric")) {
         try {
            return Class.forName("net.optifine.VersionCheckThread") != null;
         } catch (Exception var2) {
         }
      }

      return FMLLoader.getCurrent().getLoadingModList().getModFileById(modID) != null;
   }

   public static boolean isModVersionOrNewer(String modID, String version) {
      try {
         ArtifactVersion artifactVersion = new DefaultArtifactVersion(FMLLoader.getCurrent().getLoadingModList().getModFileById(modID).versionString());
         return artifactVersion.compareTo(new DefaultArtifactVersion(version)) >= 0;
      } catch (Exception var3) {
         return false;
      }
   }

   public static Path getGameDir() {
      return FMLLoader.getCurrent().getGameDir();
   }

   public static String getModloader() {
      return "neoforge";
   }
}
