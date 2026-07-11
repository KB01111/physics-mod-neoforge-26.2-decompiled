package net.diebuddies.util.cpp;

import java.io.IOException;
import java.util.List;

@Deprecated
class TokenSnifferSource extends Source {
   private final List<Token> target;

   TokenSnifferSource(List<Token> target) {
      this.target = target;
   }

   @Override
   public Token token() throws IOException, LexerException {
      Token tok = this.getParent().token();
      if (tok.getType() != 265) {
         this.target.add(tok);
      }

      return tok;
   }

   @Override
   public String toString() {
      return this.getParent().toString();
   }
}
