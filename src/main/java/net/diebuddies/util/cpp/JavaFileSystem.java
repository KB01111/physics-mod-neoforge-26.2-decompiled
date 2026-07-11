package net.diebuddies.util.cpp;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class JavaFileSystem implements VirtualFileSystem {
   @Override
   public VirtualFile getFile(String path) {
      return new JavaFileSystem.JavaFile(path);
   }

   @Override
   public VirtualFile getFile(String dir, String name) {
      return new JavaFileSystem.JavaFile(dir, name);
   }

   private class JavaFile extends File implements VirtualFile {
      public JavaFile(String path) {
         Objects.requireNonNull(JavaFileSystem.this);
         super(path);
      }

      public JavaFile(String dir, String name) {
         Objects.requireNonNull(JavaFileSystem.this);
         super(dir, name);
      }

      public JavaFile(File dir, String name) {
         Objects.requireNonNull(JavaFileSystem.this);
         super(dir, name);
      }

      public JavaFileSystem.JavaFile getParentFile() {
         String parent = this.getParent();
         if (parent != null) {
            return JavaFileSystem.this.new JavaFile(parent);
         } else {
            File absolute = this.getAbsoluteFile();
            parent = absolute.getParent();
            return JavaFileSystem.this.new JavaFile(parent);
         }
      }

      public JavaFileSystem.JavaFile getChildFile(String name) {
         return JavaFileSystem.this.new JavaFile(this, name);
      }

      @Override
      public Source getSource() throws IOException {
         return new FileLexerSource(this);
      }
   }
}
