package net.diebuddies.util.cpp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import org.jspecify.annotations.NonNull;

public class LexerSource extends Source {
   private static final boolean DEBUG = false;
   private JoinReader reader;
   private final boolean ppvalid;
   private boolean bol;
   private boolean include;
   private boolean digraphs;
   private int u0;
   private int u1;
   private int ucount;
   private int line;
   private int column;
   private int lastcolumn;
   private boolean cr;

   @NonNull
   protected static BufferedReader toBufferedReader(@NonNull Reader r) {
      return r instanceof BufferedReader ? (BufferedReader)r : new BufferedReader(r);
   }

   public LexerSource(Reader r, boolean ppvalid) {
      this.reader = new JoinReader(r);
      this.ppvalid = ppvalid;
      this.bol = true;
      this.include = false;
      this.digraphs = true;
      this.ucount = 0;
      this.line = 1;
      this.column = 0;
      this.lastcolumn = -1;
      this.cr = false;
   }

   @Override
   void init(Preprocessor pp) {
      super.init(pp);
      this.digraphs = pp.getFeature(Feature.DIGRAPHS);
      this.reader.init(pp, this);
   }

   @Override
   public int getLine() {
      return this.line;
   }

   @Override
   public int getColumn() {
      return this.column;
   }

   @Override
   boolean isNumbered() {
      return true;
   }

   private void _error(String msg, boolean error) throws LexerException {
      int _l = this.line;
      int _c = this.column;
      if (_c == 0) {
         _c = this.lastcolumn;
         _l--;
      } else {
         _c--;
      }

      if (error) {
         super.error(_l, _c, msg);
      } else {
         super.warning(_l, _c, msg);
      }
   }

   final void error(String msg) throws LexerException {
      this._error(msg, true);
   }

   final void warning(String msg) throws LexerException {
      this._error(msg, false);
   }

   void setInclude(boolean b) {
      this.include = b;
   }

   private static boolean isLineSeparator(int c) {
      switch ((char)c) {
         case '\n':
         case '\u000b':
         case '\f':
         case '\r':
         case '\u0085':
         case '\u2028':
         case '\u2029':
            return true;
         default:
            return c == -1;
      }
   }

   private int read() throws IOException, LexerException {
      assert this.ucount <= 2 : "Illegal ucount: " + this.ucount;

      int c = switch (this.ucount) {
         case 1 -> {
            this.ucount = 0;
            yield this.u0;
         }
         case 2 -> {
            this.ucount = 1;
            yield this.u1;
         }
         default -> {
            if (this.reader == null) {
               yield -1;
            } else {
               yield this.reader.read();
            }
         }
      };

      switch (c) {
         case -1:
            this.cr = false;
            break;
         case 10:
            if (this.cr) {
               this.cr = false;
               break;
            }
         case 11:
         case 12:
         case 133:
         case 8232:
         case 8233:
            this.cr = false;
            this.line++;
            this.lastcolumn = this.column;
            this.column = 0;
            break;
         case 13:
            this.cr = true;
            this.line++;
            this.lastcolumn = this.column;
            this.column = 0;
            break;
         default:
            this.cr = false;
            this.column++;
      }

      return c;
   }

   private void unread(int c) throws IOException {
      if (c != -1) {
         if (isLineSeparator(c)) {
            this.line--;
            this.column = this.lastcolumn;
            this.cr = false;
         } else {
            this.column--;
         }

         switch (this.ucount) {
            case 0:
               this.u0 = c;
               this.ucount = 1;
               break;
            case 1:
               this.u1 = c;
               this.ucount = 2;
               break;
            default:
               throw new IllegalStateException("Cannot unget another character!");
         }
      }
   }

   @NonNull
   private Token invalid(StringBuilder text, String reason) throws IOException, LexerException {
      int d;
      for (d = this.read(); !isLineSeparator(d); d = this.read()) {
         text.append((char)d);
      }

      this.unread(d);
      return new Token(300, text.toString(), reason);
   }

