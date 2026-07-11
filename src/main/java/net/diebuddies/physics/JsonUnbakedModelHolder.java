package net.diebuddies.physics;

import net.minecraft.client.resources.model.ResolvedModel;
import org.joml.Matrix4f;

public class JsonUnbakedModelHolder {
   public ResolvedModel model;
   public Matrix4f transformation;

   public JsonUnbakedModelHolder(ResolvedModel model, Matrix4f transformation) {
      this.model = model;
      this.transformation = transformation;
   }
}
