package net.glowstone.io.nbt.reflection.serialization;

import net.glowstone.GlowServer;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.Tag;
import net.glowstone.util.nbt.TagType;

/**
 * Default NBT serialization for fields.
 */
public class NBTFieldSerialization extends SerializableNBTComponent<Object> {
    @Override
    public Object read(Tag tag) {
        if (tag.getType() == TagType.END) {
            return null;
        }
        return tag.getValue();
    }

    @Override
    public void write(String tagName, TagType objectType, Object object, CompoundTag compound) {
        if (tagName == null || object == null) {
            return;
        }
        if (objectType == TagType.END) {
            // check for possible tag types
            for (TagType t : TagType.values()) {
                if (t.getValueClass().isAssignableFrom(object.getClass())) {
                    objectType = t;
                    break;
                }
                if (t == TagType.BYTE && boolean.class.isAssignableFrom(object.getClass())) {
                    objectType = t;
                    break;
                }
            }
        }
        if (objectType == TagType.END) {
            // no match found, send a warning and do nothing
            GlowServer.logger.warning("Could not save field '" + tagName + "' of type '" + object.getClass().getName() + "' as no matching serializer was found.");
            return;
        }
        if (!objectType.getValueClass().isAssignableFrom(object.getClass())) {
            // there is a mismatch between the field type and the given value
            GlowServer.logger.warning("Could not save field '" + tagName + "' of type '" + object.getClass().getName() + "' as " + objectType);
            return;
        }
        Tag tag = new Tag(objectType) {
            @Override
            public Object getValue() {
                return object;
            }
        };
        compound.put(tagName, tag);
    }
}
