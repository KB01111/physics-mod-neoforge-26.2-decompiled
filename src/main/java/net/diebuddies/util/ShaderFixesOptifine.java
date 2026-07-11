package net.diebuddies.util;

import net.optifine.util.LineBuffer;
import org.apache.commons.lang3.StringUtils;

public class ShaderFixesOptifine {
   public static LineBuffer applyOptifineFixes(LineBuffer lines, boolean onlyOptifineFix) {
      if (lines == null) {
         return null;
      } else {
         LineBuffer buffer = new LineBuffer();
         boolean hasColorModulator = false;

         for (String line : lines) {
            if (line.contains("colorModulator")) {
               hasColorModulator = true;
               break;
            }
         }

         for (String linex : lines) {
            if (!onlyOptifineFix) {
               linex = ShaderFixes.applyFixes(linex);
            }

            linex = ShaderFixes.applyAllFixes(linex);
            if (!hasColorModulator) {
               if (linex.contains("in vec4 vaColor;")) {
                  buffer.add(linex);
                  buffer.add("uniform vec4 colorModulator;");
                  buffer.add("#define PHYSICS_COLOR (vaColor * colorModulator)");
               } else {
                  linex = StringUtils.replace(linex, "vaColor", "PHYSICS_COLOR");
                  buffer.add(linex);
               }
            } else {
               buffer.add(linex);
            }
         }

         return buffer;
      }
   }
}
