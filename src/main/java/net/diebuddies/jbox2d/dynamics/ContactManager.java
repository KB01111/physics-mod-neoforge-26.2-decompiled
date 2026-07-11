package net.diebuddies.jbox2d.dynamics;

import net.diebuddies.jbox2d.callbacks.ContactFilter;
import net.diebuddies.jbox2d.callbacks.ContactListener;
import net.diebuddies.jbox2d.callbacks.PairCallback;
import net.diebuddies.jbox2d.collision.broadphase.BroadPhase;
import net.diebuddies.jbox2d.dynamics.contacts.Contact;
import net.diebuddies.jbox2d.dynamics.contacts.ContactEdge;

public class ContactManager implements PairCallback {
   public BroadPhase m_broadPhase;
   public Contact m_contactList = null;
   public int m_contactCount = 0;
   public ContactFilter m_contactFilter = new ContactFilter();
   public ContactListener m_contactListener = null;
   private final World pool;

   public ContactManager(World argPool, BroadPhase broadPhase) {
      this.m_broadPhase = broadPhase;
      this.pool = argPool;
   }

   @Override
   public void addPair(Object proxyUserDataA, Object proxyUserDataB) {
      FixtureProxy proxyA = (FixtureProxy)proxyUserDataA;
      FixtureProxy proxyB = (FixtureProxy)proxyUserDataB;
      Fixture fixtureA = proxyA.fixture;
      Fixture fixtureB = proxyB.fixture;
      int indexA = proxyA.childIndex;
      int indexB = proxyB.childIndex;
      Body bodyA = fixtureA.getBody();
      Body bodyB = fixtureB.getBody();
      if (bodyA != bodyB) {
         for (ContactEdge edge = bodyB.getContactList(); edge != null; edge = edge.next) {
            if (edge.other == bodyA) {
               Fixture fA = edge.contact.getFixtureA();
               Fixture fB = edge.contact.getFixtureB();
               int iA = edge.contact.getChildIndexA();
               int iB = edge.contact.getChildIndexB();
               if (fA == fixtureA && iA == indexA && fB == fixtureB && iB == indexB) {
                  return;
               }

               if (fA == fixtureB && iA == indexB && fB == fixtureA && iB == indexA) {
                  return;
               }
            }
         }

         if (bodyB.shouldCollide(bodyA)) {
            if (this.m_contactFilter == null || this.m_contactFilter.shouldCollide(fixtureA, fixtureB)) {
               Contact c = this.pool.popContact(fixtureA, indexA, fixtureB, indexB);
               if (c != null) {
                  fixtureA = c.getFixtureA();
                  fixtureB = c.getFixtureB();
                  indexA = c.getChildIndexA();
                  indexB = c.getChildIndexB();
                  bodyA = fixtureA.getBody();
                  bodyB = fixtureB.getBody();
                  c.m_prev = null;
                  c.m_next = this.m_contactList;
                  if (this.m_contactList != null) {
                     this.m_contactList.m_prev = c;
                  }

                  this.m_contactList = c;
                  c.m_nodeA.contact = c;
                  c.m_nodeA.other = bodyB;
                  c.m_nodeA.prev = null;
                  c.m_nodeA.next = bodyA.m_contactList;
                  if (bodyA.m_contactList != null) {
                     bodyA.m_contactList.prev = c.m_nodeA;
                  }

                  bodyA.m_contactList = c.m_nodeA;
                  c.m_nodeB.contact = c;
                  c.m_nodeB.other = bodyA;
                  c.m_nodeB.prev = null;
                  c.m_nodeB.next = bodyB.m_contactList;
                  if (bodyB.m_contactList != null) {
                     bodyB.m_contactList.prev = c.m_nodeB;
                  }

                  bodyB.m_contactList = c.m_nodeB;
                  if (!fixtureA.isSensor() && !fixtureB.isSensor()) {
                     bodyA.setAwake(true);
                     bodyB.setAwake(true);
                  }

                  this.m_contactCount++;
               }
            }
         }
      }
   }

   public void findNewContacts() {
      this.m_broadPhase.updatePairs(this);
   }

   public void destroy(Contact c) {
      Fixture fixtureA = c.getFixtureA();
      Fixture fixtureB = c.getFixtureB();
      Body bodyA = fixtureA.getBody();
      Body bodyB = fixtureB.getBody();
      if (this.m_contactListener != null && c.isTouching()) {
         this.m_contactListener.endContact(c);
      }

      if (c.m_prev != null) {
         c.m_prev.m_next = c.m_next;
      }

      if (c.m_next != null) {
         c.m_next.m_prev = c.m_prev;
      }

      if (c == this.m_contactList) {
         this.m_contactList = c.m_next;
      }

      if (c.m_nodeA.prev != null) {
         c.m_nodeA.prev.next = c.m_nodeA.next;
      }

      if (c.m_nodeA.next != null) {
         c.m_nodeA.next.prev = c.m_nodeA.prev;
      }

      if (c.m_nodeA == bodyA.m_contactList) {
         bodyA.m_contactList = c.m_nodeA.next;
      }

      if (c.m_nodeB.prev != null) {
         c.m_nodeB.prev.next = c.m_nodeB.next;
      }

      if (c.m_nodeB.next != null) {
         c.m_nodeB.next.prev = c.m_nodeB.prev;
      }

      if (c.m_nodeB == bodyB.m_contactList) {
         bodyB.m_contactList = c.m_nodeB.next;
      }

      this.pool.pushContact(c);
      this.m_contactCount--;
   }

   public void collide() {
      Contact c = this.m_contactList;

      while (c != null) {
         Fixture fixtureA = c.getFixtureA();
         Fixture fixtureB = c.getFixtureB();
         int indexA = c.getChildIndexA();
         int indexB = c.getChildIndexB();
         Body bodyA = fixtureA.getBody();
         Body bodyB = fixtureB.getBody();
         if ((c.m_flags & 8) == 8) {
            if (!bodyB.shouldCollide(bodyA)) {
               Contact cNuke = c;
               c = c.getNext();
               this.destroy(cNuke);
               continue;
            }

            if (this.m_contactFilter != null && !this.m_contactFilter.shouldCollide(fixtureA, fixtureB)) {
               Contact cNuke = c;
               c = c.getNext();
               this.destroy(cNuke);
               continue;
            }

            c.m_flags &= -9;
         }

         boolean activeA = bodyA.isAwake() && bodyA.m_type != BodyType.STATIC;
         boolean activeB = bodyB.isAwake() && bodyB.m_type != BodyType.STATIC;
         if (!activeA && !activeB) {
            c = c.getNext();
         } else {
            int proxyIdA = fixtureA.m_proxies[indexA].proxyId;
            int proxyIdB = fixtureB.m_proxies[indexB].proxyId;
            boolean overlap = this.m_broadPhase.testOverlap(proxyIdA, proxyIdB);
            if (!overlap) {
               Contact cNuke = c;
               c = c.getNext();
               this.destroy(cNuke);
            } else {
               c.update(this.m_contactListener);
               c = c.getNext();
            }
         }
      }
   }
}
