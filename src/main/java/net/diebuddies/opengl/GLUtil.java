package net.diebuddies.opengl;

import java.io.PrintStream;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.AMDDebugOutput;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageAMDCallback;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.KHRDebug;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.Callback;

public class GLUtil {
   public static Callback setupDebugMessageCallback() {
      return setupDebugMessageCallback(APIUtil.DEBUG_STREAM);
   }

   private static void trace(Consumer<String> output) {
      StackTraceElement[] elems = filterStackTrace(new Throwable(), 4).getStackTrace();

      for (int i = 0; i < elems.length; i++) {
         StackTraceElement ste = elems[i];
         output.accept(ste.toString());
      }
   }

   private static void printTrace(final PrintStream stream) {
      trace(new Consumer<String>() {
         boolean first = true;

         public void accept(String str) {
            if (this.first) {
               GLUtil.printDetail(stream, "Stacktrace", str);
               this.first = false;
            } else {
               GLUtil.printDetailLine(stream, "Stacktrace", str);
            }
         }
      });
   }

   public static Callback setupDebugMessageCallback(PrintStream stream) {
      GLCapabilities caps = GL.getCapabilities();
      if (caps.OpenGL43) {
         APIUtil.apiLog("[GL] Using OpenGL 4.3 for error logging.");
         GLDebugMessageCallback proc = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
            String level;
            if (severity == 33387 || severity == 37192) {
               level = "info ";
            } else if (severity == 37191) {
               level = "warn ";
            } else {
               level = "error";
            }

            stream.println("[" + level + "] OpenGL debug message");
            printDetail(stream, "ID", String.format("0x%X", id));
            printDetail(stream, "Source", getDebugSource(source));
            printDetail(stream, "Type", getDebugType(type));
            printDetail(stream, "Severity", getDebugSeverity(severity));
            printDetail(stream, "Message", GLDebugMessageCallback.getMessage(length, message));
            printTrace(stream);
         });
         GL43.glDebugMessageCallback(proc, 0L);
         if ((GL11.glGetInteger(33310) & 2) == 0) {
            APIUtil.apiLog("[GL] Warning: A non-debug context may not produce any debug output.");
            GL11.glEnable(37600);
         }

