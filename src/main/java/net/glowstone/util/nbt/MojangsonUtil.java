package net.glowstone.util.nbt;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

public class MojangsonUtil {

    /**
     * Converts a JSON Object into a standart Mojangson string
     * @param json The JSON Object to convert
     * @return the resulting Mojangson string
     */
    public static String convertJSONtoMojangson(JSONObject json) {
        // This removes double-quotes from key names.
        // For example, the JSON string {"foo":"bar"} would be
        // changed to {foo:"bar"}
        return json.toJSONString().replaceAll("\"+([^\"]+)\"+(?=:)", "$1");
    }

    /**
     * Converts a Mojangson string into a JSON Object
     * @param mojangson The Mojangson string to convert
     * @return the resulting JSON object
     * @throws ParseException if the conversion of the Mojangson string into valid JSON was unsuccessful.
     */
    public static JSONObject convertMojangsonToJSON(String mojangson) throws ParseException {
        int len = mojangson.length();
        StringBuilder builder = new StringBuilder();
        List<Integer> indexes = new ArrayList<>();
        boolean inArray = false;

        for (int index = 0; index < len; index++) {
            if (index == 0 || index == len - 1) continue;
            char symbol = mojangson.charAt(index);
            char prec = mojangson.charAt(index - 1);
            char next = mojangson.charAt(index + 1);

            if (symbol == '\"') {
                continue;
            }
            if (symbol == '[') {
                inArray = true;
            } else if (symbol == ']' || symbol == '{') {
                inArray = false;
            }
            if (inArray) {
                continue;
            }
            if (prec == '{' || prec == ',') {
                indexes.add(index - 1);
            } else if (next == ':') {
                indexes.add(index);
            }
        }

        for (int index = 0; index < len; index++) {
            builder.append(mojangson.charAt(index));
            if (indexes.contains(index)) {
                builder.append('\"');
            }
        }
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(builder.toString());
    }

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
                if (value.getClass() == JSONArray.class) {
                    tag = parseList((JSONArray) value);
                } else if (value.getClass() == JSONObject.class) {
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
            if (firstValue.getClass() == JSONArray.class) {
                firstTag = parseList((JSONArray) firstValue);
            } else if (firstValue.getClass() == JSONObject.class) {
                firstTag = parseCompound((JSONObject) firstValue);
            }
        }
        TagType type = firstTag.getType();
        List list = new ArrayList();
        for (Object object : array) {
            Tag tag = toTag(object);
            if (tag == null) {
                if (object.getClass() == JSONArray.class) {
                    tag = parseList((JSONArray) object);
                } else if (object.getClass() == JSONObject.class) {
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
     * @return the resultant NBT tag, null if the given value is not of an appropriate type or if it is a JSON Object or JSON Array
     */
    private static Tag toTag(Object value) {
        if (value.getClass() == int.class) {
            return new IntTag((int) value);
        } else if (value.getClass() == byte.class) {
            return new ByteTag((byte) value);
        } else if (value.getClass() == short.class) {
            return new ShortTag((short) value);
        } else if (value.getClass() == long.class) {
            return new LongTag((long) value);
        } else if (value.getClass() == float.class) {
            return new FloatTag((float) value);
        } else if (value.getClass() == double.class) {
            return new DoubleTag((double) value);
        } else if (value.getClass() == byte[].class) {
            return new ByteArrayTag((byte[]) value);
        } else if (value.getClass() == String.class) {
            return new StringTag((String) value);
        } else if (value.getClass() == int[].class) {
            return new IntArrayTag((int[]) value);
        }
        return null;
    }
}
