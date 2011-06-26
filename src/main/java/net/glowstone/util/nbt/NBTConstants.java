package net.glowstone.util.nbt;

import java.nio.charset.Charset;

/**
 * A class which holds constant values.
 * @author Graham Edgecombe
 */
public final class NBTConstants {

    /**
     * The character set used by NBT (UTF-8).
     */
    public static final Charset CHARSET = Charset.forName("UTF-8");

    /**
     * Tag type constants.
     */
    public static final int TYPE_END = 0,
        TYPE_BYTE = 1,
        TYPE_SHORT = 2,
        TYPE_INT = 3,
        TYPE_LONG = 4,
        TYPE_FLOAT = 5,
        TYPE_DOUBLE = 6,
        TYPE_BYTE_ARRAY = 7,
        TYPE_STRING = 8,
        TYPE_LIST = 9,
        TYPE_COMPOUND = 10;

    /**
     * Default private constructor.
     */
    private NBTConstants() {

    }

}

