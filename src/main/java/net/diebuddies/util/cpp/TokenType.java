package net.diebuddies.util.cpp;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.NonNull;

class TokenType {
   private static final List<TokenType> TYPES = new ArrayList<>();
   private final String name;
   private final String text;

   private static void addTokenType(int type, @NonNull String name, String text) {
      while (TYPES.size() <= type) {
         TYPES.add(null);
      }

      TYPES.set(type, new TokenType(name, text));
   }

   private static void addTokenType(int type, @NonNull String name) {
      addTokenType(type, name, null);
   }

   public static TokenType getTokenType(int type) {
      try {
         return TYPES.get(type);
      } catch (IndexOutOfBoundsException var2) {
         return null;
      }
   }

   @NonNull
   public static String getTokenName(int type) {
      if (type < 0) {
         return "Invalid" + type;
      } else {
         TokenType tokenType = getTokenType(type);
         return tokenType == null ? "Unknown" + type : tokenType.getName();
      }
   }

   public static String getTokenText(int type) {
      TokenType tokenType = getTokenType(type);
      return tokenType == null ? null : tokenType.getText();
   }

   TokenType(@NonNull String name, String text) {
      this.name = name;
      this.text = text;
   }

   @NonNull
   public String getName() {
      return this.name;
   }

   public String getText() {
      return this.text;
   }

   static {
      for (int i = 0; i < 255; i++) {
         String text = String.valueOf((char)i);
         addTokenType(i, text, text);
      }

      addTokenType(257, "AND_EQ", "&=");
      addTokenType(258, "ARROW", "->");
      addTokenType(259, "CHARACTER");
      addTokenType(260, "CCOMMENT");
      addTokenType(261, "CPPCOMMENT");
      addTokenType(262, "DEC", "--");
      addTokenType(263, "DIV_EQ", "/=");
      addTokenType(264, "ELLIPSIS", "...");
      addTokenType(265, "EOF");
      addTokenType(266, "EQ", "==");
      addTokenType(267, "GE", ">=");
      addTokenType(268, "HASH", "#");
      addTokenType(269, "HEADER");
      addTokenType(270, "IDENTIFIER");
      addTokenType(271, "INC", "++");
      addTokenType(272, "NUMBER");
      addTokenType(273, "LAND", "&&");
      addTokenType(274, "LAND_EQ", "&&=");
      addTokenType(275, "LE", "<=");
      addTokenType(276, "LITERAL");
      addTokenType(277, "LOR", "||");
      addTokenType(278, "LOR_EQ", "||=");
      addTokenType(279, "LSH", "<<");
      addTokenType(280, "LSH_EQ", "<<=");
      addTokenType(281, "MOD_EQ", "%=");
      addTokenType(282, "MULT_EQ", "*=");
      addTokenType(283, "NE", "!=");
      addTokenType(284, "NL");
      addTokenType(285, "OR_EQ", "|=");
      addTokenType(286, "PASTE", "##");
      addTokenType(287, "PLUS_EQ", "+=");
      addTokenType(288, "RANGE", "..");
      addTokenType(289, "RSH", ">>");
      addTokenType(290, "RSH_EQ", ">>=");
      addTokenType(291, "SQSTRING");
      addTokenType(292, "STRING");
      addTokenType(293, "SUB_EQ", "-=");
      addTokenType(294, "WHITESPACE");
      addTokenType(295, "XOR_EQ", "^=");
      addTokenType(296, "M_ARG");
      addTokenType(297, "M_PASTE");
      addTokenType(298, "M_STRING");
      addTokenType(299, "P_LINE");
      addTokenType(300, "INVALID");
   }
}
