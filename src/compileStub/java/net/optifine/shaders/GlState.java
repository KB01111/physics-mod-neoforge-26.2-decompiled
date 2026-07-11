package net.optifine.shaders;

public class GlState {
   public static ShadersFramebuffer activeFramebuffer;

   public static ShadersFramebuffer getFramebuffer() {
      return activeFramebuffer;
   }
}
