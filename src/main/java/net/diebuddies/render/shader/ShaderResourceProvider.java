package net.diebuddies.render.shader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PackResources.ResourceOutput;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.jetbrains.annotations.Nullable;

public class ShaderResourceProvider implements ResourceProvider, PackResources {
   private static final String NAMESPACE = "physicsmod";

   public Optional<Resource> getResource(Identifier location) {
      return Optional.of(new Resource(this, () -> this.getResourceStream(location)));
   }

   public InputStream getResourceStream(Identifier Identifier) throws IOException {
      InputStream inputStream = this.processResourceAsStream(Identifier);
      if (inputStream != null) {
         return inputStream;
      } else {
         throw new FileNotFoundException(Identifier.getPath());
      }
   }

   protected InputStream processResourceAsStream(Identifier Identifier) {
      PackType packType = PackType.CLIENT_RESOURCES;
      String string = createPath(packType, Identifier);

      try {
         URL url = ShaderResourceProvider.class.getResource(string);
         return isResourceUrlValid(string, url) ? url.openStream() : ShaderResourceProvider.class.getResourceAsStream(string);
      } catch (IOException var5) {
         return ShaderResourceProvider.class.getResourceAsStream(string);
      }
   }

   private static String createPath(PackType packType, Identifier Identifier) {
      return "/" + packType.getDirectory() + "/physicsmod/" + Identifier.getPath();
   }

   private static boolean isResourceUrlValid(String string, @Nullable URL url) throws IOException {
      return url != null && (url.getProtocol().equals("jar") || validatePath(new File(url.getFile()), string));
   }

   public static boolean validatePath(File file, String string) throws IOException {
      String string2 = file.getCanonicalPath();
      return string2.endsWith(string);
   }

   public IoSupplier<InputStream> getRootResource(String... var1) {
      return null;
   }

   public IoSupplier<InputStream> getResource(PackType var1, Identifier var2) {
      return null;
   }

   public void listResources(PackType var1, String var2, String var3, ResourceOutput var4) {
   }

   public Set<String> getNamespaces(PackType var1) {
      return null;
   }

   public <T> T getMetadataSection(MetadataSectionType<T> metadataSectionType) throws IOException {
      return null;
   }

   public String packId() {
      return "physicsmod";
   }

   public void close() {
   }

   public PackLocationInfo location() {
      return null;
   }
}
