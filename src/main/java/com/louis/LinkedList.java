package com.louis;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import com.google.common.base.Preconditions;

/**
 * Implements a Linkedlist.
 *
 * Linked lists implement the list abstract data type. Each member contains a reference to the next
 * member. In this doubly linkedlist, they also contain a reference to the previous member.
 *
 * @author Louis Ashton (louisashton@live.com)
 */
public class LinkedList<Item> implements Iterable<Item> {

    private int sizeOfList;
    private Node first;
    private Node finish;

    /**
     * Creates a new LinkedList.
     */
    public LinkedList() {
        first = new Node();
        finish = new Node();
        first.next = finish;
        finish.previous = first;
    }

    /**
     * Defines a Node.
     *
     * Each member has an item. It also stores the location of its surrounding nodes.
     */
    private class Node {
        private Item item;
        private Node next;
        private Node previous;
    }

    /**
     * Returns the number of nodes in the list.
     */
    public int size() {
        return sizeOfList;
    }

    /**
     * Adds a node to the list.
     *
     * @param item the item to be added.
     * @throws IllegalArgumentException if item is null.
     */
    public void add(Item item) {
        Preconditions.checkArgument(item != null, "First argument to add(Item item) is null.");
        Node penultimate = finish.previous;
        Node addition = new Node();
        addition.item = item;
        addition.next = finish;
        addition.previous = penultimate;
        finish.previous = addition;
        penultimate.next = addition;
        sizeOfList++;
    }

    /**
     * Checks the list for an item.
     *
     * @param item item is the item.
     * @return true if key is in the BST.
     * @throws IllegalArgumentException if item is null.
     */
    public boolean contains(Item item) {
        Preconditions.checkArgument(item != null, "First argument to contains(Item item) is null.");
        for (Node x = first; x != null; x = x.next) {
            if (item.equals(x.item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes an item from the list.
     *
     * @param item the item.
     * @throws IllegalArgumentException if item is null.
     */
    public void remove(Item item) {
        Preconditions.checkArgument(item != null, "First argument to remove(Item item) is null.");

        for (Node x = first; x.next != null; x = x.next) {
            if (item.equals(x.next.item)) {
                x.next = x.next.next;
                x.next.previous = x;
                sizeOfList--;
            }
        }
    }

    /**
     * Creates an iterator.
     *
     * @return Returns the iterator for the list.
     */
    public ListIterator<Item> iterator() {
        return new LinkedListIterator();
    }

    /**
     * Implements the list's iterator.
     *
     * Each member has an item. It also stores the location of its surrounding nodes.
     */
    private class LinkedListIterator implements ListIterator<Item> {
        private Node current = first.next;
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
