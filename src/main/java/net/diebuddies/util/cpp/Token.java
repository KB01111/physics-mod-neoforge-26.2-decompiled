package net.diebuddies.util.cpp;

import org.jspecify.annotations.NonNull;

public final class Token {
   private final int type;
   private int line;
   private int column;
   private final Object value;
   private final String text;
   public static final int AND_EQ = 257;
   public static final int ARROW = 258;
   public static final int CHARACTER = 259;
   public static final int CCOMMENT = 260;
   public static final int CPPCOMMENT = 261;
   public static final int DEC = 262;
   public static final int DIV_EQ = 263;
   public static final int ELLIPSIS = 264;
   public static final int EOF = 265;
   public static final int EQ = 266;
   public static final int GE = 267;
   public static final int HASH = 268;
   public static final int HEADER = 269;
   public static final int IDENTIFIER = 270;
   public static final int INC = 271;
   public static final int NUMBER = 272;
   public static final int LAND = 273;
   public static final int LAND_EQ = 274;
   public static final int LE = 275;
   public static final int LITERAL = 276;
   public static final int LOR = 277;
   public static final int LOR_EQ = 278;
   public static final int LSH = 279;
   public static final int LSH_EQ = 280;
   public static final int MOD_EQ = 281;
   public static final int MULT_EQ = 282;
   public static final int NE = 283;
   public static final int NL = 284;
   public static final int OR_EQ = 285;
   public static final int PASTE = 286;
   public static final int PLUS_EQ = 287;
   public static final int RANGE = 288;
   public static final int RSH = 289;
   public static final int RSH_EQ = 290;
   public static final int SQSTRING = 291;
   public static final int STRING = 292;
   public static final int SUB_EQ = 293;
   public static final int WHITESPACE = 294;
   public static final int XOR_EQ = 295;
   public static final int M_ARG = 296;
   public static final int M_PASTE = 297;
   public static final int M_STRING = 298;
   public static final int P_LINE = 299;
   public static final int INVALID = 300;
   static final Token space = new Token(294, -1, -1, " ");

   public Token(int type, int line, int column, String text, Object value) {
      this.type = type;
      this.line = line;
      this.column = column;
      this.text = text;
      this.value = value;
   }

   public Token(int type, int line, int column, String text) {
      this(type, line, column, text, null);
   }

   Token(int type, String text, Object value) {
      this(type, -1, -1, text, value);
   }

   Token(int type, String text) {
      this(type, text, null);
   }

   Token(int type) {
      this(type, TokenType.getTokenText(type));
   }

   public int getType() {
      return this.type;
   }

   void setLocation(int line, int column) {
      this.line = line;
      this.column = column;
   }

   public int getLine() {
      return this.line;
   }

   public int getColumn() {
      return this.column;
   }

   public String getText() {
      return this.text;
   }

   public Object getValue() {
      return this.value;
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append('[').append(getTokenName(this.type));
      if (this.line != -1) {
         buf.append('@').append(this.line);
         if (this.column != -1) {
            buf.append(',').append(this.column);
         }
      }

      buf.append("]:");
      if (this.text != null) {
         buf.append('"').append(this.text).append('"');
      } else if (this.type > 3 && this.type < 256) {
         buf.append((char)this.type);
      } else {
         buf.append('<').append(this.type).append('>');
      }

      if (this.value != null) {
         buf.append('=').append(this.value);
      }

      return buf.toString();
   }

   @NonNull
   public static String getTokenName(int type) {
      return TokenType.getTokenName(type);
   }
}
