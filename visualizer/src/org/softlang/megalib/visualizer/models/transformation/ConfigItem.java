/*
 *  All rights reserved.
 */
package org.softlang.megalib.visualizer.models.transformation;

import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author Dmitri Nikonov <dnikonov at uni-koblenz.de>
 */
public class ConfigItem<K, V> {

    private SortedMap<K, V> backend = new TreeMap<>();

    public boolean contains(K key) {
        return backend.containsKey(key);
    }

    public ConfigItem<K, V> put(K key, V value) {
        backend.put(key, value);

        return this;
    }

    public V get(K key) {
        return backend.get(key);
    }

    @Override
    public String toString() {
        return backend.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.backend);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ConfigItem<?, ?> other = (ConfigItem<?, ?>) obj;
        if (!Objects.equals(this.backend, other.backend))
            return false;
        return true;
    }

}
