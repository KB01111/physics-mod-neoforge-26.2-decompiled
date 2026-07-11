package net.diebuddies.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class DoublyLinkedList<T extends DoublyLinkedList.NodeStorage<T>> implements Iterable<T> {
   private DoublyLinkedList.Node<T> head = null;
   private DoublyLinkedList.Node<T> tail = null;
   private int size = 0;

   public void add(T data) {
      DoublyLinkedList.Node<T> newNode = new DoublyLinkedList.Node<>(data);
      data.setNode(newNode);
      if (this.tail == null) {
         this.head = newNode;
         this.tail = newNode;
      } else {
         this.tail.next = newNode;
         newNode.prev = this.tail;
         this.tail = newNode;
      }

      this.size++;
   }

   public boolean remove(T data) {
      if (data != null) {
         DoublyLinkedList.Node<T> node = data.getNode();
         if (node != null) {
            this.removeNode(node);
            data.setNode(null);
            return true;
         }
      }

      return false;
   }

   private void removeNode(DoublyLinkedList.Node<T> node) {
      this.size--;
      if (node == this.head && node == this.tail) {
         this.head = null;
         this.tail = null;
      } else if (node == this.head) {
         this.head = node.next;
         this.head.prev = null;
      } else if (node == this.tail) {
         this.tail = node.prev;
         this.tail.next = null;
      } else {
         node.prev.next = node.next;
         node.next.prev = node.prev;
      }
   }

   public void clear() {
      DoublyLinkedList.Node<T> n = this.head;

      while (n != null) {
         DoublyLinkedList.Node<T> next = n.next;
         T data = n.data;
         if (data != null) {
            data.setNode(null);
         }

         n.data = null;
         n.prev = null;
         n.next = null;
         n = next;
      }

      this.head = null;
      this.tail = null;
      this.size = 0;
   }

   public int size() {
      return this.size;
   }

   @Override
   public Iterator<T> iterator() {
      return new DoublyLinkedList.DoublyLinkedListIterator(this.head);
   }

   private class DoublyLinkedListIterator implements Iterator<T> {
      private DoublyLinkedList.Node<T> current;

      public DoublyLinkedListIterator(DoublyLinkedList.Node<T> head) {
         Objects.requireNonNull(DoublyLinkedList.this);
         super();
         this.current = head;
      }

      @Override
      public boolean hasNext() {
         return this.current != null;
      }

      public T next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            T data = this.current.data;
            this.current = this.current.next;
            return data;
         }
      }

      @Override
      public void remove() {
         DoublyLinkedList.Node<T> nodeToRemove;
         if (this.current == null) {
            nodeToRemove = DoublyLinkedList.this.tail;
         } else {
            if (this.current.prev == null) {
               throw new IllegalStateException("remove() can only be called after a call to next()");
            }

            nodeToRemove = this.current.prev;
         }

         nodeToRemove.data.setNode(null);
         if (nodeToRemove.prev != null) {
            nodeToRemove.prev.next = this.current;
         } else {
            DoublyLinkedList.this.head = this.current;
         }

         if (this.current != null) {
            this.current.prev = nodeToRemove.prev;
         }

         if (nodeToRemove == DoublyLinkedList.this.tail) {
            DoublyLinkedList.this.tail = nodeToRemove.prev;
         }

         DoublyLinkedList.this.size--;
      }
   }

   public static class Node<T> {
      T data;
      DoublyLinkedList.Node<T> prev;
      DoublyLinkedList.Node<T> next;

      Node(T data) {
         this.data = data;
      }
   }

   public interface NodeStorage<T> {
      void setNode(DoublyLinkedList.Node<T> var1);

      DoublyLinkedList.Node<T> getNode();
   }
}
