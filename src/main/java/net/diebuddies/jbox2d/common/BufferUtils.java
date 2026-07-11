package net.diebuddies.jbox2d.common;

import java.lang.reflect.Array;

public class BufferUtils {
   public static <T> T[] reallocateBuffer(Class<T> klass, T[] oldBuffer, int oldCapacity, int newCapacity) {
      assert newCapacity > oldCapacity;

      T[] newBuffer = (T[])Array.newInstance(klass, newCapacity);
      if (oldBuffer != null) {
         System.arraycopy(oldBuffer, 0, newBuffer, 0, oldCapacity);
      }

      for (int i = oldCapacity; i < newCapacity; i++) {
         try {
            newBuffer[i] = klass.newInstance();
         } catch (Exception var7) {
            throw new RuntimeException(var7);
         }
      }

      return newBuffer;
   }

   public static int[] reallocateBuffer(int[] oldBuffer, int oldCapacity, int newCapacity) {
      assert newCapacity > oldCapacity;

      int[] newBuffer = new int[newCapacity];
      if (oldBuffer != null) {
         System.arraycopy(oldBuffer, 0, newBuffer, 0, oldCapacity);
      }

      return newBuffer;
   }

   public static float[] reallocateBuffer(float[] oldBuffer, int oldCapacity, int newCapacity) {
      assert newCapacity > oldCapacity;

      float[] newBuffer = new float[newCapacity];
      if (oldBuffer != null) {
         System.arraycopy(oldBuffer, 0, newBuffer, 0, oldCapacity);
      }

      return newBuffer;
   }

   public static <T> T[] reallocateBuffer(Class<T> klass, T[] buffer, int userSuppliedCapacity, int oldCapacity, int newCapacity, boolean deferred) {
      assert newCapacity > oldCapacity;

      assert userSuppliedCapacity == 0 || newCapacity <= userSuppliedCapacity;

      if ((!deferred || buffer != null) && userSuppliedCapacity == 0) {
         buffer = reallocateBuffer(klass, buffer, oldCapacity, newCapacity);
      }

      return buffer;
   }

   public static int[] reallocateBuffer(int[] buffer, int userSuppliedCapacity, int oldCapacity, int newCapacity, boolean deferred) {
      assert newCapacity > oldCapacity;

      assert userSuppliedCapacity == 0 || newCapacity <= userSuppliedCapacity;

      if ((!deferred || buffer != null) && userSuppliedCapacity == 0) {
         buffer = reallocateBuffer(buffer, oldCapacity, newCapacity);
      }

      return buffer;
   }

   public static float[] reallocateBuffer(float[] buffer, int userSuppliedCapacity, int oldCapacity, int newCapacity, boolean deferred) {
      assert newCapacity > oldCapacity;

      assert userSuppliedCapacity == 0 || newCapacity <= userSuppliedCapacity;

      if ((!deferred || buffer != null) && userSuppliedCapacity == 0) {
         buffer = reallocateBuffer(buffer, oldCapacity, newCapacity);
      }

      return buffer;
   }

   public static <T> void rotate(T[] ray, int first, int new_first, int last) {
      int next = new_first;

      while (next != first) {
         T temp = ray[first];
         ray[first] = ray[next];
         ray[next] = temp;
         first++;
         if (++next == last) {
            next = new_first;
         } else if (first == new_first) {
            new_first = next;
         }
      }
   }

   public static void rotate(int[] ray, int first, int new_first, int last) {
      int next = new_first;

      while (next != first) {
         int temp = ray[first];
         ray[first] = ray[next];
         ray[next] = temp;
         first++;
         if (++next == last) {
            next = new_first;
         } else if (first == new_first) {
            new_first = next;
         }
      }
   }

   public static void rotate(float[] ray, int first, int new_first, int last) {
      int next = new_first;

      while (next != first) {
         float temp = ray[first];
         ray[first] = ray[next];
         ray[next] = temp;
         first++;
         if (++next == last) {
            next = new_first;
         } else if (first == new_first) {
            new_first = next;
         }
      }
   }
}
