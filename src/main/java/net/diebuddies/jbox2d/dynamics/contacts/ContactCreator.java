package net.diebuddies.jbox2d.dynamics.contacts;

import net.diebuddies.jbox2d.dynamics.Fixture;
import net.diebuddies.jbox2d.pooling.IWorldPool;

public interface ContactCreator {
   Contact contactCreateFcn(IWorldPool var1, Fixture var2, Fixture var3);

   void contactDestroyFcn(IWorldPool var1, Contact var2);
}
