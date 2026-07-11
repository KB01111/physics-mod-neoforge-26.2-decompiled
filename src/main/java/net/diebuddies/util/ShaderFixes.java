package net.diebuddies.util;

import java.io.IOException;
import net.diebuddies.util.cpp.DefaultPreprocessorListener;
import net.diebuddies.util.cpp.LexerException;
import net.diebuddies.util.cpp.Preprocessor;
import net.diebuddies.util.cpp.Source;
import net.diebuddies.util.cpp.StringLexerSource;
import net.diebuddies.util.cpp.Token;
import org.apache.commons.lang3.StringUtils;

public class ShaderFixes {
   public static String applyFixes(String shaderSource) {
      if (!shaderSource.contains("gl_TextureMatrix[0]")) {
         shaderSource = StringUtils.replace(shaderSource, "gl_MultiTexCoord0", "(gl_TextureMatrix[0] * gl_MultiTexCoord0)");
      }

      return StringUtils.replace(shaderSource, "vec3 normalM = VdotN > 0.0 ? -normal : normal;", "vec3 normalM = normal;");
   }

   public static String applyAllFixes(String shaderSource) {
      shaderSource = StringUtils.replace(shaderSource, "flat varying mat3 tbn;", "varying mat3 tbn;");
      shaderSource = StringUtils.replace(shaderSource, "flat out vec3 normal;", "out vec3 normal;");
      shaderSource = StringUtils.replace(shaderSource, "flat in vec3 normal;", "in vec3 normal;");
      shaderSource = StringUtils.replace(shaderSource, "flat out mat3 tbn;", "out mat3 tbn;");
      return StringUtils.replace(shaderSource, "flat in mat3 tbn;", "in mat3 tbn;");
   }

   public static String preprocessOptifineSource(String shaderSource) {
      String content = StringUtils.replace(shaderSource, "#version", "#warning TMP_VERSION");
      content = StringUtils.replace(content, "#extension", "#warning TMP_EXTENSION");

      try {
         String e;
         try (Preprocessor processor = new Preprocessor(new StringLexerSource(content, true))) {
            ShaderFixes.IgnorePreprocessorListener listener = new ShaderFixes.IgnorePreprocessorListener();
            processor.setListener(listener);
            StringBuilder builder = new StringBuilder();
            Token token = null;

            try {
               while ((token = processor.token()) != null && token.getType() != 265) {
                  builder.append(token.getText());
               }
            } catch (LexerException var8) {
               var8.printStackTrace();
            }

            processor.close();
            e = listener.getIgnoredTokens() + builder.toString();
         }

         return e;
      } catch (IOException var10) {
         var10.printStackTrace();
         return shaderSource;
      }
   }

   private static class IgnorePreprocessorListener extends DefaultPreprocessorListener {
      private StringBuilder sb = new StringBuilder();

      @Override
      public void handleWarning(Source source, int line, int column, String msg) throws LexerException {
         if (msg.contains("TMP_VERSION")) {
            this.sb.append(StringUtils.replace(msg, "#warning TMP_VERSION", "#version")).append("\n");
         } else if (msg.contains("TMP_EXTENSION")) {
            this.sb.append(StringUtils.replace(msg, "#warning TMP_EXTENSION", "#extension")).append("\n");
         } else {
            super.handleWarning(source, line, column, msg);
         }
      }

      public String getIgnoredTokens() {
         return this.sb.toString();
      }
   }
}