   @NonNull
   private Token ccomment() throws IOException, LexerException {
      StringBuilder text = new StringBuilder("/*");

      while (true) {
         int d = this.read();
         if (d == -1) {
            return new Token(300, text.toString(), "Unterminated comment");
         }

         text.append((char)d);
         if (d == 42) {
            do {
               d = this.read();
               if (d == -1) {
                  return new Token(300, text.toString(), "Unterminated comment");
               }

               text.append((char)d);
            } while (d == 42);

            if (d == 47) {
               return new Token(260, text.toString());
            }
         }
      }
   }

   @NonNull
   private Token cppcomment() throws IOException, LexerException {
      StringBuilder text = new StringBuilder("//");

      int d;
      for (d = this.read(); !isLineSeparator(d); d = this.read()) {
         text.append((char)d);
      }

      this.unread(d);
      return new Token(261, text.toString());
   }

   private int escape(StringBuilder text) throws IOException, LexerException {
      int d = this.read();
      switch (d) {
         case 34:
            text.append('"');
            return 34;
         case 39:
            text.append('\'');
            return 39;
         case 48:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 55: {
            int len = 0;
            int val = 0;

            do {
               val = (val << 3) + Character.digit(d, 8);
               text.append((char)d);
               d = this.read();
            } while (++len < 3 && Character.digit(d, 8) != -1);

            this.unread(d);
            return val;
         }
         case 92:
            text.append('\\');
            return 92;
         case 97:
            text.append('a');
            return 7;
         case 98:
            text.append('b');
            return 8;
         case 102:
            text.append('f');
            return 12;
         case 110:
            text.append('n');
            return 10;
         case 114:
            text.append('r');
            return 13;
         case 116:
            text.append('t');
            return 9;
         case 118:
            text.append('v');
            return 11;
         case 120: {
            text.append((char)d);
            int len = 0;
            int val = 0;

            while (len++ < 2) {
               d = this.read();
               if (Character.digit(d, 16) == -1) {
                  this.unread(d);
                  break;
               }

               val = (val << 4) + Character.digit(d, 16);
               text.append((char)d);
            }

            return val;
         }
         default:
            this.warning("Unnecessary escape character " + (char)d);
            text.append((char)d);
            return d;
      }
   }

   @NonNull
   private Token character() throws IOException, LexerException {
      StringBuilder text = new StringBuilder("'");
      int d = this.read();
      if (d == 92) {
         text.append('\\');
         d = this.escape(text);
      } else {
         if (isLineSeparator(d)) {
            this.unread(d);
            return new Token(300, text.toString(), "Unterminated character literal");
         }

         if (d == 39) {
            text.append('\'');
            return new Token(300, text.toString(), "Empty character literal");
         }

         if (!Character.isDefined(d)) {
            text.append('?');
            return this.invalid(text, "Illegal unicode character literal");
         }

         text.append((char)d);
      }

      int e = this.read();
      if (e == 39) {
         text.append('\'');
         return new Token(259, text.toString(), (char)d);
      } else {
         while (true) {
            if (isLineSeparator(e)) {
               this.unread(e);
               break;
            }

            text.append((char)e);
            if (e == 39) {
               break;
            }

            e = this.read();
         }

         return new Token(300, text.toString(), "Illegal character constant " + text);
      }
   }

   @NonNull
   private Token string(char open, char close) throws IOException, LexerException {
      StringBuilder text = new StringBuilder();
      text.append(open);
      StringBuilder buf = new StringBuilder();

      while (true) {
         int c = this.read();
         if (c == close) {
            text.append(close);
            switch (close) {
               case '"':
                  return new Token(292, text.toString(), buf.toString());
               case '\'':
                  if (buf.length() == 1) {
                     return new Token(259, text.toString(), buf.toString());
                  }

                  return new Token(291, text.toString(), buf.toString());
               case '>':
                  return new Token(269, text.toString(), buf.toString());
               default:
                  throw new IllegalStateException("Unknown closing character " + close);
            }
         }

         if (c == 92) {
            text.append('\\');
            if (!this.include) {
               char d = (char)this.escape(text);
               buf.append(d);
            }
         } else {
            if (c == -1) {
               this.unread(c);
               return new Token(300, text.toString(), "End of file in string literal after " + buf);
            }

            if (isLineSeparator(c)) {
               this.unread(c);
               return new Token(300, text.toString(), "Unterminated string literal after " + buf);
            }

            text.append((char)c);
            buf.append((char)c);
         }
      }
   }

