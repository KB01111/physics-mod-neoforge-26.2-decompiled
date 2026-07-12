package net.diebuddies.debug;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class AgentDebugLog {
   private static final Path LOG_PATH = Path.of("debug-6bf9b5.log");

   private AgentDebugLog() {
   }

   public static void log(String hypothesisId, String location, String message, String dataJson) {
      long ts = System.currentTimeMillis();
      String line = "{\"sessionId\":\"6bf9b5\",\"hypothesisId\":\""
         + hypothesisId
         + "\",\"location\":\""
         + location
         + "\",\"message\":\""
         + message
         + "\",\"data\":"
         + dataJson
         + ",\"timestamp\":"
         + ts
         + "}\n";

      try {
         Files.writeString(LOG_PATH, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
      } catch (Exception ignored) {
      }
   }
}