package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.dynamics.Body;

public class JointEdge {
   public Body other = null;
   public Joint joint = null;
   public JointEdge prev = null;
   public JointEdge next = null;
}
