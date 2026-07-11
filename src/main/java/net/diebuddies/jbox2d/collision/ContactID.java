package net.diebuddies.jbox2d.collision;

public class ContactID implements Comparable<ContactID> {
   public byte indexA;
   public byte indexB;
   public byte typeA;
   public byte typeB;

   public int getKey() {
      return this.indexA << 24 | this.indexB << 16 | this.typeA << 8 | this.typeB;
   }

   public boolean isEqual(ContactID cid) {
      return this.getKey() == cid.getKey();
   }

   public ContactID() {
   }

   public ContactID(ContactID c) {
      this.set(c);
   }

   public void set(ContactID c) {
      this.indexA = c.indexA;
      this.indexB = c.indexB;
      this.typeA = c.typeA;
      this.typeB = c.typeB;
   }

   public void flip() {
      byte tempA = this.indexA;
      this.indexA = this.indexB;
      this.indexB = tempA;
      tempA = this.typeA;
      this.typeA = this.typeB;
      this.typeB = tempA;
   }

   public void zero() {
      this.indexA = 0;
      this.indexB = 0;
      this.typeA = 0;
      this.typeB = 0;
   }

   public int compareTo(ContactID o) {
      return this.getKey() - o.getKey();
   }

   public static enum Type {
      VERTEX,
      FACE;
   }
}
