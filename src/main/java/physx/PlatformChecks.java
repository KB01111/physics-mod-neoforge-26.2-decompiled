package physx;

public class PlatformChecks {
   public static final int PLATFORM_WINDOWS = 1;
   public static final int PLATFORM_LINUX = 2;
   public static final int PLATFORM_MACOS = 4;
   public static final int PLATFORM_ANDROID = 8;
   public static final int PLATFORM_OTHER = Integer.MIN_VALUE;
   private static int platformBit = Integer.MIN_VALUE;

   protected PlatformChecks() {
   }

   public static void setPlatformBit(int platformBit) {
      PlatformChecks.platformBit = platformBit;
   }

   public static void requirePlatform(int supportedPlatforms, String name) {
      if ((supportedPlatforms & platformBit) == 0) {
         throw new RuntimeException(
            name
               + " is not supported on this platform. If you think this is a mistake, make sure the correct platform is set by calling PlatformChecks.setPlatformBit()."
         );
      }
   }
}
