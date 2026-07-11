package net.diebuddies.physics.verlet;

import net.minecraft.client.model.geom.ModelPart;

public interface ModelPartParent {
   void physicsmod$setParent(ModelPart var1);

   ModelPart physicsmod$getParent();

   void physicsmod$setName(String var1);

   String physicsmod$getName();
}
