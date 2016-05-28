// CHECKSTYLE:OFF

// CHECKSTYLE:ON

package com.louis;

import com.google.common.base.Preconditions;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Implements a Linkedlist.
 *
 * Linked lists implement the list abstract data type. Each member contains a reference to the next
 * member. In this doubly linkedlist, they also contain a reference to the previous member.
 *
 * @author Louis Ashton (louisashton@live.com)
 */
public class LinkedList<T> implements Iterable<T> {

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
        private T item;
        private Node next;
        private Node previous;
    }

    /**
     * Returns the number of nodes in the list.
     */
    public final int size() {
        return sizeOfList;
    }

    /**
     * Adds a node to the list.
     *
     * @param item the item to be added.
     * @throws IllegalArgumentException if item is null.
     */
    public final void add(T item) {
        Preconditions.checkArgument(item != null, "First argument to add(T item) is null.");
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
     * @return true if key is in the BST
     * @throws IllegalArgumentException if item is null.
     */
    public final boolean contains(T item) {
        Preconditions.checkArgument(item != null, "First argument to contains(T item) is null.");
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
    public final void remove(T item) {
        Preconditions.checkArgument(item != null, "First argument to remove(T item) is null.");

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
     * @return Returns the iterator for the list
     */
    @Override
    public final ListIterator<T> iterator() {
        return new LinkedListIterator();
    }

    /**
     * Implements the list's iterator.
     *
     * Each member has an item. It also stores the location of its surrounding nodes.
     */
    private class LinkedListIterator implements ListIterator<T> {
        private Node current = first.next;
        private Node lastAccessed = null;
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < sizeOfList;
        }

        @Override
        public boolean hasPrevious() {
            return index > 0;
        }

        @Override
        public int previousIndex() {
            return index - 1;
        }

        @Override
        public int nextIndex() {
            return index;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            lastAccessed = current;
            T item = current.item;
            current = current.next;
            index++;
            return item;
        }

        @Override
        public T previous() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            current = current.previous;
            index--;
            lastAccessed = current;
            return current.item;
        }

        @Override
        public void set(T item) {
            if (lastAccessed == null) {
                throw new IllegalStateException();
            }
            lastAccessed.item = item;
        }

        @Override
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

        @Override
        public void add(T item) {
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