         GL11.glEnable(33346);
         return proc;
      } else if (caps.GL_KHR_debug) {
         APIUtil.apiLog("[GL] Using KHR_debug for error logging.");
         GLDebugMessageCallback proc = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
            String level;
            if (severity == 33387 || severity == 37192) {
               level = "info ";
            } else if (severity == 37191) {
               level = "warn ";
            } else {
               level = "error";
            }

            stream.println("[" + level + "] OpenGL debug message");
            printDetail(stream, "ID", String.format("0x%X", id));
            printDetail(stream, "Source", getDebugSource(source));
            printDetail(stream, "Type", getDebugType(type));
            printDetail(stream, "Severity", getDebugSeverity(severity));
            printDetail(stream, "Message", GLDebugMessageCallback.getMessage(length, message));
            printTrace(stream);
         });
         KHRDebug.glDebugMessageCallback(proc, 0L);
         if (caps.OpenGL30 && (GL11.glGetInteger(33310) & 2) == 0) {
            APIUtil.apiLog("[GL] Warning: A non-debug context may not produce any debug output.");
            GL11.glEnable(37600);
         }

         GL11.glEnable(33346);
         return proc;
      } else if (caps.GL_ARB_debug_output) {
         APIUtil.apiLog("[GL] Using ARB_debug_output for error logging.");
         GLDebugMessageARBCallback proc = GLDebugMessageARBCallback.create((source, type, id, severity, length, message, userParam) -> {
            String level;
            if (severity == 37192) {
               level = "info ";
            } else if (severity == 37191) {
               level = "warn ";
            } else {
               level = "error";
            }

            stream.println("[" + level + "] OpenGL debug message");
            printDetail(stream, "ID", String.format("0x%X", id));
            printDetail(stream, "Source", getSourceARB(source));
            printDetail(stream, "Type", getTypeARB(type));
            printDetail(stream, "Severity", getSeverityARB(severity));
            printDetail(stream, "Message", GLDebugMessageARBCallback.getMessage(length, message));
            printTrace(stream);
         });
         ARBDebugOutput.glDebugMessageCallbackARB(proc, 0L);
         GL11.glEnable(33346);
         return proc;
      } else if (caps.GL_AMD_debug_output) {
         APIUtil.apiLog("[GL] Using AMD_debug_output for error logging.");
         GLDebugMessageAMDCallback proc = GLDebugMessageAMDCallback.create((id, category, severity, length, message, userParam) -> {
            String level;
            if (severity == 37192) {
               level = "info ";
            } else if (severity == 37191) {
               level = "warn ";
            } else {
               level = "error";
            }

            stream.println("[" + level + "] OpenGL debug message");
            printDetail(stream, "ID", String.format("0x%X", id));
            printDetail(stream, "Category", getCategoryAMD(category));
            printDetail(stream, "Severity", getSeverityAMD(severity));
            printDetail(stream, "Message", GLDebugMessageAMDCallback.getMessage(length, message));
            printTrace(stream);
         });
         AMDDebugOutput.glDebugMessageCallbackAMD(proc, 0L);
         return proc;
      } else {
         APIUtil.apiLog("[GL] No debug output implementation is available.");
         return null;
      }
   }

   private static void printDetail(PrintStream stream, String type, String message) {
      LogManager.getLogger().error(String.format("  %s: %s\n", type, message));
   }

   private static void printDetailLine(PrintStream stream, String type, String message) {
      String indent = "    ";

      for (int i = 0; i < type.length(); i++) {
         indent = indent + " ";
      }

      LogManager.getLogger().error(indent + message);
   }

   private static String getDebugSource(int source) {
      switch (source) {
         case 33350:
            return "API";
         case 33351:
            return "WINDOW SYSTEM";
         case 33352:
            return "SHADER COMPILER";
         case 33353:
            return "THIRD PARTY";
         case 33354:
            return "APPLICATION";
         case 33355:
            return "OTHER";
         default:
            return APIUtil.apiUnknownToken(source);
      }
   }

   private static String getDebugType(int type) {
      switch (type) {
         case 33356:
            return "ERROR";
         case 33357:
            return "DEPRECATED BEHAVIOR";
         case 33358:
            return "UNDEFINED BEHAVIOR";
         case 33359:
            return "PORTABILITY";
         case 33360:
            return "PERFORMANCE";
         case 33361:
            return "OTHER";
         case 33384:
            return "MARKER";
         default:
            return APIUtil.apiUnknownToken(type);
      }
   }

   private static String getDebugSeverity(int severity) {
      switch (severity) {
         case 33387:
            return "NOTIFICATION";
         case 37190:
            return "HIGH";
         case 37191:
            return "MEDIUM";
         case 37192:
            return "LOW";
         default:
            return APIUtil.apiUnknownToken(severity);
      }
   }

   private static String getSourceARB(int source) {
      switch (source) {
         case 33350:
            return "API";
         case 33351:
            return "WINDOW SYSTEM";
         case 33352:
            return "SHADER COMPILER";
         case 33353:
            return "THIRD PARTY";
         case 33354:
            return "APPLICATION";
         case 33355:
            return "OTHER";
         default:
            return APIUtil.apiUnknownToken(source);
      }
   }

   private static String getTypeARB(int type) {
      switch (type) {
         case 33356:
            return "ERROR";
         case 33357:
            return "DEPRECATED BEHAVIOR";
         case 33358:
            return "UNDEFINED BEHAVIOR";
         case 33359:
            return "PORTABILITY";
         case 33360:
            return "PERFORMANCE";
         case 33361:
            return "OTHER";
         default:
            return APIUtil.apiUnknownToken(type);
      }
   }

   private static String getSeverityARB(int severity) {
      switch (severity) {
         case 37190:
            return "HIGH";
         case 37191:
            return "MEDIUM";
         case 37192:
            return "LOW";
         default:
            return APIUtil.apiUnknownToken(severity);
      }
   }

   private static String getCategoryAMD(int category) {
      switch (category) {
         case 37193:
            return "API ERROR";
         case 37194:
            return "WINDOW SYSTEM";
         case 37195:
            return "DEPRECATION";
         case 37196:
            return "UNDEFINED BEHAVIOR";
         case 37197:
            return "PERFORMANCE";
         case 37198:
            return "SHADER COMPILER";
         case 37199:
            return "APPLICATION";
         case 37200:
            return "OTHER";
         default:
            return APIUtil.apiUnknownToken(category);
      }
   }

   private static String getSeverityAMD(int severity) {
      switch (severity) {
         case 37190:
            return "HIGH";
         case 37191:
            return "MEDIUM";
         case 37192:
            return "LOW";
         default:
            return APIUtil.apiUnknownToken(severity);
      }
   }

   public static <T extends Throwable> T filterStackTrace(T t, int offset) {
      StackTraceElement[] elems = t.getStackTrace();
      StackTraceElement[] filtered = new StackTraceElement[elems.length];
      int j = 0;

      for (int i = offset; i < elems.length; i++) {
         String className = elems[i].getClassName();
         if (className == null) {
            className = "";
         }

         if (!className.startsWith("org.lwjglx.") || className.startsWith("org.lwjglx.debug.opengl") || className.startsWith("org.lwjglx.debug.glfw")) {
            filtered[j++] = elems[i];
         }
      }

      StackTraceElement[] newElems = new StackTraceElement[j];
      System.arraycopy(filtered, 0, newElems, 0, j);
      t.setStackTrace(newElems);
      return t;
   }
}
