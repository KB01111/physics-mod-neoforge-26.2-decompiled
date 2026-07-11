package net.diebuddies.util.cpp;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import org.jspecify.annotations.NonNull;

public abstract class Source implements Iterable<Token>, Closeable {
   private Source parent = null;
   private boolean autopop = false;
   private PreprocessorListener listener = null;
   private boolean active = true;
   private boolean werror = false;

   void setParent(Source parent, boolean autopop) {
      this.parent = parent;
      this.autopop = autopop;
   }

   final Source getParent() {
      return this.parent;
   }

   void init(Preprocessor pp) {
      this.setListener(pp.getListener());
      this.werror = pp.getWarnings().contains(Warning.ERROR);
   }

   public void setListener(PreprocessorListener pl) {
      this.listener = pl;
   }

   public String getPath() {
      Source parent = this.getParent();
      return parent != null ? parent.getPath() : null;
   }

   public String getName() {
      Source parent = this.getParent();
      return parent != null ? parent.getName() : null;
   }

   public int getLine() {
      Source parent = this.getParent();
      return parent == null ? 0 : parent.getLine();
   }

   public int getColumn() {
      Source parent = this.getParent();
      return parent == null ? 0 : parent.getColumn();
   }

   boolean isExpanding(@NonNull Macro m) {
      Source parent = this.getParent();
      return parent != null ? parent.isExpanding(m) : false;
   }

   boolean isAutopop() {
      return this.autopop;
   }

   boolean isNumbered() {
      return false;
   }

   void setActive(boolean b) {
      this.active = b;
   }

   boolean isActive() {
      return this.active;
   }

   @NonNull
   public abstract Token token() throws IOException, LexerException;

   @Override
   public Iterator<Token> iterator() {
      return new SourceIterator(this);
   }

   @NonNull
   public Token skipline(boolean white) throws IOException, LexerException {
      while (true) {
         Token tok = this.token();
         switch (tok.getType()) {
            case 260:
            case 261:
            case 294:
               break;
            case 265:
               this.warning(tok.getLine(), tok.getColumn(), "No newline before end of file");
               return new Token(284, tok.getLine(), tok.getColumn(), "\n");
            case 284:
               return tok;
            default:
               if (white) {
                  this.warning(tok.getLine(), tok.getColumn(), "Unexpected nonwhite token");
               }
         }
      }
   }

   protected void error(int line, int column, String msg) throws LexerException {
      if (this.listener != null) {
         this.listener.handleError(this, line, column, msg);
      } else {
         throw new LexerException("Error at " + line + ":" + column + ": " + msg);
      }
   }

   protected void warning(int line, int column, String msg) throws LexerException {
      if (this.werror) {
         this.error(line, column, msg);
      } else {
         if (this.listener == null) {
            throw new LexerException("Warning at " + line + ":" + column + ": " + msg);
         }

         this.listener.handleWarning(this, line, column, msg);
      }
   }

   @Override
   public void close() throws IOException {
   }
}
