package net.diebuddies.jbox2d.callbacks;

import net.diebuddies.jbox2d.dynamics.Fixture;
import net.diebuddies.jbox2d.dynamics.joints.Joint;

public interface DestructionListener {
   void sayGoodbye(Joint var1);

   void sayGoodbye(Fixture var1);
}
