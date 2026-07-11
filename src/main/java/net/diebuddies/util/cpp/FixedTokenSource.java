package net.diebuddies.util.cpp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class FixedTokenSource extends Source {
   private static final Token EOF = new Token(265, "<ts-eof>");
   private final List<Token> tokens;
   private int idx;

   FixedTokenSource(Token... tokens) {
      this.tokens = Arrays.asList(tokens);
      this.idx = 0;
   }

   FixedTokenSource(List<Token> tokens) {
      this.tokens = tokens;
      this.idx = 0;
   }

   @Override
   public Token token() throws IOException, LexerException {
      return this.idx >= this.tokens.size() ? EOF : this.tokens.get(this.idx++);
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("constant token stream ").append(this.tokens);
      Source parent = this.getParent();
      if (parent != null) {
         buf.append(" in ").append(String.valueOf(parent));
      }

      return buf.toString();
   }
}
