package net.diebuddies.util.cpp;

class State {
   boolean parent;
   boolean active;
   boolean sawElse;

   State() {
      this.parent = true;
      this.active = true;
      this.sawElse = false;
   }

   State(State parent) {
      this.parent = parent.isParentActive() && parent.isActive();
      this.active = true;
      this.sawElse = false;
   }

   void setParentActive(boolean b) {
      this.parent = b;
   }

   boolean isParentActive() {
      return this.parent;
   }

   void setActive(boolean b) {
      this.active = b;
   }

   boolean isActive() {
      return this.active;
   }

   void setSawElse() {
      this.sawElse = true;
   }

   boolean sawElse() {
      return this.sawElse;
   }

   @Override
   public String toString() {
      return "parent=" + this.parent + ", active=" + this.active + ", sawelse=" + this.sawElse;
   }
}
