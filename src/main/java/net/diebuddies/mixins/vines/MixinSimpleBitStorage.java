package net.diebuddies.mixins.vines;

import net.diebuddies.physics.vines.FastBlockSearcher;
import net.diebuddies.physics.vines.FastBlockSearcherConsumer;
import net.minecraft.util.SimpleBitStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({SimpleBitStorage.class})
public class MixinSimpleBitStorage implements FastBlockSearcher {
   @Shadow
   @Final
   private long[] data;
   @Shadow
   @Final
   private int bits;
   @Shadow
   @Final
   private long mask;
   @Shadow
   @Final
   private int size;
   @Shadow
   @Final
   private int valuesPerLong;

   @Override
   public void getAllFast(FastBlockSearcherConsumer consumer) {
      int i = 0;
      int same = 0;
      int value = Integer.MAX_VALUE;

      for (int k = 0; k < this.data.length; k++) {
         long l = this.data[k];

         for (int j = 0; j < this.valuesPerLong; j++) {
            int newValue = (int)(l & this.mask);
            if (value != newValue && same != 0) {
               consumer.accept(value, same);
               same = 0;
            }

            same++;
            value = newValue;
            l >>= this.bits;
            if (++i >= this.size) {
               if (same != 0) {
                  consumer.accept(newValue, same);
               }

               return;
            }
         }
      }

      if (same != 0) {
         consumer.accept(value, same);
      }
   }
}
