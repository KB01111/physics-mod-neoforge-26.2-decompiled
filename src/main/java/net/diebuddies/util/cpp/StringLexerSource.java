package net.diebuddies.util.cpp;

import java.io.StringReader;

public class StringLexerSource extends LexerSource {
   public StringLexerSource(String string, boolean ppvalid) {
      super(new StringReader(string), ppvalid);
   }

   public StringLexerSource(String string) {
      this(string, false);
   }

   @Override
   public String toString() {
      return "string literal";
   }
}
