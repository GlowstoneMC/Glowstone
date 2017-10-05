package net.glowstone.io.nbt.reflection;

import net.glowstone.io.nbt.reflection.serialization.NBTFieldSerialization;
import net.glowstone.io.nbt.reflection.serialization.SerializableNBTComponent;
import net.glowstone.util.nbt.TagType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares an NBT field that can be stored in an entity's NBT tag, or a specified group.
 * <br>
 * This annotation can be applied to any field, as well as getters and setters. Note that for getter and setter methods,
 * the NBT annotation should be applied to both corresponding methods with the same properties.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface NBT {
    /**
     * The name of this NBT field.
     *
     * @return the name of this NBT field
     */
    String name();

    /**
     * The type of this NBT field.
     * <br>
     * If this is not overridden or is set to {@link TagType#END} and no {@link NBT#serialization()} was set,
     * the default field serializer will attempt to find a matching {@link TagType} to save the field.
     *
     * @return the type of this NBT field
     */
    TagType type() default TagType.END;

    /**
     * The type of elements contained in this field if its {@link NBT#type()} is set to {@link TagType#LIST}
     *
     * @return the type of elements contained in this field
     */
    TagType listType() default TagType.END;

    /**
     * Whether this field is required when reading and saving it. If the value of the field is null, an error will occur.
     *
     * @return whether this field is required when reading and saving it
     */
    boolean required() default false;

    /**
     * Whether the value of this NBT field should be inverted when read and saved.
     * <br>
     * This is only applicable to NBT fields of boolean type ({@link TagType#BYTE}). If used with another type, it will be silently ignored.
     *
     * @return whether the value of this NBT field should be inverted when read and saved
     */
    boolean inverted() default false;

    /**
     * The parent group name for this NBT field. If this is set to an empty string (""),
     * or if the given group does not exist, it will default to the entity's root NBT tag.
     *
     * @return the group name for this NBT field, or an empty string if there is no parent group
     */
    String group() default "";

    /**
     * The serialization class used to read and save this field as an NBT format.
     *
     * @return the serialization class used to read and save this field as an NBT format
     */
    Class<? extends SerializableNBTComponent> serialization() default NBTFieldSerialization.class;
}
