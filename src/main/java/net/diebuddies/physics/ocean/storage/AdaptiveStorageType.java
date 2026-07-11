package net.diebuddies.physics.ocean.storage;

import java.util.Arrays;

public final class AdaptiveStorageType implements StorageType {
   private static final int MAX_PALETTE = 64;
   private final int size;
   private byte[] palette;
   private int paletteSize;
   private final byte[] valueToIndex = new byte[256];
   private int bitsPerValue;
   private byte[] packed;

   private AdaptiveStorageType(int size) {
      this.size = size;
   }

   public static StorageType fromFull(byte[] full) {
      int size = full.length;
      if (size == 0) {
         return new EqualStorageType((byte)0);
      } else {
         byte first = full[0];
         boolean allSame = true;

         for (int i = 1; i < size; i++) {
            if (full[i] != first) {
               allSame = false;
               break;
            }
         }

         if (allSame) {
            return new EqualStorageType(first);
         } else {
            AdaptiveStorageType s = new AdaptiveStorageType(size);
            s.palette = new byte[64];
            s.paletteSize = 0;
            byte[] indices = new byte[size];

            for (int pos = 0; pos < size; pos++) {
               byte v = full[pos];
               int u = v & 255;
               int enc = s.valueToIndex[u];
               if (enc == 0) {
                  int idx = s.paletteSize;
                  if (idx >= 64) {
                     return new FullStorageType((byte[])full.clone());
                  }

                  s.palette[idx] = v;
                  s.valueToIndex[u] = (byte)(idx + 1);
                  s.paletteSize = idx + 1;
                  enc = idx + 1;
               }

               indices[pos] = (byte)(enc - 1);
            }

            s.bitsPerValue = Math.max(1, bitsRequired(s.paletteSize));
            s.packed = packIndices(indices, s.bitsPerValue);
            return s;
         }
      }
   }

   private static byte[] packIndices(byte[] indices, int bits) {
      int size = indices.length;
      byte[] out = new byte[packedLength(size, bits)];
      int acc = 0;
      int accBits = 0;
      int outPos = 0;
      int mask = (1 << bits) - 1;

      for (int i = 0; i < size; i++) {
         int v = indices[i] & 255;
         acc |= (v & mask) << accBits;

         for (accBits += bits; accBits >= 8; accBits -= 8) {
            out[outPos++] = (byte)acc;
            acc >>>= 8;
         }
      }

      if (accBits != 0) {
         out[outPos] = (byte)acc;
      }

      return out;
   }

   @Override
   public byte getData(int index) {
      int pIdx = readIndex(this.packed, this.bitsPerValue, index);
      return this.palette[pIdx];
   }

   @Override
   public void setData(StorageContainer storage, int index, byte value) {
      int oldIdx = readIndex(this.packed, this.bitsPerValue, index);
      if (this.palette[oldIdx] != value) {
         int u = value & 255;
         int encoded = this.valueToIndex[u] & 255;
         int newIdx;
         if (encoded != 0) {
            newIdx = encoded - 1;
         } else {
            if (this.paletteSize >= 64) {
               byte[] full = this.toFullArray();
               full[index] = value;
               storage.setData(new FullStorageType(full));
               return;
            }

            newIdx = this.addPaletteValue(value);
         }

         writeIndex(this.packed, this.bitsPerValue, index, newIdx);
      }
   }

   @Override
   public boolean setAndCompareData(StorageContainer storage, int index, byte value) {
      int oldIdx = readIndex(this.packed, this.bitsPerValue, index);
      if (this.palette[oldIdx] == value) {
         return false;
      } else {
         this.setData(storage, index, value);
         return true;
      }
   }

   private int addPaletteValue(byte value) {
      int idx = this.paletteSize;
      this.ensurePaletteCapacity(this.paletteSize + 1);
      this.palette[idx] = value;
      this.valueToIndex[value & 255] = (byte)(idx + 1);
      this.paletteSize++;
      int neededBits = Math.max(1, bitsRequired(this.paletteSize));
      if (neededBits > this.bitsPerValue) {
         this.repackTo(neededBits);
      }

      return idx;
   }

   private void ensurePaletteCapacity(int needed) {
      if (needed > this.palette.length) {
         int newCap = Math.min(64, Math.max(needed, this.palette.length * 2));
         this.palette = Arrays.copyOf(this.palette, newCap);
      }
   }

   private void repackTo(int newBits) {
      byte[] newPacked = new byte[packedLength(this.size, newBits)];

      for (int pos = 0; pos < this.size; pos++) {
         int idx = readIndex(this.packed, this.bitsPerValue, pos);
         writeIndex(newPacked, newBits, pos, idx);
      }

      this.packed = newPacked;
      this.bitsPerValue = newBits;
   }

   private byte[] toFullArray() {
      byte[] out = new byte[this.size];

      for (int pos = 0; pos < this.size; pos++) {
         out[pos] = this.palette[readIndex(this.packed, this.bitsPerValue, pos)];
      }

      return out;
   }

   private static int bitsRequired(int distinctValues) {
      return distinctValues <= 1 ? 0 : 32 - Integer.numberOfLeadingZeros(distinctValues - 1);
   }

   private static int packedLength(int size, int bitsPerValue) {
      long totalBits = (long)size * (long)bitsPerValue;
      return (int)(totalBits + 7L >>> 3);
   }

   private static int readIndex(byte[] packed, int bits, int pos) {
      int bitIndex = pos * bits;
      int byteIndex = bitIndex >>> 3;
      int bitOffset = bitIndex & 7;
      int value = (packed[byteIndex] & 255) >>> bitOffset;
      int bitsInFirst = 8 - bitOffset;
      int shift = bitsInFirst;
      int remaining = bits - bitsInFirst;

      for (int i = 1; remaining > 0; i++) {
         value |= (packed[byteIndex + i] & 255) << shift;
         shift += 8;
         remaining -= 8;
      }

      return value & (1 << bits) - 1;
   }

   private static void writeIndex(byte[] packed, int bits, int pos, int value) {
      int bitIndex = pos * bits;
      int byteIndex = bitIndex >>> 3;
      int bitOffset = bitIndex & 7;
      int remaining = bits;
      int v = value;
      int b = byteIndex;

      for (int off = bitOffset; remaining > 0; off = 0) {
         int bitsThisByte = Math.min(8 - off, remaining);
         int mask = (1 << bitsThisByte) - 1 << off;
         int part = (v & (1 << bitsThisByte) - 1) << off;
         int cur = packed[b] & 255;
         cur = cur & ~mask | part;
         packed[b] = (byte)cur;
         v >>>= bitsThisByte;
         remaining -= bitsThisByte;
         b++;
      }
   }
}
