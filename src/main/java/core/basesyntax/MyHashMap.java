package core.basesyntax;

public class MyHashMap<K, V> implements MyMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    private static final int DEFAULT_MAXIMUM_CAPACITY = 1 << 30;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private Node<K, V>[] table;
    private int size;
    private int threshold;

    @Override
    public void put(K key, V value) {
        putValue(hash(key), key, value);
    }

    @Override
    public V getValue(K key) {
        Node<K,V> current = getNode(key);
        return current == null ? null : current.value;
    }

    @Override
    public int getSize() {
        return size;
    }

    private Node<K,V> getNode(Object key) {
        Node<K, V>[] tab;
        Node<K, V> first;
        int len;
        int hash;
        K firstKey;
        if ((tab = table) != null && (len = tab.length) > 0
                && (first = tab[(len - 1) & (hash = hash(key))]) != null) {
            while (first != null) {
                if (first.hash == hash
                        && ((firstKey = first.key) == key
                        || (key != null && key.equals(firstKey)))) {
                    return first;
                }
                first = first.next;
            }

        }
        return null;
    }

    private Node<K, V>[] resizeTable() {
        Node<K, V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap = 0;
        int newThr = 0;

        if (oldCap > 0) {
            if (oldCap >= DEFAULT_MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            } else if ((newCap = oldCap << 1) < DEFAULT_MAXIMUM_CAPACITY) {
                newThr = oldThr << 1;
            }
        } else {
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }

        if (newThr == 0) {
            newThr = (int) (newCap * DEFAULT_LOAD_FACTOR);
        }

        threshold = newThr;
        Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCap];

        for (int bucketIndex = 0; bucketIndex < oldCap; ++bucketIndex) {
            Node<K, V> element = oldTab[bucketIndex];

            while (element != null) {
                Node<K, V> next = element.next;
                int newIndex = element.hash & (newCap - 1);
                element.next = newTab[newIndex];
                newTab[newIndex] = element;
                element = next;

            }
            oldTab[bucketIndex] = null;
        }
        table = newTab;
        return table;
    }

    private class Node<K, V> {
        private final int hash;
        private final K key;
        private V value;
        private Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private V putValue(int hash, K key, V value) {
        Node<K, V>[] tab;
        int len;
        int index;

        if ((tab = table) == null || tab.length == 0) {
            tab = resizeTable();
        }

        len = tab.length;
        index = (len - 1) & hash;
        Node<K, V> node = tab[index];

        if (node == null) {
            tab[index] = new Node<>(hash, key, value, null);
            size++;
            return null;
        }

        Node<K, V> current = node;
        Node<K, V> prev = null;

        while (current != null) {
            if (current.hash == hash
                    && (current.key == key || (key != null && key.equals(current.key)))) {
                V oldValue = current.value;
                current.value = value;
                return oldValue;
            }
            prev = current;
            current = current.next;
        }

        prev.next = new Node<>(hash, key, value, null);
        size++;

        if (size > threshold) {
            resizeTable();
        }

        return null;
    }

    static final int hash(Object key) {
        int hashCode;
        return (key == null) ? 0 : (hashCode = key.hashCode()) ^ (hashCode >>> 16);
    }
}
