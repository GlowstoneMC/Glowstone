package net.glowstone.entity.meta;

import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * A map for entity metadata.
 */
public class MetadataMap {

    private final Map<MetadataIndex, Object> map = new EnumMap<>(MetadataIndex.class);
    private final List<Entry> changes = new ArrayList<>(4);
    private final Class<? extends Entity> entityClass;

    public MetadataMap(Class<? extends Entity> entityClass) {
        this.entityClass = entityClass;
        set(MetadataIndex.STATUS, 0);  // all entities have to have at least this
    }

    public boolean containsKey(MetadataIndex index) {
        return map.containsKey(index);
    }

    public void set(MetadataIndex index, Object value) {
        // take numbers down to the correct precision
        if (value instanceof Number) {
            Number n = (Number) value;
            switch (index.getType()) {
                case BYTE:
                    value = n.byteValue();
                    break;
                case SHORT:
                    value = n.shortValue();
                    break;
                case INT:
                    value = n.intValue();
                    break;
                case FLOAT:
                    value = n.floatValue();
                    break;
            }
        }

        if (!index.getType().getDataType().isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Cannot assign " + value + " to " + index + ", expects " + index.getType());
        }

        if (!index.appliesTo(entityClass)) {
            throw new IllegalArgumentException("Index " + index + " does not apply to " + entityClass.getSimpleName() + ", only " + index.getAppliesTo().getSimpleName());
        }

        Object prev = map.put(index, value);
        if (!Objects.equals(prev, value)) {
            changes.add(new Entry(index, value));
        }
    }

    public Object get(MetadataIndex index) {
        return map.get(index);
    }

    public boolean getBit(MetadataIndex index, int bit) {
        return (getNumber(index).intValue() & bit) != 0;
    }

    public void setBit(MetadataIndex index, int bit, boolean status) {
        if (status) {
            set(index, getNumber(index).intValue() | bit);
        } else {
            set(index, getNumber(index).intValue() & ~bit);
        }
    }

    public Number getNumber(MetadataIndex index) {
        if (!containsKey(index)) {
            return 0;
        }
        Object o = get(index);
        if (!(o instanceof Number)) {
            throw new IllegalArgumentException("Index " + index + " is of non-number type " + index.getType());
        }
        return (Number) o;
    }

    public byte getByte(MetadataIndex index) {
        return get(index, MetadataType.BYTE, (byte) 0);
    }

    public short getShort(MetadataIndex index) {
        return get(index, MetadataType.SHORT, (short) 0);
    }

    public int getInt(MetadataIndex index) {
        return get(index, MetadataType.INT, 0);
    }

    public float getFloat(MetadataIndex index) {
        return get(index, MetadataType.FLOAT, 0f);
    }

    public String getString(MetadataIndex index) {
        return get(index, MetadataType.STRING, null);
    }

    public ItemStack getItem(MetadataIndex index) {
        return get(index, MetadataType.ITEM, null);
    }

    @SuppressWarnings("unchecked")
    private <T> T get(MetadataIndex index, MetadataType expected, T def) {
        if (index.getType() != expected) {
            throw new IllegalArgumentException("Cannot get " + index + ": is " + index.getType() + ", not " + expected);
        }
        T t = (T) map.get(index);
        if (t == null) {
            return def;
        }
        return t;
    }

    public List<Entry> getEntryList() {
        List<Entry> result = new ArrayList<>(map.size());
        for (Map.Entry<MetadataIndex, Object> entry : map.entrySet()) {
            result.add(new Entry(entry.getKey(), entry.getValue()));
        }
        Collections.sort(result);
        return result;
    }

    public List<Entry> getChanges() {
        Collections.sort(changes);
        return ImmutableList.copyOf(changes);
    }

    public void resetChanges() {
        changes.clear();
    }

    @Override
    public String toString() {
        return "MetadataMap{" +
                "map=" + map +
                ", entityClass=" + entityClass +
                '}';
    }

    public static class Entry implements Comparable<Entry> {
        public final MetadataIndex index;
        public final Object value;

        public Entry(MetadataIndex index, Object value) {
            this.index = index;
            this.value = value;
        }

        @Override
        public int compareTo(Entry o) {
            return o.index.getIndex() - index.getIndex();
        }

        @Override
        public String toString() {
            return index + "=" + value;
        }
    }
}
