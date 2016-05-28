package com.louis;

import java.util.NoSuchElementException;

import com.google.common.base.Preconditions;

/**
 * A binary search tree implementation.
 *
 * Binary search trees (BSTs) are derived from the associative array abstract data type. It is a
 * symbol table with keys and values; null is excluded. The tree is not balanced. Keys are
 * Comparable and the tree is sorted by them. Most operations have linear worst case complexity.
 *
 * @author Louis Ashton (louisashton@live.com)
 */
public class BinarySearchTree<Key extends Comparable<Key>, Value> {

    // The root of the binary search tree.
    private Node root;

    /**
     * A node of a binary search tree.
     *
     * Each node has a key and associated value. Pointers to the node's children are also stored.
     */
    private class Node {
        private Key key;
        private Value value;
        private Node leftSubtree;
        private Node rightSubtree;
        private int sizeOfSubtree;

        /**
         * Creates a Node.
         */
        public Node(Key key, Value value, int sizeOfSubtree) {
            this.key = key;
            this.value = value;
            this.sizeOfSubtree = sizeOfSubtree;
        }
    }

    /**
     * Creates a binary search tree.
     */
    public BinarySearchTree() {}

    /**
     * Returns the number of nodes in the binary search tree.
     */
    public int size() {
        return size(root);
    }

    /**
     * Gets the size of a subtree.
     *
     * @param node the node is the root of the subtree.
     * @return Returns the number of pairs descendant from a node.
     */
    private int size(Node node) {
        if (node == null) {
            return 0;
        } else {
            return node.sizeOfSubtree;
        }
    }

    /**
     * Checks the binary search tree for a key.
     *
     * @param key the key that is to be found.
     * @return true if key is in the binary search tree.
     * @throws IllegalArgumentException if key is null.
     */
    public boolean contains(Key key) {
        Preconditions.checkArgument(key != null, "first argument to contains(Key key) is null");
        return get(key) != null;
    }

    /**
     * Gets a key's value.
     *
     * @param key the key whose value is sought.
     * @return Returns the value of the key if it is in the binary search tree; null otherwise.
     * @throws IllegalArgumentException if key is null.
     */
    public Value get(Key key) {
        Preconditions.checkArgument(key != null, "First argument to get(Key key) is null.");
        return get(root, key);
    }

    /**
     * Gets a key's value.
     *
     * @param node node is the root of the subtree.
     * @param key  key is the key.
     * @return Returns the value of the key if it is in the subtree; null otherwise.
     */
    private Value get(Node node, Key key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return get(node.leftSubtree, key);
        } else if (cmp > 0) {
            return get(node.rightSubtree, key);
        } else {
            return node.value;
        }
    }

    /**
     * Adds a node to the binary search tree.
     *
     * @param key   the key to be added.
     * @param value the value to be added.
     * @throws IllegalArgumentException if key is null.
     */
    public void put(Key key, Value value) {
        Preconditions.checkArgument(key != null,
                "First argument to put(Key key, Value value) is null.");
        if (value == null) {
            delete(key);
            return;
        }
        root = put(root, key, value);
    }

    /**
     * Adds a pair to a subtree.
     *
     * @param node  node is the root of the subtree.
     * @param key   key is the key.
     * @param value value is the value.
     * @return Returns the updated root of the subtree.
     */
    private Node put(Node node, Key key, Value value) {
        if (node == null) {
            return new Node(key, value, 1);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.leftSubtree = put(node.leftSubtree, key, value);
        } else if (cmp > 0) {
            node.rightSubtree = put(node.rightSubtree, key, value);
        } else {
            node.value = value;
        }
        node.sizeOfSubtree = 1 + size(node.leftSubtree) + size(node.rightSubtree);
        return node;
    }

    /**
     * Removes a key from the binary search tree.
     *
     * @param key the key to be deleted.
     * @throws IllegalArgumentException if key is null.
     */
    public void delete(Key key) {
        Preconditions.checkArgument(key != null, "First argument to delete(Key key) is null.");
        root = delete(root, key);
    }

    /**
     * Removes a key from a subtree.
     *
     * @param node node is the root of the subtree.
     * @param key  key is the key.
     * @return Returns the updated root of the subtree.
     */
    private Node delete(Node node, Key key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.leftSubtree = delete(node.leftSubtree, key);
        } else if (cmp > 0) {
            node.rightSubtree = delete(node.rightSubtree, key);
        } else {
            if (node.rightSubtree == null)
                return node.leftSubtree;
            if (node.leftSubtree == null)
                return node.rightSubtree;
            Node topNode = node;
            node = min(topNode.rightSubtree);
            node.rightSubtree = deleteMin(topNode.rightSubtree);
            node.leftSubtree = topNode.leftSubtree;
        }
        node.sizeOfSubtree = size(node.leftSubtree) + size(node.rightSubtree) + 1;
        return node;
    }

    /**
     * Deletes the binary search tree's minimum.
     *
     * @throws NoSuchElementException if the symbol table is empty
     */
    public void deleteMin() {
        if (size() == 0) {
            throw new NoSuchElementException("Nothing to delete.");
        }
        root = deleteMin(root);
    }

    /**
     * Deletes a subtree's minimum.
     *
     * @param node node is the root of the subtree.
     * @return Returns the updated root of the subtree.
     * @throws NoSuchElementException if the symbol table is empty
     */
    private Node deleteMin(Node node) {
        if (node.leftSubtree == null) {
            return node.rightSubtree;
        }
        node.leftSubtree = deleteMin(node.leftSubtree);
        node.sizeOfSubtree = size(node.leftSubtree) + size(node.rightSubtree) + 1;
        return node;
    }

    /**
     * Finds the minimum of the binary search tree.
     *
     * @return Returns the smallest key in the symbol table.
     * @throws NoSuchElementException if the symbol table is empty.
     */
    public Key min() {
        if (size() == 0) {
            throw new NoSuchElementException("binary search tree is empty.");
        }
        return min(root).key;
    }

    /**
     * Finds the minimum of a subtree.
     *
     * @param node node is the root of the subtree.
     * @return Returns the smallest key in the subtree.
     */
    private Node min(Node node) {
        if (node.leftSubtree == null) {
            return node;
        } else {
            return min(node.leftSubtree);
        }
    }

    /**
     * Finds the binary search tree's maximum.
     *
     * @return Returns the largest key in the symbol table.
     * @throws NoSuchElementException if the symbol table is empty.
     */
    public Key max() {
        if (size() == 0) {
            throw new NoSuchElementException("The binary search tree is empty.");
        }
        return max(root).key;
    }

    /**
     * Finds the maximum of a subtree.
     *
     * @param node node is the root of the subtree.
     * @return Returns the largest key in the subtree.
     */
    private Node max(Node node) {
        if (node.rightSubtree == null) {
            return node;
        } else {
            return max(node.rightSubtree);
        }
    }
}
