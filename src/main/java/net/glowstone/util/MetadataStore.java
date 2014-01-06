package net.glowstone.util;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A reusable implementation of Metadatable.
 */
public final class MetadataStore implements Metadatable {

    private final ConcurrentMap<String, List<MetadataValue>> values = new ConcurrentHashMap<String, List<MetadataValue>>();

    private List<MetadataValue> getList(String key) {
        List<MetadataValue> result = values.get(key);
        if (result == null) {
            result = new LinkedList<MetadataValue>();
            values.putIfAbsent(key, result);
        }
        return result;
    }

    @Override
    public void setMetadata(String metadataKey, MetadataValue newMetadataValue) {
        removeMetadata(metadataKey, newMetadataValue.getOwningPlugin());
        List<MetadataValue> list = getList(metadataKey);
        list.add(newMetadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String metadataKey) {
        return new ArrayList<MetadataValue>(getList(metadataKey));
    }

    @Override
    public boolean hasMetadata(String metadataKey) {
        return values.containsKey(metadataKey) && values.get(metadataKey).size() > 0;
    }

    @Override
    public void removeMetadata(String metadataKey, Plugin owningPlugin) {
        List<MetadataValue> list = getList(metadataKey);
        Iterator<MetadataValue> iter = list.iterator();
        while (iter.hasNext()) {
            if (iter.next().getOwningPlugin() == owningPlugin) {
                iter.remove();
            }
        }
    }
}
