package net.diebuddies.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class GLSLModifier {
   private static final Pattern BEFORE_FIRST_FUNCTION_PATTERN = Pattern.compile(
      "^(?!#version)\\s*(?<!_)\\b[a-zA-Z_][a-zA-Z0-9_]+\\s+[a-zA-Z_][a-zA-Z0-9_]+\\s*\\(", 8
   );
   private static final Pattern FUNCTION_START_PATTERN = Pattern.compile("(\\b\\w+\\b\\s*\\([^)]*\\)\\s*\\{)", 32);
   private static final Pattern FUNCTION_END_PATTERN = Pattern.compile("(\\w+\\s*\\(.*?\\)\\s*\\{.*?)(\\})", 32);
   private static final Pattern LAYOUT_DECL = Pattern.compile(
      "\\blayout\\s*\\(\\s*location\\s*=\\s*(\\d+)\\s*\\)\\s*(in|out)\\s+([a-zA-Z0-9_]+)\\s+([a-zA-Z0-9_]+)\\s*;"
   );

   public static String removeComments(String glslCode) {
      String regex = "/\\*.*?\\*/|//.*?\\n";
      Pattern pattern = Pattern.compile(regex, 32);
      Matcher matcher = pattern.matcher(glslCode);
      return matcher.replaceAll("");
   }

   public static String replaceFunctionContent(String glslCode, String functionName, String newContent) {
      String functionSignaturePattern = "(" + functionName + "\\s*\\(.*?\\)\\s*\\{)";
      Pattern pattern = Pattern.compile(functionSignaturePattern, 32);
      Matcher matcher = pattern.matcher(glslCode);
      if (!matcher.find()) {
         return glslCode;
      } else {
         String fullFunctionSignature = matcher.group(1);
         int start = matcher.start();
         int end = matcher.end();
         int braceCount = 1;

         for (int i = end; i < glslCode.length(); i++) {
            if (glslCode.charAt(i) == '{') {
               braceCount++;
            } else if (glslCode.charAt(i) == '}') {
               braceCount--;
            }

            if (braceCount == 0) {
               end = i + 1;
               break;
            }
         }

         String beforeFunction = glslCode.substring(0, start);
         String afterFunction = glslCode.substring(end);
         return beforeFunction + fullFunctionSignature + "\n" + newContent + "\n}" + afterFunction;
      }
   }

   public static String replaceFunctionCalls(String glslCode, String targetFunctionName, String replacement) {
      String regex = "\\b" + Pattern.quote(targetFunctionName) + "\\b\\s*\\((?:[^()]++|\\([^()]*+\\))*\\)(?!\\s*\\{)";
      return glslCode.replaceAll(regex, replacement);
   }

   public static boolean hasFunction(String glslCode, String functionName) {
      String regex = "(" + Pattern.quote(functionName) + "\\s*\\(.*?\\)\\s*\\{)(.*?)(\\})";
      Pattern pattern = Pattern.compile(regex, 32);
      Matcher matcher = pattern.matcher(glslCode);
      return matcher.find();
   }

   public static String insertBeforeFirstFunction(String glslCode, String content) {
      Matcher matcher = BEFORE_FIRST_FUNCTION_PATTERN.matcher(glslCode);
      if (matcher.find()) {
         glslCode = new StringBuilder(glslCode).insert(matcher.start(), content + "\n").toString();
      }

      return glslCode;
   }

   public static String insertAtFunctionStart(String glslCode, String functionName, String content) {
      Matcher matcher = FUNCTION_START_PATTERN.matcher(glslCode);

      while (matcher.find()) {
         if (matcher.group().startsWith(functionName)) {
            glslCode = new StringBuilder(glslCode).insert(matcher.end(), "\n" + content + "\n").toString();
            break;
         }
      }

      return glslCode;
   }

   public static String insertAtFunctionEnd(String glslCode, String functionName, String content) {
      Matcher matcher = FUNCTION_START_PATTERN.matcher(glslCode);

      while (matcher.find()) {
         if (matcher.group().startsWith(functionName)) {
            int functionStart = matcher.end();
            int braceCount = 1;

            int i;
            for (i = functionStart; i < glslCode.length() && braceCount > 0; i++) {
               if (glslCode.charAt(i) == '{') {
                  braceCount++;
               } else if (glslCode.charAt(i) == '}') {
                  braceCount--;
               }
            }

            if (braceCount == 0) {
               glslCode = new StringBuilder(glslCode).insert(i - 1, "\n" + content + "\n").toString();
            }
            break;
         }
      }

      return glslCode;
   }

   public static Map<Integer, GLSLModifier.VarInfo> findOutputNames(String glslCode) {
      Map<Integer, GLSLModifier.VarInfo> outputs = new HashMap<>();
      Matcher m = LAYOUT_DECL.matcher(glslCode);

      while (m.find()) {
         int location = Integer.parseInt(m.group(1));
         String qualifier = m.group(2);
         String glslType = m.group(3);
         String varName = m.group(4);
         outputs.put(location, new GLSLModifier.VarInfo(qualifier, glslType, varName));
      }

      return outputs;
   }

   public static String replaceVariableReferences(String glslCode, String variableName, String replacement) {
      StringBuilder result = new StringBuilder();
      int bracketCount = 0;
      int lastIndex = 0;

      for (int i = 0; i < glslCode.length(); i++) {
         char c = glslCode.charAt(i);
         if (c == '{') {
            if (++bracketCount == 1) {
               result.append(glslCode, lastIndex, i + 1);
               lastIndex = i + 1;
            }
         } else if (c == '}') {
            if (--bracketCount == 0) {
               String functionContent = glslCode.substring(lastIndex, i);
               functionContent = functionContent.replaceAll("(?<![\\w\\.])" + variableName + "(?!\\s*=|\\w)", replacement);
               result.append(functionContent);
               result.append(c);
               lastIndex = i + 1;
            }
         }
      }

      result.append(glslCode.substring(lastIndex));
      return result.toString();
   }

   public static String replaceVariableReferencesWithinFunction(String glslCode, String functionName, String variableName, String replacement) {
      int functionStart = glslCode.indexOf(functionName);
      if (functionStart != -1) {
         int braceCount = 0;
         int contentStart = -1;
         int contentEnd = -1;

         for (int i = functionStart; i < glslCode.length(); i++) {
            char c = glslCode.charAt(i);
            if (c == '{') {
               if (braceCount == 0) {
                  contentStart = i + 1;
               }

               braceCount++;
            } else if (c == '}') {
               if (--braceCount == 0) {
                  contentEnd = i;
                  break;
               }
            }
         }

         if (contentStart != -1 && contentEnd != -1) {
            String functionContent = glslCode.substring(contentStart, contentEnd);
            functionContent = functionContent.replaceAll("(?<![\\w\\.])" + variableName + "(?!\\s*=|\\w)", replacement);
            glslCode = glslCode.substring(0, contentStart) + functionContent + glslCode.substring(contentEnd);
         }
      }

      return glslCode;
   }

   public static String replaceWithinFunctionContent(String glslCode, String functionName, String search, String replace) {
      int functionStart = glslCode.indexOf(functionName);
      if (functionStart != -1) {
         int braceCount = 0;
         int contentStart = -1;
         int contentEnd = -1;

         for (int i = functionStart; i < glslCode.length(); i++) {
            char c = glslCode.charAt(i);
            if (c == '{') {
               if (braceCount == 0) {
                  contentStart = i + 1;
               }

               braceCount++;
            } else if (c == '}') {
               if (--braceCount == 0) {
                  contentEnd = i;
                  break;
               }
            }
         }

         if (contentStart != -1 && contentEnd != -1) {
            String functionContent = glslCode.substring(contentStart, contentEnd);
            String modifiedContent = StringUtils.replace(functionContent, search, replace);
            glslCode = glslCode.substring(0, contentStart) + modifiedContent + glslCode.substring(contentEnd);
         }
      }

      return glslCode;
   }

   public static String convertToString(List<String> oceanInjection) {
      StringBuilder sb = new StringBuilder();

      for (String line : oceanInjection) {
         sb.append(line).append("\n");
      }

      return sb.toString();
   }

   public static record VarInfo(String qualifier, String glslType, String varName) {
   }
}
