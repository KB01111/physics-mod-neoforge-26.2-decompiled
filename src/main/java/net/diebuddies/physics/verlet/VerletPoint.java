package net.diebuddies.physics.verlet;

import net.minecraft.util.ARGB;
import org.joml.Vector2f;
import org.joml.Vector3d;

public class VerletPoint {
   public final Vector3d position;
   public final Vector3d prevPosition;
   public final Vector3d bufferPosition;
   public final Vector3d bufferPrevPosition;
   public final Vector3d renderPosition;
   public final Vector3d force;
   public final Vector3d normal;
   public final Vector3d bufferNormal;
   public final Vector2f uv;
   public final int rgba;
   public boolean locked;
   public double friction = 1.0;
   public Vector3d softRestriction;
   public double restriction;

   public VerletPoint(Vector3d position, boolean locked, int color) {
      this.position = position;
      this.prevPosition = new Vector3d(position);
      this.bufferPosition = new Vector3d(position);
      this.bufferPrevPosition = new Vector3d(position);
      this.force = new Vector3d();
      this.renderPosition = new Vector3d();
      this.locked = locked;
      this.normal = new Vector3d(0.0, 1.0, 0.0);
      this.bufferNormal = new Vector3d(this.normal);
      this.rgba = color;
      this.uv = new Vector2f(0.0F);
   }

   public VerletPoint(Vector3d position, boolean locked) {
      this(position, locked, ARGB.color(255, 255, 255, 255));
   }

   public VerletPoint(Vector3d position) {
      this(position, false);
   }

   public void setRestriction(double restriction) {
      this.softRestriction = new Vector3d(this.position);
      this.restriction = restriction;
   }

   public void updateRenderPosition(double renderPercent) {
      this.bufferPrevPosition.lerp(this.bufferPosition, renderPercent, this.renderPosition);
   }
}
