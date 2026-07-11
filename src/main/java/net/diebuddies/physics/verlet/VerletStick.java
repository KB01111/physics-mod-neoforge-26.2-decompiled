package net.diebuddies.physics.verlet;

public class VerletStick {
   public VerletPoint pointA;
   public VerletPoint pointB;
   public double length;
   public double halfLength;

   public VerletStick(VerletPoint pointA, VerletPoint pointB, double length) {
      this.pointA = pointA;
      this.pointB = pointB;
      this.length = length;
      this.halfLength = length * 0.5;
   }

   public VerletStick(VerletPoint pointA, VerletPoint pointB) {
      this(pointA, pointB, pointA.position.distance(pointB.position));
   }
}
