package net.optifine.shaders;

public class Programs {
   private Program makeGbuffers(String name, Program backupProgram) {
      return backupProgram;
   }

   private Program makeShadow(String name, Program backupProgram) {
      return backupProgram;
   }

   public Program[] getPrograms() {
      return new Program[0];
   }

   public Program[] makeShadowcomps(String prefix, int count) {
      return new Program[0];
   }
}