   @NonNull
   private Token _number_suffix(StringBuilder text, NumericValue value, int d) throws IOException, LexerException {
      int flags = 0;

      while (true) {
         while (d == 85 || d == 117) {
            if ((flags & 1) != 0) {
               this.warning("Duplicate unsigned suffix " + d);
            }

            flags |= 1;
            text.append((char)d);
            d = this.read();
         }

         if (d != 76 && d != 108) {
            if (d != 73 && d != 105) {
               if (d != 70 && d != 102) {
                  if (d != 68 && d != 100) {
                     if (!Character.isUnicodeIdentifierPart(d)) {
                        this.unread(d);
                        value.setFlags(flags);
                        return new Token(272, text.toString(), value);
                     }

                     String reason;
                     for (reason = "Invalid suffix \"" + (char)d + "\" on numeric constant"; Character.isUnicodeIdentifierPart(d); d = this.read()) {
                        text.append((char)d);
                     }

                     this.unread(d);
                     return new Token(300, text.toString(), reason);
                  }

                  if ((flags & 62) != 0) {
                     this.warning("Multiple length suffixes after " + text);
                  }

                  flags |= 32;
                  text.append((char)d);
                  d = this.read();
               } else {
                  if ((flags & 62) != 0) {
                     this.warning("Multiple length suffixes after " + text);
                  }

                  flags |= 16;
                  text.append((char)d);
                  d = this.read();
               }
            } else {
               if ((flags & 62) != 0) {
                  this.warning("Multiple length suffixes after " + text);
               }

               flags |= 2;
               text.append((char)d);
               d = this.read();
            }
         } else {
            if ((flags & 62) != 0) {
               this.warning("Multiple length suffixes after " + text);
            }

            text.append((char)d);
            int e = this.read();
            if (e == d) {
               flags |= 8;
               text.append((char)e);
               d = this.read();
            } else {
               flags |= 4;
               d = e;
            }
         }
      }
   }

   @NonNull
   private String _number_part(StringBuilder text, int base, boolean sign) throws IOException, LexerException {
      StringBuilder part = new StringBuilder();
      int d = this.read();
      if (sign && (d == 43 || d == 45)) {
         text.append((char)d);
         part.append((char)d);
         d = this.read();
      }

      while (Character.digit(d, base) != -1) {
         text.append((char)d);
         part.append((char)d);
         d = this.read();
      }

      this.unread(d);
      return part.toString();
   }

   @NonNull
   private Token number_hex(char x) throws IOException, LexerException {
      StringBuilder text = new StringBuilder("0");
      text.append(x);
      String integer = this._number_part(text, 16, false);
      NumericValue value = new NumericValue(16, integer);
      int d = this.read();
      if (d == 46) {
         text.append((char)d);
         String fraction = this._number_part(text, 16, false);
         value.setFractionalPart(fraction);
         d = this.read();
      }

      if (d == 80 || d == 112) {
         text.append((char)d);
         String exponent = this._number_part(text, 10, true);
         value.setExponent(2, exponent);
         d = this.read();
      }

      return this._number_suffix(text, value, d);
   }

   private static boolean is_octal(@NonNull String text) {
      if (!text.startsWith("0")) {
         return false;
      } else {
         for (int i = 0; i < text.length(); i++) {
            if (Character.digit(text.charAt(i), 8) == -1) {
               return false;
            }
         }

         return true;
      }
   }

