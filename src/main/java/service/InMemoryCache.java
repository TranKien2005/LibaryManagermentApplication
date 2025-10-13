package service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCache<K, V> {
    private final Map<K, V> map = new ConcurrentHashMap<>();

    public V get(K key) {
        return map.get(key);
    }

    public void put(K key, V value) {
        map.put(key, value);
    }

    public void clear() {
        map.clear();
    }

    public V remove(K key) {
        return map.remove(key);
    }

    /**
     * Remove all keys whose toString() starts with the provided prefix.
     * This is safe when keys are Strings or have meaningful string representations.
     */
    public void removeByPrefix(String prefix) {
        for (K key : map.keySet()) {
            if (key != null && key.toString().startsWith(prefix)) {
                map.remove(key);
            }
        }
    }
}
