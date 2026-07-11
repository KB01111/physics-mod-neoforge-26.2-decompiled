package net.diebuddies.physics.settings.mobs;

public enum MobPhysicsType {
   RAGDOLL("physicsmod.enum.mobphysics.ragdoll"),
   BLOCKY("physicsmod.enum.mobphysics.blocky"),
   FRACTURED("physicsmod.enum.mobphysics.fractured"),
   FRACTURED_BLOOD("physicsmod.enum.mobphysics.fracturedblood"),
   OFF("physicsmod.enum.mobphysics.off"),
   MAIN_RULE("physicsmod.enum.mobphysics.mainrule"),
   RAGDOLL_BREAK("physicsmod.enum.mobphysics.ragdollbreaking"),
   RAGDOLL_BREAK_BLOOD("physicsmod.enum.mobphysics.ragdollbreakingblood");

   private String translationId;

   private MobPhysicsType(String translationId) {
      this.translationId = translationId;
   }

   @Override
   public String toString() {
      return this.translationId;
   }
}
