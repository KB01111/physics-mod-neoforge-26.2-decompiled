package net.diebuddies.util.cpp;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ChrootFileSystem implements VirtualFileSystem {
   private File root;

   public ChrootFileSystem(File root) {
      this.root = root;
   }

   @Override
   public VirtualFile getFile(String path) {
      return new ChrootFileSystem.ChrootFile(path);
   }

   @Override
   public VirtualFile getFile(String dir, String name) {
      return new ChrootFileSystem.ChrootFile(dir, name);
   }

   private class ChrootFile extends File implements VirtualFile {
      private File rfile;

      public ChrootFile(String path) {
         Objects.requireNonNull(ChrootFileSystem.this);
         super(path);
      }

      public ChrootFile(String dir, String name) {
         Objects.requireNonNull(ChrootFileSystem.this);
         super(dir, name);
      }

      public ChrootFile(File dir, String name) {
         Objects.requireNonNull(ChrootFileSystem.this);
         super(dir, name);
      }

      public ChrootFileSystem.ChrootFile getParentFile() {
         return ChrootFileSystem.this.new ChrootFile(this.getParent());
      }

      public ChrootFileSystem.ChrootFile getChildFile(String name) {
         return ChrootFileSystem.this.new ChrootFile(this, name);
      }

      @Override
      public boolean isFile() {
         File real = new File(ChrootFileSystem.this.root, this.getPath());
         return real.isFile();
      }

      @Override
      public Source getSource() throws IOException {
         return new FileLexerSource(new File(ChrootFileSystem.this.root, this.getPath()), this.getPath());
      }
   }
}
