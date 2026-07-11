package net.diebuddies.util.cpp;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.util.Objects;
import org.jspecify.annotations.NonNull;

public class CppReader extends Reader implements Closeable {
   private final Preprocessor cpp;
   private String token;
   private int idx;

   public CppReader(@NonNull final Reader r) {
      this.cpp = new Preprocessor(new LexerSource(r, true) {
         {
            Objects.requireNonNull(CppReader.this);
         }

         @Override
         public String getName() {
            return "<CppReader Input@" + System.identityHashCode(r) + ">";
         }
      });
      this.token = "";
      this.idx = 0;
   }

   public CppReader(@NonNull Preprocessor p) {
      this.cpp = p;
      this.token = "";
      this.idx = 0;
   }

   @NonNull
   public Preprocessor getPreprocessor() {
      return this.cpp;
   }

   public void addMacro(@NonNull String name) throws LexerException {
      this.cpp.addMacro(name);
   }

   public void addMacro(@NonNull String name, @NonNull String value) throws LexerException {
      this.cpp.addMacro(name, value);
   }

   private boolean refill() throws IOException {
      try {
         assert this.cpp != null : "cpp is null : was it closed?";

         if (this.token == null) {
            return false;
         } else {
            for (; this.idx >= this.token.length(); this.idx = 0) {
               Token tok = this.cpp.token();
               switch (tok.getType()) {
                  case 260:
                  case 261:
                     if (!this.cpp.getFeature(Feature.KEEPCOMMENTS)) {
                        this.token = " ";
                        break;
                     }
                  default:
                     this.token = tok.getText();
                     break;
                  case 265:
                     this.token = null;
                     return false;
               }
            }

            return true;
         }
      } catch (LexerException var3) {
         IOException _e = new IOException(String.valueOf(var3));
         _e.initCause(var3);
         throw _e;
      }
   }

   @Override
   public int read() throws IOException {
      return !this.refill() ? -1 : this.token.charAt(this.idx++);
   }

   @Override
   public int read(char[] cbuf, int off, int len) throws IOException {
      if (this.token == null) {
         return -1;
      } else {
         for (int i = 0; i < len; i++) {
            int ch = this.read();
            if (ch == -1) {
               return i;
            }

            cbuf[off + i] = (char)ch;
         }

         return len;
      }
   }

   @Override
   public void close() throws IOException {
      this.cpp.close();
      this.token = null;
   }
}
