package net.diebuddies.util.cpp;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MacroTokenSource extends Source {
   private static final Logger LOG = LoggerFactory.getLogger(MacroTokenSource.class);
   private final Macro macro;
   private final Iterator<Token> tokens;
   private final List<Argument> args;
   private Iterator<Token> arg;

   MacroTokenSource(@NonNull Macro m, @NonNull List<Argument> args) {
      this.macro = m;
      this.tokens = m.getTokens().iterator();
      this.args = args;
      this.arg = null;
   }

   @Override
   boolean isExpanding(@NonNull Macro m) {
      return this.macro == m ? true : super.isExpanding(m);
   }

   static void escape(@NonNull StringBuilder buf, @NonNull CharSequence cs) {
      if (buf == null) {
         throw new NullPointerException("Buffer was null.");
      } else if (cs == null) {
         throw new NullPointerException("CharSequence was null.");
      } else {
         for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);
            switch (c) {
               case '\n':
                  buf.append("\\n");
                  break;
               case '\r':
                  buf.append("\\r");
                  break;
               case '"':
                  buf.append("\\\"");
                  break;
               case '\\':
                  buf.append("\\\\");
                  break;
               default:
                  buf.append(c);
            }
         }
      }
   }

   private void concat(@NonNull StringBuilder buf, @NonNull Argument arg) {
      for (Token tok : arg) {
         buf.append(tok.getText());
      }
   }

   @NonNull
   private Token stringify(@NonNull Token pos, @NonNull Argument arg) {
      StringBuilder buf = new StringBuilder();
      this.concat(buf, arg);
      StringBuilder str = new StringBuilder("\"");
      escape(str, buf);
      str.append("\"");
      return new Token(292, pos.getLine(), pos.getColumn(), str.toString(), buf.toString());
   }

   private boolean isVariadicArgument(int argumentIndex) {
      return !this.macro.isVariadic() ? false : argumentIndex == this.args.size() - 1;
   }

   private void paste(@NonNull Token ptok) throws IOException, LexerException {
      StringBuilder buf = new StringBuilder();
      int count = 2;
      boolean comma = false;

      for (int i = 0; i < count; i++) {
         if (!this.tokens.hasNext()) {
            this.error(ptok.getLine(), ptok.getColumn(), "Paste at end of expansion");
            buf.append(' ').append(ptok.getText());
            break;
         }

         Token tok = this.tokens.next();
         switch (tok.getType()) {
            case 44:
               comma = true;
               buf.append(tok.getText());
               continue;
            case 260:
            case 261:
               break;
            case 296:
               int idx = (Integer)tok.getValue();
               Argument arg = this.args.get(idx);
               if (comma && this.isVariadicArgument(idx) && arg.isEmpty()) {
                  buf.setLength(buf.length() - 1);
               } else {
                  this.concat(buf, arg);
               }
               break;
            case 297:
               count += 2;
               ptok = tok;
               break;
            default:
               buf.append(tok.getText());
         }

         comma = false;
      }

      StringLexerSource sl = new StringLexerSource(buf.toString());
      this.arg = new SourceIterator(sl);
   }

   @Override
   public Token token() throws IOException, LexerException {
      while (true) {
         if (this.arg != null) {
            if (this.arg.hasNext()) {
               Token tok = this.arg.next();

               assert tok.getType() != 297 : "Unexpected paste token";

               return tok;
            }

            this.arg = null;
         }

         if (!this.tokens.hasNext()) {
            return new Token(265, -1, -1, "");
         }

         Token tok = this.tokens.next();
         switch (tok.getType()) {
            case 296: {
               int idx = (Integer)tok.getValue();
               this.arg = this.args.get(idx).expansion();
               break;
            }
            case 297:
               this.paste(tok);
               break;
            case 298: {
               int idx = (Integer)tok.getValue();
               return this.stringify(tok, this.args.get(idx));
            }
            default:
               return tok;
         }
      }
   }

   @Override
   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("expansion of ").append(this.macro.getName());
      Source parent = this.getParent();
      if (parent != null) {
         buf.append(" in ").append(String.valueOf(parent));
      }

      return buf.toString();
   }
}
