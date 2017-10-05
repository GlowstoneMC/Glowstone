package net.glowstone.io.nbt.reflection;

import net.glowstone.io.nbt.reflection.serialization.NBTFieldSerialization;
import net.glowstone.io.nbt.reflection.serialization.SerializableNBTComponent;
import net.glowstone.util.nbt.TagType;

/**
 * Declares a default NBT tag that will be stored when saving the entity's NBT tag.
 * <br>
 * This tag may be overridden by the extension of the entity's class.
 */
public @interface NBTDefault {
    String name();

    TagType type() default TagType.END;

    TagType listType() default TagType.END;

    String group() default "";

    Class<? extends SerializableNBTComponent> serialization() default NBTFieldSerialization.class;

    // Defaults

    int intValue() default 0;

    byte byteValue() default (byte) 0;

    short shortValue() default (short) 0;

    long longValue() default (long) 0;

    float floatValue() default 0.0F;

    double doubleValue() default 0.0;

    String stringValue() default "";

    boolean booleanValue() default false;
}
