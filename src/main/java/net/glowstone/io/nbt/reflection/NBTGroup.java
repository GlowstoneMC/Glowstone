package net.glowstone.io.nbt.reflection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares a new child NBT compound as a sub-group to an entity's storage, which can be referenced by other NBT fields as their parent.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NBTGroup {
    String name();
}
