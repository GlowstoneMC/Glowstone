package net.glowstone.util.nbt;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MojangsonUtil {

    /**
     * Transforms a CompoundTag into a JSON Object
     * @param compound The CompoundTag to convert
     * @return the resulting JSON Object
     */
    public static JSONObject compoundToJSON(CompoundTag compound) {
        JSONObject object = new JSONObject();
        for (String key : compound.getValue().keySet()) {
            Tag tag = (Tag) compound.getValue().get(key);
            Object value = tag.getValue();
            if (tag.getType() == TagType.COMPOUND) {
                object.put(key, compoundToJSON((CompoundTag) tag));
            } else if (tag.getType() == TagType.LIST) {
                ListTag listTag = (ListTag) tag;
                object.put(key, listToJSON(listTag));
            } else {
                object.put(key, value);
            }
        }
        return object;
    }

    /**
     * Transforms a ListTag into a JSON Array
     * @param list The ListTag to convert
     * @return the resulting JSON Array
     */
    public static JSONArray listToJSON(ListTag list) {
        JSONArray array = new JSONArray();
        for (Object object : list.getValue()) {
            Tag tag = (Tag) object;
            if (tag.getType() == TagType.COMPOUND) {
                array.add(compoundToJSON((CompoundTag) tag));
            } else if (tag.getType() == TagType.LIST) {
                ListTag listTag = (ListTag) tag;
                array.add(listToJSON(listTag));
            } else {
                array.add(tag.getValue());
            }
        }
        return array;
    }

    /**
     * Parses a JSON Object into a CompoundTag
     * @param json The JSON Object to parse
     * @return the resulting CompoundTag
     */
    public static CompoundTag parseCompound(JSONObject json) {
        CompoundTag compound = new CompoundTag();

        for (Object keyO : json.keySet()) {
            String key = (String) keyO;
            Object value = json.get(keyO);
            Tag tag = toTag(value);

            if (tag == null) {
                if (value instanceof JSONArray) {
                    tag = parseList((JSONArray) value);
                } else if (value instanceof JSONObject) {
                    tag = parseCompound((JSONObject) value);
                }
            }
            compound.put(key, tag);
        }
        return compound;
    }

    /**
     * Parses a JSON Array into a ListTag of the same element type
     * @param array The JSON Array to parse
     * @return the resulting ListTag
     */
    public static ListTag parseList(JSONArray array) {
        if (array.size() == 0) {
            return new ListTag(TagType.COMPOUND, array);
        }

        Object firstValue = array.get(0);
        Tag firstTag = toTag(firstValue);
        if (firstTag == null) {
            if (firstValue instanceof JSONArray) {
                firstTag = parseList((JSONArray) firstValue);
            } else if (firstValue instanceof JSONObject) {
                firstTag = parseCompound((JSONObject) firstValue);
            }
        }
        TagType type = firstTag.getType();
        List list = new ArrayList();
        for (Object object : array) {
            Tag tag = toTag(object);
            if (tag == null) {
                if (object instanceof JSONArray) {
                    tag = parseList((JSONArray) object);
                } else if (object instanceof JSONObject) {
                    tag = parseCompound((JSONObject) object);
                }
            }
            list.add(tag);
        }

        return new ListTag(type, list);
    }

    /**
     * Transforms a value into the appropriate NBT tag
     * @param value The value of the NBT tag
     * @return the resultant NBT tag, null if the given value is not of an appropriate type or if it is a JSONObject or JSON Array
     */
    private static Tag toTag(Object value) {
        if (value instanceof Integer) {
            return new IntTag((int) value);
        } else if (value instanceof Byte) {
            return new ByteTag((byte) value);
        } else if (value instanceof Short) {
            return new ShortTag((short) value);
        } else if (value instanceof Long) {
            return new LongTag((long) value);
        } else if (value instanceof Float) {
            return new FloatTag((float) value);
        } else if (value instanceof Double) {
            return new DoubleTag((double) value);
        } else if (value instanceof byte[]) {
            return new ByteArrayTag((byte[]) value);
        } else if (value instanceof String) {
            return new StringTag((String) value);
        } else if (value instanceof int[]) {
            return new IntArrayTag((int[]) value);
        }
        return null;
    }
}
