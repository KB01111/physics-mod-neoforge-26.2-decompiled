package net.diebuddies.util.cpp;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

class JoinReader implements Closeable {
   private final Reader in;
   private PreprocessorListener listener;
   private LexerSource source;
   private boolean trigraphs;
   private boolean warnings;
   private int newlines;
   private boolean flushnl;
   private int[] unget;
   private int uptr;

   public JoinReader(Reader in, boolean trigraphs) {
      this.in = in;
      this.trigraphs = trigraphs;
      this.newlines = 0;
      this.flushnl = false;
      this.unget = new int[2];
      this.uptr = 0;
   }

   public JoinReader(Reader in) {
      this(in, false);
   }

   public void setTrigraphs(boolean enable, boolean warnings) {
      this.trigraphs = enable;
      this.warnings = warnings;
   }

   void init(Preprocessor pp, LexerSource s) {
      this.listener = pp.getListener();
      this.source = s;
      this.setTrigraphs(pp.getFeature(Feature.TRIGRAPHS), pp.getWarning(Warning.TRIGRAPHS));
   }

   private int __read() throws IOException {
      return this.uptr > 0 ? this.unget[--this.uptr] : this.in.read();
   }

   private void _unread(int c) {
      if (c != -1) {
         this.unget[this.uptr++] = c;
      }

      assert this.uptr <= this.unget.length : "JoinReader ungets too many characters";
   }

   protected void warning(String msg) throws LexerException {
      if (this.source != null) {
         this.source.warning(msg);
      } else {
         throw new LexerException(msg);
      }
   }

   private char trigraph(char raw, char repl) throws IOException, LexerException {
      if (this.trigraphs) {
         if (this.warnings) {
            this.warning("trigraph ??" + raw + " converted to " + repl);
         }

         return repl;
      } else {
         if (this.warnings) {
            this.warning("trigraph ??" + raw + " ignored");
         }

         this._unread(raw);
         this._unread(63);
         return '?';
      }
   }

   private int _read() throws IOException, LexerException {
      int c = this.__read();
      if (c == 63 && (this.trigraphs || this.warnings)) {
         int d = this.__read();
         if (d == 63) {
            int e = this.__read();
            switch (e) {
               case 33:
                  return this.trigraph((char)33, (char)124);
               case 34:
               case 35:
               case 36:
               case 37:
               case 38:
               case 42:
               case 43:
               case 44:
               case 46:
               case 48:
               case 49:
               case 50:
               case 51:
               case 52:
               case 53:
               case 54:
               case 55:
               case 56:
               case 57:
               case 58:
               case 59:
               default:
                  this._unread(e);
                  break;
               case 39:
                  return this.trigraph((char)39, (char)94);
               case 40:
                  return this.trigraph((char)40, (char)91);
               case 41:
                  return this.trigraph((char)41, (char)93);
               case 45:
                  return this.trigraph((char)45, (char)126);
               case 47:
                  return this.trigraph((char)47, (char)92);
               case 60:
                  return this.trigraph((char)60, (char)123);
               case 61:
                  return this.trigraph((char)61, (char)35);
               case 62:
                  return this.trigraph((char)62, (char)125);
            }
         }

         this._unread(d);
      }

      return c;
   }

   public int read() throws IOException, LexerException {
      if (this.flushnl) {
         if (this.newlines > 0) {
            this.newlines--;
            return 10;
         }

         this.flushnl = false;
      }

      while (true) {
         int c = this._read();
         switch (c) {
            case -1:
               if (this.newlines > 0) {
                  this.newlines--;
                  return 10;
               }
            default:
               return c;
            case 10:
            case 11:
            case 12:
            case 13:
            case 133:
            case 8232:
            case 8233:
               this.flushnl = true;
               return c;
            case 92:
         }

         int d = this._read();
         switch (d) {
            case 10:
               this.newlines++;
               break;
            case 13:
               this.newlines++;
               int e = this._read();
               if (e != 10) {
                  this._unread(e);
               }
               break;
            default:
               this._unread(d);
               return c;
         }
      }
   }

   public int read(char[] cbuf, int off, int len) throws IOException, LexerException {
      for (int i = 0; i < len; i++) {
         int ch = this.read();
         if (ch == -1) {
            return i;
         }

         cbuf[off + i] = (char)ch;
      }

      return len;
   }

   @Override
   public void close() throws IOException {
      this.in.close();
   }

   @Override
   public String toString() {
      return "JoinReader(nl=" + this.newlines + ")";
   }
}
