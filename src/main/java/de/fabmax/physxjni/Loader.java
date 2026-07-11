package de.fabmax.physxjni;

import java.util.concurrent.atomic.AtomicBoolean;
import physx.PlatformChecks;

public class Loader {
   private static final String version = "2.5.1";
   private static final AtomicBoolean isLoaded = new AtomicBoolean(false);

   public static void load() {
      if (!isLoaded.getAndSet(true)) {
         Platform platform = Platform.getPlatform();

         try {
            switch (platform) {
               case LINUX:
                  PlatformChecks.setPlatformBit(2);
                  break;
               case WINDOWS:
                  PlatformChecks.setPlatformBit(1);
                  break;
               case MACOS:
               case MACOS_ARM64:
                  PlatformChecks.setPlatformBit(4);
            }

            NativeLib lib = platform.getLib();
            if (!"2.5.1".equals(lib.getVersion())) {
               throw new IllegalStateException("Native lib version " + lib.getVersion() + " differs from main version 2.5.1");
            }

            lib.load();
         } catch (Throwable var2) {
            throw new IllegalStateException("Failed loading native PhysX libraries for platform " + platform, var2);
         }
      }
   }
}
