/**
 * 
 */
package com.louis;

import java.util.NoSuchElementException;
import com.google.common.base.Preconditions;

/**
 *  A binary search tree implementation.
 *  
 *  Binary search trees are derived from the associative array abstract data type.
 *  It is a symbol table with keys and values; null is excluded.
 *  The tree is not balanced. Keys are Comparable and the tree is sorted by them.
 *  Most operations have linear worst case complexity.
 *  
 *  @author Louis Ashton (louisashton@live.com)
 */
public class BinarySearchTree<Key extends Comparable<Key>, Value> {
    // The root of the BST.
    private Node root;

    private class Node {
        private Key key;
        private Value value;
        private Node leftSubtree;
        private Node rightSubtree;
        private int sizeOfSubtree;             

        public Node(Key key, Value value, int sizeOfSubtree) {
            this.key = key;
            this.value = value;
            this.sizeOfSubtree = sizeOfSubtree;
        }
    }

    /**
     * Creates a BST.
     */
    public BinarySearchTree() {
    }

    /**
     * Gets the size of the BST.
     * @return Returns the number of pairs in the BST.
     */
    public int size() {
        return size(root);
    }

    /**
     * Gets the size of a subtree.
     * @param node is the root of the subtree.
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
     * Checks the BST for a key.
     *
     * @param key is the key.
     * @return true if key is in the BST.
     * @throws IllegalArgumentException is thrown if key is null.
     */
    public boolean contains(Key key) {
        Preconditions.checkArgument(key != null, "first argument to contains(Key key) is null");
        return get(key) != null;
    }

    /**
     * Gets a key's value.
     *
     * @param key is the key.
     * @return Returns the value of the key if it is in the BST; null otherwise.
     * @throws IllegalArgumentException is thrown if key is null.
     */
    public Value get(Key key) {
        Preconditions.checkArgument(key != null, "First argument to get(Key key) is null.");
        return get(root, key);
    }

    /**
     * Gets a key's value.
     *
     * @param node is the root of the subtree.
     * @param key is the key.
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
     * Adds a node to the BST.
     * 
     * @param key is the key.
     * @param val is the value.
     * @throws IllegalArgumentException is thrown if key is null.
     */
    public void put(Key key, Value value) {
        Preconditions.checkArgument(key != null, "First argument to put(Key key, Value value) is null.");
        if (value == null) {
            delete(key);
            return;
        }
        root = put(root, key, value);
    }

    /**
     * Adds a pair to a subtree.
     * 
     * @param node is the root of the subtree.
     * @param key is the key.
     * @param val is the value.
     * @return Returns the updated root of the subtree.
     */
    private Node put(Node node, Key key, Value value) {
        if (node == null) {
            return new Node(key, value, 1);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.leftSubtree = put(node.leftSubtree,  key, value);
        } else if (cmp > 0) {
            node.rightSubtree = put(node.rightSubtree, key, value);
        } else {
            node.value = value;
        }
        node.sizeOfSubtree = 1 + size(node.leftSubtree) + size(node.rightSubtree);
        return node;
    }

    /**
     * Removes a key from the BST.
     * 
     * @param key is the key.
     * @throws IllegalArgumentException is thrown if key is null.
     */
    public void delete(Key key) {
        Preconditions.checkArgument(key != null, "First argument to delete(Key key) is null.");
        root = delete(root, key);
    }

    /**
     * Removes a key from a subtree.
     * 
     * @param node is the root of the subtree.
     * @param key is the key.
     * @return Returns the updated root of the subtree.
     */
    private Node delete(Node node, Key key) {
        if (node == null) {
            return null;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.leftSubtree  = delete(node.leftSubtree,  key);
        } else if (cmp > 0) {
            node.rightSubtree = delete(node.rightSubtree, key);
        } else { 
            if (node.rightSubtree == null) return node.leftSubtree;
            if (node.leftSubtree  == null) return node.rightSubtree;
            Node topNode = node;
            node = min(topNode.rightSubtree);
            node.rightSubtree = deleteMin(topNode.rightSubtree);
            node.leftSubtree = topNode.leftSubtree;
        } 
        node.sizeOfSubtree = size(node.leftSubtree) + size(node.rightSubtree) + 1;
        return node;
    } 

    /**
     * Deletes the BST's minimum.
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
     * @param node is the root of the subtree.
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
     * Finds the minimum of the BST.
     *
     * @return Returns the smallest key in the symbol table.
     * @throws NoSuchElementException if the symbol table is empty.
     */
    public Key min() {
        if (size() == 0) {
            throw new NoSuchElementException("BST is empty.");
        }
        return min(root).key;
    } 

    /**
     * Finds the minimum of a subtree.
     *
     * @param node is the root of the subtree.
     * @return Returns the smallest key in the subtree.
     */
    private Node min(Node node) { 
        if (node.leftSubtree == null) {
            return node; 
        }
        else {
            return min(node.leftSubtree); 
        }
    } 

    /**
     * Finds the BST's maximum.
     *
     * @return Returns the largest key in the symbol table.
     * @throws NoSuchElementException if the symbol table is empty.
     */
    public Key max() {
        if (size() == 0) {
            throw new NoSuchElementException("BST is empty.");
        }
        return max(root).key;
    } 

    /**
     * Finds the maximum of a subtree.
     *
     * @param node is the root of the subtree.
     * @return Returns the largest key in the subtree.
     */
    private Node max(Node node) {
        if (node.rightSubtree == null) {
            return node; 
        }
        else {
            return max(node.rightSubtree); 
        }
    } 
}
