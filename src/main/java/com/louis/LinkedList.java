/**
 * 
 */
package com.louis;

/**
 * @author Louis Ashton
 *
 */
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class LinkedList<Item> implements Iterable<Item> {
    private int sizeOfList;
    private Node preceding;
    private Node following;

    public LinkedList() {
        preceding  = new Node();
        following = new Node();
        preceding.next = following;
        following.previous = preceding;
    }

    private class Node {
        private Item item;
        private Node next;
        private Node previous;
    }

    public int size() {
        return sizeOfList;
    }

    public void add(Item item) {
        Node last = following.previous;
        Node addition = new Node();
        addition.item = item;
        addition.next = following;
        addition.previous = last;
        following.previous = addition;
        last.next = addition;
        sizeOfList++;
    }

    public ListIterator<Item> iterator() {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements ListIterator<Item> {
        private Node current = preceding.next;
        private Node lastAccessed = null;
        private int index = 0;

        public boolean hasNext() {
            return index < sizeOfList;
        }
        
        public boolean hasPrevious() {
            return index > 0;
        }
        
        public int previousIndex() {
            return index - 1;
        }
        
        public int nextIndex() {
            return index;
        }
        
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastAccessed = current;
            Item item = current.item;
            current = current.next; 
            index++;
            return item;
        }

        public Item previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            current = current.previous;
            index--;
            lastAccessed = current;
            return current.item;
        }

        public void set(Item item) {
            if (lastAccessed == null) {
                throw new IllegalStateException();
            }
            lastAccessed.item = item;
        }

        public void remove() { 
            if (lastAccessed == null) {
                throw new IllegalStateException();
            }
            Node before = lastAccessed.previous;
            Node replacement = lastAccessed.next;
            before.next = replacement;
            replacement.previous = before;
            sizeOfList--;
            if (current == lastAccessed) {
                current = replacement;
            } else {
                index--;
            }
            lastAccessed = null;
        }

        public void add(Item item) {
            Node before = current.previous;
            Node insertion = new Node();
            Node after = current;
            insertion.item = item;
            before.next = insertion;
            insertion.next = after;
            after.previous = insertion;
            insertion.previous = before;
            sizeOfList++;
            index++;
            lastAccessed = null;
        }

    }
}
