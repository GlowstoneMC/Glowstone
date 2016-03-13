package net.glowstone.util.mojangson;

import net.glowstone.util.mojangson.value.*;
import net.glowstone.util.nbt.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NBTtoMojangson {

    // Equivalents of MojangsonValues and Tag Types
    private static HashMap<Class<? extends MojangsonValue>, TagType> equivalentType = new HashMap<>();

    /**
     * Converts a NBT Tag into a Mojangson Value.
     * @param tag The NBT tag to convert
     * @return The resulting Mojangson Value
     */
    public static MojangsonValue toMojangson(Tag tag) {
        if (tag.getType() == TagType.BYTE) {
            return new MojangsonByte((byte) tag.getValue());
        }
        if (tag.getType() == TagType.SHORT) {
            return new MojangsonShort((short) tag.getValue());
        }
        if (tag.getType() == TagType.INT) {
            return new MojangsonInt((int) tag.getValue());
        }
        if (tag.getType() == TagType.LONG) {
            return new MojangsonLong((long) tag.getValue());
        }
        if (tag.getType() == TagType.FLOAT) {
            return new MojangsonFloat((float) tag.getValue());
        }
        if (tag.getType() == TagType.DOUBLE) {
            return new MojangsonDouble((double) tag.getValue());
        }
        if (tag.getType() == TagType.STRING) {
            return new MojangsonString((String) tag.getValue());
        }
        if (tag.getType() == TagType.INT_ARRAY) {
            IntArrayTag intArray = (IntArrayTag) tag;
            List<MojangsonValue> values = new ArrayList<>();
            for (int i : intArray.getValue()) {
                values.add(toMojangson(new IntTag(i)));
            }
            return new MojangsonArray<>(MojangsonInt.class, values);
        }
        if (tag.getType() == TagType.BYTE_ARRAY) {
            ByteArrayTag byteArray = (ByteArrayTag) tag;
            List<MojangsonValue> values = new ArrayList<>();
            for (byte b : byteArray.getValue()) {
                values.add(toMojangson(new ByteTag(b)));
            }
            return new MojangsonArray<>(MojangsonByte.class, values);
        }
        if (tag.getType() == TagType.LIST) {
            ListTag list = (ListTag) tag;
            List<MojangsonValue> values = new ArrayList<>();
            TagType valType = null;
            for (Object to : list.getValue()) {
                if (!(to instanceof Tag))
                    continue;
                Tag tago = (Tag) to;
                valType = tago.getType();
                values.add(toMojangson(tago));
            }
            return new MojangsonArray<>(getMojangsonEquivalent(valType), values);
        }
        if (tag.getType() == TagType.COMPOUND) {
            CompoundTag compoundTag = (CompoundTag) tag;
            MojangsonCompound compound = new MojangsonCompound();
            for (String key : compoundTag.getValue().keySet()) {
                Tag t = compoundTag.getValue().get(key);
                compound.put(key, toMojangson(t));
            }
            return compound;
        }
        return null;
    }

    public static Tag toNBT(MojangsonValue mojangson) {
        TagType type = equivalentType.get(mojangson.getClass());
        if (type == TagType.LIST) {
            MojangsonArray array = ((MojangsonArray) mojangson);
            if (array.getType() == MojangsonInt.class) {
                List<Integer> values = new ArrayList<>();
                for (Object o : array) {
                    MojangsonInt vo = (MojangsonInt) o;
                    values.add(vo.getValue());
                }
                int[] ret = new int[values.size()];
                for (int i = 0; i < ret.length; i++)
                    ret[i] = values.get(i);
                return new IntArrayTag(ret);
            } else if (array.getType() == MojangsonByte.class) {
                List<Byte> values = new ArrayList<>();
                for (Object o : array) {
                    MojangsonByte vo = (MojangsonByte) o;
                    values.add(vo.getValue());
                }
                byte[] ret = new byte[values.size()];
                for (int i = 0; i < ret.length; i++)
                    ret[i] = values.get(i);
                return new ByteArrayTag(ret);
            } else {
                List<Tag> tags = new ArrayList<>();
                TagType elemType = null;
                for (Object o : array) {
                    MojangsonValue vo = (MojangsonValue) o;
                    for (TagType t : TagType.values()) {
                        if (t.getValueClass() == vo.getValueClass()) {
                            elemType = t;
                        }
                    }
                    tags.add(toNBT(vo));
                }
                return new ListTag<>(elemType, tags);
            }
        } else if (type == TagType.COMPOUND) {
            CompoundTag tag = new CompoundTag();
            MojangsonCompound compound = (MojangsonCompound) mojangson;
            for (String key : compound.keySet()) {
                MojangsonValue val = compound.get(key);
                Tag t = toNBT(val);
                tag.getValue().put(key, t);
            }
            return tag;
        } else {
            try {
                // "<g> Reflect the shit out of it?"
                return type.getTagClass().getConstructor(type.getValueClass()).newInstance(mojangson.getValue());
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Gets the Mojangson Value Type class equivalent to the given Tag Type.
     * @param type The equivalent Tag Type
     * @return The equivalent MojangsonValue class
     */
    private static Class<? extends MojangsonValue> getMojangsonEquivalent(TagType type) {
        for (Class<? extends MojangsonValue> val : equivalentType.keySet()) {
            if (equivalentType.get(val) == type)
                return val;
        }
        return null;
    }

    static {
        // (Mojangson Value Class, NBT Type)
        equivalentType.put(MojangsonByte.class, TagType.BYTE);
        equivalentType.put(MojangsonShort.class, TagType.SHORT);
        equivalentType.put(MojangsonInt.class, TagType.INT);
        equivalentType.put(MojangsonLong.class, TagType.LONG);
        equivalentType.put(MojangsonFloat.class, TagType.FLOAT);
        equivalentType.put(MojangsonDouble.class, TagType.DOUBLE);
        equivalentType.put(MojangsonString.class, TagType.STRING);
        equivalentType.put(MojangsonArray.class, TagType.LIST);
        equivalentType.put(MojangsonCompound.class, TagType.COMPOUND);
    }
}
