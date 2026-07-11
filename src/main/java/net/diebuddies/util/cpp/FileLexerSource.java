package net.diebuddies.util.cpp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import org.jspecify.annotations.NonNull;

public class FileLexerSource extends InputLexerSource {
   private final String path;
   private final File file;

   public FileLexerSource(@NonNull File file, @NonNull Charset charset, @NonNull String path) throws IOException {
      super(new FileInputStream(file), charset);
      this.file = file;
      this.path = path;
   }

   public FileLexerSource(@NonNull File file, @NonNull String path) throws IOException {
      this(file, Charset.defaultCharset(), path);
   }

   public FileLexerSource(@NonNull File file, @NonNull Charset charset) throws IOException {
      this(file, charset, file.getPath());
   }

   @Deprecated
   public FileLexerSource(@NonNull File file) throws IOException {
      this(file, Charset.defaultCharset());
   }

   public FileLexerSource(@NonNull String path, @NonNull Charset charset) throws IOException {
      this(new File(path), charset, path);
   }

   @Deprecated
   public FileLexerSource(@NonNull String path) throws IOException {
      this(path, Charset.defaultCharset());
   }

   @NonNull
   public File getFile() {
      return this.file;
   }

   @Override
   public String getPath() {
      return this.path;
   }

   @Override
   public String getName() {
      return this.getPath();
   }

   @Override
   public String toString() {
      return "file " + this.getPath();
   }
}
