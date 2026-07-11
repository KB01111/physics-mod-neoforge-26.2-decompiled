package net.diebuddies.util.cpp;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jspecify.annotations.NonNull;

public class SourceIterator implements Iterator<Token> {
   private final Source source;
   private Token tok;

   public SourceIterator(@NonNull Source s) {
      this.source = s;
      this.tok = null;
   }

   private void advance() {
      try {
         if (this.tok == null) {
            this.tok = this.source.token();
         }
      } catch (LexerException var2) {
         throw new IllegalStateException(var2);
      } catch (IOException var3) {
         throw new IllegalStateException(var3);
      }
   }

   @Override
   public boolean hasNext() {
      this.advance();
      return this.tok.getType() != 265;
   }

   public Token next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         Token t = this.tok;
         this.tok = null;
         return t;
      }
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException();
   }
}
