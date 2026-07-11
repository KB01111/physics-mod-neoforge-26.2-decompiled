package net.diebuddies.util.cpp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public class ResourceFileSystem implements VirtualFileSystem {
   private final ClassLoader loader;
   private final Charset charset;

   public ResourceFileSystem(@NonNull ClassLoader loader, @NonNull Charset charset) {
      this.loader = loader;
      this.charset = charset;
   }

   @Override
   public VirtualFile getFile(String path) {
      return new ResourceFileSystem.ResourceFile(this.loader, path);
   }

   @Override
   public VirtualFile getFile(String dir, String name) {
      return this.getFile(dir + "/" + name);
   }

   private class ResourceFile implements VirtualFile {
      private final ClassLoader loader;
      private final String path;

      public ResourceFile(ClassLoader loader, String path) {
         Objects.requireNonNull(ResourceFileSystem.this);
         super();
         this.loader = loader;
         this.path = path;
      }

      @Override
      public boolean isFile() {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      @Override
      public String getPath() {
         return this.path;
      }

      @Override
      public String getName() {
         return this.path.substring(this.path.lastIndexOf(47) + 1);
      }

      public ResourceFileSystem.ResourceFile getParentFile() {
         int idx = this.path.lastIndexOf(47);
         return idx < 1 ? null : ResourceFileSystem.this.new ResourceFile(this.loader, this.path.substring(0, idx));
      }

      public ResourceFileSystem.ResourceFile getChildFile(String name) {
         return ResourceFileSystem.this.new ResourceFile(this.loader, this.path + "/" + name);
      }

      @Override
      public Source getSource() throws IOException {
         InputStream stream = this.loader.getResourceAsStream(this.path);
         return new InputLexerSource(stream, ResourceFileSystem.this.charset);
      }
   }
}
