package net.diebuddies.jbox2d.callbacks;

import net.diebuddies.jbox2d.collision.Manifold;
import net.diebuddies.jbox2d.dynamics.contacts.Contact;

public interface ContactListener {
   void beginContact(Contact var1);

   void endContact(Contact var1);

   void preSolve(Contact var1, Manifold var2);

   void postSolve(Contact var1, ContactImpulse var2);
}
