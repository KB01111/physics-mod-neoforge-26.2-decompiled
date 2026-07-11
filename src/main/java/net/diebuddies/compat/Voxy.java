package net.diebuddies.compat;

public class Voxy {
   private static ThreadLocal<Boolean> rendersVoxyBlock = ThreadLocal.withInitial(() -> Boolean.FALSE);

   public static void enableVoxyBlock() {
      rendersVoxyBlock.set(Boolean.TRUE);
   }

   public static void disableVoxyBlock() {
      rendersVoxyBlock.set(Boolean.FALSE);
   }

   public static boolean rendersVoxyBlock() {
      return rendersVoxyBlock.get();
   }
}
