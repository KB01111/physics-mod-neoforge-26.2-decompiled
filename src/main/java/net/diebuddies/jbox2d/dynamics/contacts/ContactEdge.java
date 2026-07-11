package net.diebuddies.jbox2d.dynamics.contacts;

import net.diebuddies.jbox2d.dynamics.Body;

public class ContactEdge {
   public Body other = null;
   public Contact contact = null;
   public ContactEdge prev = null;
   public ContactEdge next = null;
}
