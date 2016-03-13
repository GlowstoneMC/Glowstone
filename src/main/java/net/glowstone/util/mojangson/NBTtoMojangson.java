package net.glowstone.util.mojangson;

import net.glowstone.util.mojangson.value.*;
import net.glowstone.util.nbt.*;

import java.util.ArrayList;
import java.util.List;

public class NBTtoMojangson {

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
            return new MojangsonArray<>(values);
        }
        if (tag.getType() == TagType.BYTE_ARRAY) {
            ByteArrayTag byteArray = (ByteArrayTag) tag;
            List<MojangsonValue> values = new ArrayList<>();
            for (byte b : byteArray.getValue()) {
                values.add(toMojangson(new ByteTag(b)));
            }
            return new MojangsonArray<>(values);
        }
        if (tag.getType() == TagType.LIST) {
            ListTag list = (ListTag) tag;
            List<MojangsonValue> values = new ArrayList<>();
            for (Object to : list.getValue()) {
                if (!(to instanceof Tag))
                    continue;
                Tag tago = (Tag) to;
                values.add(toMojangson(tago));
            }
            return new MojangsonArray<>(values);
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

}
