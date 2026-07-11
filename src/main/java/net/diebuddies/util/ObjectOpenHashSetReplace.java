package net.diebuddies.util;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class ObjectOpenHashSetReplace<K> extends ObjectOpenHashSet<K> {
   private static final long serialVersionUID = 6162921407543084747L;

   public boolean addOrReplace(K k) {
      if (k == null) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
      } else {
         K[] key = (K[])this.key;
         int pos;
         K curr;
         if ((curr = key[pos = HashCommon.mix(k.hashCode()) & this.mask]) != null) {
            if (curr.equals(k)) {
               key[pos] = k;
               return false;
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (curr.equals(k)) {
                  key[pos] = k;
                  return false;
               }
            }
         }

         key[pos] = k;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return true;
   }
}