   @NonNull
   private Token number_decimal() throws IOException, LexerException {
      StringBuilder text = new StringBuilder();
      String integer = this._number_part(text, 10, false);
      String fraction = null;
      String exponent = null;
      int d = this.read();
      if (d == 46) {
         text.append((char)d);
         fraction = this._number_part(text, 10, false);
         d = this.read();
      }

      if (d == 69 || d == 101) {
         text.append((char)d);
         exponent = this._number_part(text, 10, true);
         d = this.read();
      }

      int base = 10;
      if (fraction == null && exponent == null && integer.startsWith("0")) {
         if (!is_octal(integer)) {
            this.warning("Decimal constant starts with 0, but not octal: " + integer);
         } else {
            base = 8;
         }
      }

      NumericValue value = new NumericValue(base, integer);
      if (fraction != null) {
         value.setFractionalPart(fraction);
      }

      if (exponent != null) {
         value.setExponent(10, exponent);
      }

      return this._number_suffix(text, value, d);
   }

   @NonNull
   private Token number() throws IOException, LexerException {
      int c = this.read();
      Token tok;
      if (c == 48) {
         int d = this.read();
         if (d != 120 && d != 88) {
            this.unread(d);
            this.unread(c);
            tok = this.number_decimal();
         } else {
            tok = this.number_hex((char)d);
         }
      } else {
         if (!Character.isDigit(c) && c != 46) {
            throw new LexerException("Asked to parse something as a number which isn't: " + (char)c);
         }

         this.unread(c);
         tok = this.number_decimal();
      }

      return tok;
   }

   @NonNull
   private Token identifier(int c) throws IOException, LexerException {
      StringBuilder text = new StringBuilder();
      text.append((char)c);

      while (true) {
         int d = this.read();
         if (!Character.isIdentifierIgnorable(d)) {
            if (!Character.isJavaIdentifierPart(d)) {
               this.unread(d);
               return new Token(270, text.toString());
            }

            text.append((char)d);
         }
      }
   }

   @NonNull
   private Token whitespace(int c) throws IOException, LexerException {
      StringBuilder text = new StringBuilder();
      text.append((char)c);

      while (true) {
         int d = this.read();
         if (this.ppvalid && isLineSeparator(d) || !Character.isWhitespace(d)) {
            this.unread(d);
            return new Token(294, text.toString());
         }

         text.append((char)d);
      }
   }

   @NonNull
   private Token cond(char c, int yes, int no) throws IOException, LexerException {
      int d = this.read();
      if (c == d) {
         return new Token(yes);
      } else {
         this.unread(d);
         return new Token(no);
      }
   }

