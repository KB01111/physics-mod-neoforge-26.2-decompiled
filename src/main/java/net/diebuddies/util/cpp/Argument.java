package net.diebuddies.util.cpp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jspecify.annotations.NonNull;

class Argument extends ArrayList<Token> {
   private List<Token> expansion = null;

   public Argument() {
   }

   public void addToken(@NonNull Token tok) {
      this.add(tok);
   }

   void expand(@NonNull Preprocessor p) throws IOException, LexerException {
      if (this.expansion == null) {
         this.expansion = p.expand(this);
      }
   }

   @NonNull
   public Iterator<Token> expansion() {
      return this.expansion.iterator();
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("Argument(");
      buf.append("raw=[ ");

      for (int i = 0; i < this.size(); i++) {
         buf.append(this.get(i).getText());
      }

      buf.append(" ];expansion=[ ");
      if (this.expansion == null) {
         buf.append("null");
      } else {
         for (Token token : this.expansion) {
            buf.append(token.getText());
         }
      }

      buf.append(" ])");
      return buf.toString();
   }
}
