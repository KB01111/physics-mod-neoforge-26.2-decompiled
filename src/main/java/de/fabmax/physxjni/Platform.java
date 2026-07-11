package de.fabmax.physxjni;

public enum Platform {
   LINUX("de.fabmax.physxjni.linux.NativeLibLinux"),
   WINDOWS("de.fabmax.physxjni.windows.NativeLibWindows"),
   MACOS("de.fabmax.physxjni.macos.NativeLibMacos"),
   MACOS_ARM64("de.fabmax.physxjni.macosarm.NativeLibMacosArm64");

   private final String metaClassName;

   private Platform(String metaClassName) {
      this.metaClassName = metaClassName;
   }

   public NativeLib getLib() throws ReflectiveOperationException {
      Class<?> libImpl = Loader.class.getClassLoader().loadClass(this.metaClassName);
      return (NativeLib)libImpl.getConstructor().newInstance();
   }

   public static Platform getPlatform() {
      String vendor = System.getProperty("java.vendor", "unknown").toLowerCase();
      String osName = System.getProperty("os.name", "unknown").toLowerCase();
      String arch = System.getProperty("os.arch", "unknown");
      if (vendor.contains("android")) {
         throw new IllegalStateException("Android environment detected. Use 'physx-jni-android' library instead of regular 'physx-jni'");
      } else if (osName.contains("windows")) {
         return WINDOWS;
      } else if (osName.contains("linux")) {
         return LINUX;
      } else if (!osName.contains("mac os x") && !osName.contains("darwin") && !osName.contains("osx")) {
         throw new IllegalStateException("Unsupported OS: " + osName);
      } else {
         return "aarch64".equals(arch) ? MACOS_ARM64 : MACOS;
      }
   }
}
