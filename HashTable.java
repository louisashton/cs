import java.util.*;
import java.lang.reflect.Array;

class HashTable <K, V> {

    // The minimum size of the array; no down-sizing will occur when smaller than this.
    private static final int MIN_CAPACITY = 11;
    private int approxSize = Integer.highestOneBit(MIN_CAPACITY) << 1;
    private int tableMask = approxSize - 1;

    private HashEntry[] table;

    // The current number of elements.
    private int size;

    // The current capacity of the array.
    private int capacity;

    // initialLoad determines how full the array should be made when resizing
    private double initialLoad;
    private double maxLoad;
    private double minLoad;

    public HashTable(){
        size = 0;
        capacity = MIN_CAPACITY;
        maxLoad = 0.75;
        minLoad = 0.25;
        this.initialLoad = 0.5;

        //HashEntry[] table = new HashEntry[capacity];
        table = (HashEntry[]) Array.newInstance(HashEntry.class, capacity);
    }

    /**
    * Returns the value associated with the specified key.
    * @param key the key
    * @return the value associated with <tt>key</tt>;
    *         <tt>null</tt> if no such value
    * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
    */
    public synchronized V get(K key){
        if (key == null) throw new NullPointerException("first argument to get() is null");

        // Finds the key.
        int index = hash(key) % capacity;
        while(!key.equals(table[index].getKey())){
            index = (index + 1) % capacity;
        }
        // Returns the value unless it is null.
        return table[index]==null? null : table[index].getValue();
    }

    /**
    * @param  key the key
    * @param  value the value
    * @throws NullPointerException if <tt>key</tt> is <tt>null</tt>
    */
    public synchronized void put(K key, V value){
        if (key == null) throw new NullPointerException("first argument to put() is null");
        int index = hash(key) % capacity;
        if (value == null) {
	    throw new NullPointerException();
	}
        while (table[index] != null && !key.equals(table[index].getKey())) {
            index = (index + 1) % capacity;
        }

        //Increases the size if a new key is being put in.
        if (table[index] == null) 
            size++;
        table[index] = new HashEntry(key, value);

        // Resizes if required.
        resize(); 
    }

    // Returns the absolute value of the hashed key
    private int hash(K key){
        int hashValue = key.hashCode();
        hashValue ^= (hashValue >>> 20) ^ (hashValue >>> 12);
        hashValue = hashValue ^ (hashValue >>> 7) ^ (hashValue >>> 4);
	return Math.abs(hashValue);
    }

    // Resizes the array when required.
    private void resize(){
        if(!((size<capacity*minLoad && capacity>MIN_CAPACITY) || size>capacity*maxLoad)){
            return;
        }
        // The size of the new array
        int newCapacity = (int) (size/initialLoad);

        @SuppressWarnings("unchecked")
        // Makes the new array
        HashEntry[] newArray = (HashEntry[]) Array.newInstance(HashEntry.class, newCapacity);
        //HashEntry[] newArray = new HashEntry[newCapacity];

        for (int oldIndex = 0; oldIndex < capacity; oldIndex++){
            HashEntry oldEntry = table[oldIndex];
            if (oldEntry == null)
                continue;

            int index = hash(oldEntry.getKey()) % newCapacity;
            while (newArray[index]!=null && !oldEntry.getKey().equals(newArray[index].getKey())) {
                // get next index
                index = (index + 1) % newCapacity;
            }
            newArray[index] = oldEntry;
        }
        this.table = newArray;
        this.capacity = newCapacity;
    }

    // Returns the number of elements in the hashtable.
    public int size(){
        return size;
    }

    // Returns all the keys in the hashtable.
    public Set<K> getAll(){
        Set<K> keys = new HashSet<K>(size);
        for(HashEntry entry : table)
            if(entry != null)
                keys.add(entry.getKey());
        return keys;
    }

    // Removes the key from the hashtable.   
    public void delete(K key){
        if (key == null) throw new NullPointerException("first argument to put() is null");
        List<HashEntry> entries = new ArrayList<HashEntry>();

        // Locates the key.
        int index = hash(key) % capacity;
        while (table[index]!=null && !key.equals(table[index].getKey())){
            index = (index+1) % capacity;
            if (table[index] == null)
                System.out.printf("Key %s already deleted %n", key.toString());
        }

        // Deletes keys that collided with this key.
        while(table[index] != null){
            entries.add(table[index]);
            table[index] = null;
            size--;
            index = (index+1) % capacity;
        }

        entries.remove(0); // Ignore the key to be deleted.

        for (HashEntry entry : entries)
            this.put(entry.getKey(), entry.getValue()); // Puts the rest back in the hashtable.
    }

    public String toString(){
        return String.format("Hashtable(%f, %f, %f)", maxLoad, minLoad, initialLoad);
    }

    private class HashEntry {
        private final K key;
        private final V value;

        HashEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }     

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

    public static void main(String arg[]) {
        HashTable<String, String> table = new HashTable<String, String>();

        for (int i = 0; i < 100; i++) {
            table.put("key" + i, "value" + i);

        }

        for (int i = 0; i < 100; i++) {

            System.out.println(table.get("key" + i));
        }
    }

}