   @Override
   public Token token() throws IOException, LexerException {
      Token tok = null;
      int _l = this.line;
      int _c = this.column;
      int c = this.read();
      switch (c) {
         case -1:
            this.close();
            tok = new Token(265, _l, _c, "<eof>");
            break;
         case 10:
            if (this.ppvalid) {
               this.bol = true;
               if (this.include) {
                  tok = new Token(284, _l, _c, "\n");
               } else {
                  int nls = 0;

                  int dxxxxxxx;
                  do {
                     nls++;
                     dxxxxxxx = this.read();
                  } while (dxxxxxxx == 10);

                  this.unread(dxxxxxxx);
                  char[] text = new char[nls];

                  for (int i = 0; i < text.length; i++) {
                     text[i] = '\n';
                  }

                  tok = new Token(284, _l, _c, new String(text));
               }

               return tok;
            }
            break;
         case 33:
            tok = this.cond('=', 283, 33);
            break;
         case 34:
            tok = this.string('"', '"');
            break;
         case 35:
            if (this.bol) {
               tok = new Token(268);
            } else {
               tok = this.cond('#', 286, 35);
            }
            break;
         case 37:
            int dxxxxxxxxx = this.read();
            if (dxxxxxxxxx == 61) {
               tok = new Token(281);
            } else if (this.digraphs && dxxxxxxxxx == 62) {
               tok = new Token(125);
            } else if (this.digraphs && dxxxxxxxxx == 58) {
               dxxxxxxxxx = this.read();
               if (dxxxxxxxxx != 37) {
                  this.unread(dxxxxxxxxx);
                  tok = new Token(35);
               } else {
                  dxxxxxxxxx = this.read();
                  if (dxxxxxxxxx != 58) {
                     this.unread(dxxxxxxxxx);
                     this.unread(37);
                     tok = new Token(35);
                  } else {
                     tok = new Token(286);
                  }
               }
            } else {
               this.unread(dxxxxxxxxx);
            }
            break;
         case 38:
            int dx = this.read();
            if (dx == 38) {
               tok = this.cond('=', 274, 273);
            } else if (dx == 61) {
               tok = new Token(257);
            } else {
               this.unread(dx);
            }
            break;
         case 39:
            tok = this.string('\'', '\'');
            break;
         case 42:
            tok = this.cond('=', 282, 42);
            break;
         case 43:
            int dxxxxxx = this.read();
            if (dxxxxxx == 43) {
               tok = new Token(271);
            } else if (dxxxxxx == 61) {
               tok = new Token(287);
            } else {
               this.unread(dxxxxxx);
            }
            break;
         case 45:
            int dxxxxx = this.read();
            if (dxxxxx == 45) {
               tok = new Token(262);
            } else if (dxxxxx == 61) {
               tok = new Token(293);
            } else if (dxxxxx == 62) {
               tok = new Token(258);
            } else {
               this.unread(dxxxxx);
            }
            break;
         case 46:
            int d = this.read();
            if (d == 46) {
               tok = this.cond('.', 264, 288);
            } else {
               this.unread(d);
            }

            if (Character.isDigit(d)) {
               this.unread(46);
               tok = this.number();
            }
            break;
         case 47:
            int dxxxx = this.read();
            if (dxxxx == 42) {
               tok = this.ccomment();
            } else if (dxxxx == 47) {
               tok = this.cppcomment();
            } else if (dxxxx == 61) {
               tok = new Token(263);
            } else {
               this.unread(dxxxx);
            }
            break;
         case 58:
            int dxxxxxxxx = this.read();
            if (this.digraphs && dxxxxxxxx == 62) {
               tok = new Token(93);
            } else {
               this.unread(dxxxxxxxx);
            }
            break;
         case 60:
            if (this.include) {
               tok = this.string('<', '>');
            } else {
               int dxxxxxxx = this.read();
               if (dxxxxxxx == 61) {
                  tok = new Token(275);
               } else if (dxxxxxxx == 60) {
                  tok = this.cond('=', 280, 279);
               } else if (this.digraphs && dxxxxxxx == 58) {
                  tok = new Token(91);
               } else if (this.digraphs && dxxxxxxx == 37) {
                  tok = new Token(123);
               } else {
                  this.unread(dxxxxxxx);
               }
            }
            break;
         case 61:
            tok = this.cond('=', 266, 61);
            break;
         case 62:
            int dxxx = this.read();
            if (dxxx == 61) {
               tok = new Token(267);
            } else if (dxxx == 62) {
               tok = this.cond('=', 290, 289);
            } else {
               this.unread(dxxx);
            }
            break;
         case 94:
            tok = this.cond('=', 295, 94);
            break;
         case 124:
            int dxx = this.read();
            if (dxx == 61) {
               tok = new Token(285);
            } else if (dxx == 124) {
               tok = this.cond('=', 278, 277);
            } else {
               this.unread(dxx);
            }
      }

      if (tok == null) {
         if (Character.isWhitespace(c)) {
            tok = this.whitespace(c);
         } else if (Character.isDigit(c)) {
            this.unread(c);
            tok = this.number();
         } else if (Character.isJavaIdentifierStart(c)) {
            tok = this.identifier(c);
         } else {
            String text = TokenType.getTokenText(c);
            if (text == null) {
               if (c >>> 16 == 0) {
                  text = Character.toString((char)c);
               } else {
                  text = new String(Character.toChars(c));
               }
            }

            tok = new Token(c, text);
         }
      }

      if (this.bol) {
         switch (tok.getType()) {
            case 260:
            case 294:
               break;
            default:
               this.bol = false;
         }
      }

      tok.setLocation(_l, _c);
      return tok;
   }

   @Override
   public void close() throws IOException {
      if (this.reader != null) {
         this.reader.close();
         this.reader = null;
      }

      super.close();
   }
}
