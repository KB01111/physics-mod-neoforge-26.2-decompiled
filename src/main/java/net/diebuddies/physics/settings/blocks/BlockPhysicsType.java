package net.diebuddies.physics.settings.blocks;

public enum BlockPhysicsType {
   FRACTURED("physicsmod.enum.blockphysics.fractured"),
   BLOCKY("physicsmod.enum.blockphysics.blocky"),
   PARTICLES("physicsmod.enum.blockphysics.particles"),
   OFF("physicsmod.enum.blockphysics.off"),
   MAIN_RULE("physicsmod.enum.blockphysics.mainrule"),
   FRACTURED_VOXEL("physicsmod.enum.blockphysics.fracturedvoxel");

   private String translationId;

   private BlockPhysicsType(String translationId) {
      this.translationId = translationId;
   }

   @Override
   public String toString() {
      return this.translationId;
   }
}
