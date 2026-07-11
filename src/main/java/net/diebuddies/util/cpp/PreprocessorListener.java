package net.diebuddies.util.cpp;

import org.jspecify.annotations.NonNull;

public interface PreprocessorListener {
   void handleWarning(@NonNull Source var1, int var2, int var3, @NonNull String var4) throws LexerException;

   void handleError(@NonNull Source var1, int var2, int var3, @NonNull String var4) throws LexerException;

   void handleSourceChange(@NonNull Source var1, @NonNull SourceChangeEvent var2);

   public static enum SourceChangeEvent {
      SUSPEND,
      PUSH,
      POP,
      RESUME;
   }
}
