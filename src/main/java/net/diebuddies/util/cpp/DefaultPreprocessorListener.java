package net.diebuddies.util.cpp;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPreprocessorListener implements PreprocessorListener {
   private static final Logger LOG = LoggerFactory.getLogger(DefaultPreprocessorListener.class);
   private int errors;
   private int warnings;

   public DefaultPreprocessorListener() {
      this.clear();
   }

   public void clear() {
      this.errors = 0;
      this.warnings = 0;
   }

   public int getErrors() {
      return this.errors;
   }

   public int getWarnings() {
      return this.warnings;
   }

   protected void print(@NonNull String msg) {
      LOG.info(msg);
   }

   @Override
   public void handleWarning(Source source, int line, int column, String msg) throws LexerException {
      this.warnings++;
      this.print(source.getName() + ":" + line + ":" + column + ": warning: " + msg);
   }

   @Override
   public void handleError(Source source, int line, int column, String msg) throws LexerException {
      this.errors++;
      this.print(source.getName() + ":" + line + ":" + column + ": error: " + msg);
   }

   @Override
   public void handleSourceChange(Source source, PreprocessorListener.SourceChangeEvent event) {
   }
}
