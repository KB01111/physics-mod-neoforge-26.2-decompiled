package net.diebuddies.jbox2d.dynamics.contacts;

import net.diebuddies.jbox2d.collision.Manifold;
import net.diebuddies.jbox2d.collision.shapes.PolygonShape;
import net.diebuddies.jbox2d.collision.shapes.ShapeType;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.dynamics.Fixture;
import net.diebuddies.jbox2d.pooling.IWorldPool;

public class PolygonContact extends Contact {
   public PolygonContact(IWorldPool argPool) {
      super(argPool);
   }

   public void init(Fixture fixtureA, Fixture fixtureB) {
      super.init(fixtureA, 0, fixtureB, 0);

      assert this.m_fixtureA.getType() == ShapeType.POLYGON;

      assert this.m_fixtureB.getType() == ShapeType.POLYGON;
   }

   @Override
   public void evaluate(Manifold manifold, Transform xfA, Transform xfB) {
      this.pool.getCollision().collidePolygons(manifold, (PolygonShape)this.m_fixtureA.getShape(), xfA, (PolygonShape)this.m_fixtureB.getShape(), xfB);
   }
}
