package net.diebuddies.jbox2d.dynamics.contacts;

import net.diebuddies.jbox2d.collision.Manifold;
import net.diebuddies.jbox2d.collision.shapes.ChainShape;
import net.diebuddies.jbox2d.collision.shapes.CircleShape;
import net.diebuddies.jbox2d.collision.shapes.EdgeShape;
import net.diebuddies.jbox2d.collision.shapes.ShapeType;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.dynamics.Fixture;
import net.diebuddies.jbox2d.pooling.IWorldPool;

public class ChainAndCircleContact extends Contact {
   private final EdgeShape edge = new EdgeShape();

   public ChainAndCircleContact(IWorldPool argPool) {
      super(argPool);
   }

   @Override
   public void init(Fixture fA, int indexA, Fixture fB, int indexB) {
      super.init(fA, indexA, fB, indexB);

      assert this.m_fixtureA.getType() == ShapeType.CHAIN;

      assert this.m_fixtureB.getType() == ShapeType.CIRCLE;
   }

   @Override
   public void evaluate(Manifold manifold, Transform xfA, Transform xfB) {
      ChainShape chain = (ChainShape)this.m_fixtureA.getShape();
      chain.getChildEdge(this.edge, this.m_indexA);
      this.pool.getCollision().collideEdgeAndCircle(manifold, this.edge, xfA, (CircleShape)this.m_fixtureB.getShape(), xfB);
   }
}
