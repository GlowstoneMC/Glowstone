package net.glowstone.util.mojangson;

import static net.glowstone.util.mojangson.MojangsonToken.ARRAY_END;
import static net.glowstone.util.mojangson.MojangsonToken.ARRAY_START;
import static net.glowstone.util.mojangson.MojangsonToken.BYTE_SUFFIX;
import static net.glowstone.util.mojangson.MojangsonToken.COMPOUND_END;
import static net.glowstone.util.mojangson.MojangsonToken.COMPOUND_START;
import static net.glowstone.util.mojangson.MojangsonToken.DOUBLE_SUFFIX;
import static net.glowstone.util.mojangson.MojangsonToken.ELEMENT_PAIR_SEPERATOR;
import static net.glowstone.util.mojangson.MojangsonToken.ELEMENT_SEPERATOR;
import static net.glowstone.util.mojangson.MojangsonToken.FLOAT_SUFFIX;
import static net.glowstone.util.mojangson.MojangsonToken.LONG_SUFFIX;
import static net.glowstone.util.mojangson.MojangsonToken.SHORT_SUFFIX;
import static net.glowstone.util.mojangson.MojangsonToken.STRING_QUOTES;
import static net.glowstone.util.mojangson.MojangsonToken.WHITE_SPACE;

import java.util.ArrayList;
import java.util.List;
import net.glowstone.util.mojangson.ex.MojangsonParseException;
import net.glowstone.util.nbt.ByteArrayTag;
import net.glowstone.util.nbt.ByteTag;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.DoubleTag;
import net.glowstone.util.nbt.FloatTag;
import net.glowstone.util.nbt.IntArrayTag;
import net.glowstone.util.nbt.IntTag;
import net.glowstone.util.nbt.ListTag;
import net.glowstone.util.nbt.LongTag;
import net.glowstone.util.nbt.ShortTag;
import net.glowstone.util.nbt.StringTag;
import net.glowstone.util.nbt.Tag;
import net.glowstone.util.nbt.TagType;

public class Mojangson {

    /*
     * You shall not construct.
     */
    private Mojangson() {
    }

    /**
     * Detects the Tag type of the Mojangson string, and parses it. Convenience method for other
     * parse methods.
     *
     * <p>This method will fall back to an IntTag if it could not find an appropriate Tag type, and
     * to String if the value could not be parsed as an Integer either.
     *
     * @param mojangson The Mojangson string
     * @return The parsed NBT Tag
     * @throws MojangsonParseException if the given Mojangson string could not be parsed.
     */
    public static Tag parseTag(String mojangson) throws MojangsonParseException {
        if (mojangson.startsWith(String.valueOf(STRING_QUOTES.getSymbol())) && mojangson
                .endsWith(String.valueOf(STRING_QUOTES.getSymbol()))) {
            return parseString(mojangson);
        }
        if (mojangson.endsWith(String.valueOf(BYTE_SUFFIX.getSymbol()))) {
            return parseByte(mojangson);
        }
        if (mojangson.endsWith(String.valueOf(DOUBLE_SUFFIX.getSymbol()))) {
            return parseDouble(mojangson);
        }
        if (mojangson.endsWith(String.valueOf(LONG_SUFFIX.getSymbol()))) {
            return parseLong(mojangson);
        }
        if (mojangson.endsWith(String.valueOf(FLOAT_SUFFIX.getSymbol()))) {
            return parseFloat(mojangson);
        }
        if (mojangson.endsWith(String.valueOf(SHORT_SUFFIX.getSymbol()))) {
            return parseShort(mojangson);
        }
        if (mojangson.startsWith(String.valueOf(ARRAY_START.getSymbol())) && mojangson
                .endsWith(String.valueOf(ARRAY_END.getSymbol()))) {
            return parseArray(mojangson);
        }
        if (mojangson.startsWith(String.valueOf(COMPOUND_START.getSymbol())) && mojangson
                .endsWith(String.valueOf(COMPOUND_END.getSymbol()))) {
            return parseCompound(mojangson);
        }
        try {
            return parseInt(mojangson); // Check if the value is a valid integer
        } catch (MojangsonParseException e) {
            try {
                return parseLong(mojangson); // Could be a long if the number is too large
            } catch (MojangsonParseException e1) {
                try {
                    // Could be a decimal number without a type assignation, defaults to double
                    return parseDouble(
                            mojangson);
                } catch (MojangsonParseException e2) {
                    switch (mojangson) {
                        case "true":
                            return new ByteTag((byte)1);

                        case "false":
                            return new ByteTag((byte)0);

                        default:
                            // Couldn't find anything matching it, assuming it is a String.
                            return parseString(mojangson);
                    }
                }
            }
        }
    }

    /**
     * Parses an Integer value from a Mojangson string as an NBT IntTag.
     *
     * @param mojangson The Mojangson string
     * @return the parsed IntTag NBT value
     * @throws MojangsonParseException if the Mojangson string could not be parsed as an
     *         Integer value.
     */
    public static IntTag parseInt(String mojangson) throws MojangsonParseException {
        try {
            return new IntTag(Integer.parseInt(mojangson));
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + mojangson + "\'",
                    MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }

    /**
     * Parses an String value from a Mojangson string as an NBT StringTag.
     *
     * @param mojangson The Mojangson string
     * @return the parsed StringTag NBT value
     */
    public static StringTag parseString(String mojangson) {
        char lastChar = mojangson.charAt(mojangson.length() - 1);
        char firstChar = mojangson.charAt(0);

        if (firstChar == STRING_QUOTES.getSymbol() && lastChar == STRING_QUOTES.getSymbol()) {
            return new StringTag(mojangson.substring(1, mojangson.length() - 1));
        } else {
            return new StringTag(mojangson);
        }
    }

    /**
     * Parses a Long value from a Mojangson string as an NBT LongTag.
     *
     * @param mojangson The Mojangson string
     * @return the parsed LongTag NBT value
     * @throws MojangsonParseException if the Mojangson string could not be parsed as a Long
     *         value.
     */
    public static LongTag parseLong(String mojangson) throws MojangsonParseException {
        char lastChar = mojangson.charAt(mojangson.length() - 1);
        if (Character.toString(lastChar).toLowerCase().charAt(0) == MojangsonToken.LONG_SUFFIX.getSymbol()) {
            mojangson = mojangson.substring(0, mojangson.length() - 1);
        }

        try {
            return new LongTag(Long.parseLong(mojangson));
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + mojangson + "\'",
                    MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }

    /**
     * Parses a Double value from a Mojangson string as an NBT DoubleTag.
     *
     * @param mojangson The Mojangson string
     * @return the parsed DoubleTag NBT value
     * @throws MojangsonParseException if the Mojangson string could not be parsed as a
     *         Double value.
     */
    public static DoubleTag parseDouble(String mojangson) throws MojangsonParseException {
        char lastChar = mojangson.charAt(mojangson.length() - 1);
        if (Character.toString(lastChar).toLowerCase().charAt(0) == MojangsonToken.DOUBLE_SUFFIX
                .getSymbol()) {
            mojangson = mojangson.substring(0, mojangson.length() - 1);
        }

        try {
            return new DoubleTag(Double.parseDouble(mojangson));
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + mojangson + "\'",
                    MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }

    /**
     * Parses a Float value from a Mojangson string as an NBT FloatTag.
     *
     * @param mojangson The Mojangson string
     * @return the parsed FloatTag NBT value
     * @throws MojangsonParseException if the Mojangson string could not be parsed as a
     *         Flaot value.
     */
    public static FloatTag parseFloat(String mojangson) throws MojangsonParseException {
        char lastChar = mojangson.charAt(mojangson.length() - 1);
        if (Character.toString(lastChar).toLowerCase().charAt(0) == MojangsonToken.FLOAT_SUFFIX
                .getSymbol()) {
            mojangson = mojangson.substring(0, mojangson.length() - 1);
        }

        try {
            return new FloatTag(Float.parseFloat(mojangson));
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + mojangson + "\'",
                    MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }

    /**
     * Parses a Short value from a Mojangson string as an NBT ShortTag.
     *
     * @param mojangson The Mojangson string
     * @return the parsed ShortTag NBT value
     * @throws MojangsonParseException if the Mojangson string could not be parsed as a
     *         Short value.
     */
    public static ShortTag parseShort(String mojangson) throws MojangsonParseException {
        char lastChar = mojangson.charAt(mojangson.length() - 1);
        if (Character.toString(lastChar).toLowerCase().charAt(0) == MojangsonToken.SHORT_SUFFIX
                .getSymbol()) {
            mojangson = mojangson.substring(0, mojangson.length() - 1);
        }

        try {
            return new ShortTag(Short.parseShort(mojangson));
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + mojangson + "\'",
                    MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }

    /**
     * Parses a Byte value from a Mojangson string as an NBT ByteTag.
     *
     * @param mojangson The Mojangson string
     * @return the parsed ByteTag NBT value
     * @throws MojangsonParseException if the Mojangson string could not be parsed as a Byte
     *         value.
     */
    public static ByteTag parseByte(String mojangson) throws MojangsonParseException {
        char lastChar = mojangson.charAt(mojangson.length() - 1);
        if (Character.toString(lastChar).toLowerCase().charAt(0) == BYTE_SUFFIX.getSymbol()) {
            mojangson = mojangson.substring(0, mojangson.length() - 1);
        }

        try {
            return new ByteTag(Byte.parseByte(mojangson));
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + mojangson + "\'",
                    MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }

    /**
     * Parses a Compound from a Mojangson string as an NBT CompoundTag.
     *
     * @param mojangson The Mojangson string
     * @return the parsed CompoundTag NBT value
     * @throws MojangsonParseException if the Mojangson string could not be parsed as a
     *         Compound value.
     */
    public static CompoundTag parseCompound(String mojangson) throws MojangsonParseException {
        final int parseCompoundStart = 0;      // Parsing context magic value
        final int parseCompoundPairKey = 1;   // Parsing context magic value
        final int parseCompoundPairValue = 2; // Parsing context magic value

        int context = parseCompoundStart; // The current context of the parser
        String tmpkey = ""; // Temporary key being parsed, in its raw form
        String tmpval = ""; // Temporary value
        int scope
                = 0; // The scope level of the compound, this allows coherent nested arrays and
        // compounds.
        boolean inString = false; // The current character is part of a string inclusion
        CompoundTag tag = new CompoundTag();

        for (int index = 0; index < mojangson.length(); index++) {
            char character = mojangson.charAt(index);

            if (character == STRING_QUOTES.getSymbol()) {
                inString = !inString;
            }
            if (character == WHITE_SPACE.getSymbol()) {
                if (!inString) {
                    continue;
                }
            }
            if ((character == COMPOUND_START.getSymbol() || character == ARRAY_START.getSymbol())
                    && !inString) {
                scope++;
            }
            if ((character == COMPOUND_END.getSymbol() || character == ARRAY_END.getSymbol())
                    && !inString) {
                scope--;
            }
            if (context == parseCompoundStart) {
                if (character != COMPOUND_START.getSymbol()) {
                    throw new MojangsonParseException(
                            "Index: " + index + ", symbol: \'" + character + "\'",
                            MojangsonParseException.ParseExceptionReason.UNEXPECTED_SYMBOL);
                }
                context++;
                continue;
            }
            if (context == parseCompoundPairKey) {
                if (character == ELEMENT_PAIR_SEPERATOR.getSymbol() && scope <= 1) {
                    context++;
                    continue;
                }
                tmpkey += character;
                continue;
            }
            if (context == parseCompoundPairValue) {
                if ((character == ELEMENT_SEPERATOR.getSymbol() || character == COMPOUND_END
                        .getSymbol()) && scope <= 1 && !inString) {
                    context = parseCompoundPairKey;
                    tag.getValue().put(tmpkey, parseTag(tmpval));
                    tmpkey = tmpval = "";
                    continue;
                }
                tmpval += character;
            }
        }
        return tag;
    }

    /**
     * Parses an Array value from a Mojangson string.
     *
     * @param mojangson The Mojangson string
     * @return a ByteArrayTag value if the array contains byte values, an IntArrayTag value if the
     *         array contains int values or a ListTag with the array's elements.
     * @throws MojangsonParseException if the Mojangson string could not be parsed as an
     *         Array value.
     */
    public static Tag parseArray(String mojangson) throws MojangsonParseException {
        final int parseArrayStart = 0;   // Parsing context magic value
        final int parseArrayElement = 1; // Parsing context magic value

        int context = parseArrayStart; // The current context of the parser
        String tmpval = ""; // Temporary value being parsed, in its raw form

        // The scope level of the array, this allows coherent nested arrays and compounds.
        int scope = 0;

        boolean inString = false; // The current character is part of a string inclusion
        TagType tagType = null; // The element content type.
        List<Tag> values = new ArrayList<>();

        for (int index = 0; index < mojangson.length(); index++) {
            char character = mojangson.charAt(index);

            if (character == STRING_QUOTES.getSymbol()) {
                inString = !inString;
            }
            if (character == WHITE_SPACE.getSymbol()) {
                if (!inString) {
                    continue;
                }
            }
            if ((character == COMPOUND_START.getSymbol() || character == ARRAY_START.getSymbol())
                    && !inString) {
                scope++;
            }
            if ((character == COMPOUND_END.getSymbol() || character == ARRAY_END.getSymbol())
                    && !inString) {
                scope--;
            }
            if (context == parseArrayStart) {
                if (character != ARRAY_START.getSymbol()) {
                    throw new MojangsonParseException(
                            "Index: " + index + ", symbol: \'" + character + "\'",
                            MojangsonParseException.ParseExceptionReason.UNEXPECTED_SYMBOL);
                }
                context++;
                continue;
            }
            if (context == parseArrayElement) {
                if ((character == ELEMENT_SEPERATOR.getSymbol() || character == ARRAY_END
                        .getSymbol()) && scope <= 1 && !inString) {
                    if (tmpval.length() == 0) {
                        continue;
                    }
                    Tag val = parseTag(tmpval);

                    if (tagType == null) {
                        tagType = val.getType();
                    } else if (tagType != val.getType()) {
                        throw new MojangsonParseException(
                                "Index: " + index + ", value: \'" + tmpval + "\'",
                                MojangsonParseException.ParseExceptionReason.INCOMPATIBLE_TYPE);
                    }

                    values.add(val);
                    tmpval = "";
                    continue;
                }
                tmpval += character;
            }
        }
        if (tagType == TagType.BYTE) {
            byte[] bytes = new byte[values.size()];
            for (int i = 0; i < values.size(); i++) {
                bytes[i] = (byte) values.get(i).getValue();
            }
            return new ByteArrayTag(bytes);
        } else if (tagType == TagType.INT) {
            int[] ints = new int[values.size()];
            for (int i = 0; i < values.size(); i++) {
                ints[i] = (int) values.get(i).getValue();
            }
            return new IntArrayTag(ints);
        } else {
            return new ListTag<>(tagType, values);
        }
    }

    /**
     * Creates a Mojangson string from the given NBT Tag. Convenience method for generic tags
     * (Tag).
     *
     * @param tag the NBT Tag to convert
     * @return the converted Mojangson string
     */
    @SuppressWarnings("unchecked")
    public static String fromGenericTag(Tag tag) {
        switch (tag.getType()) {
            case BYTE:
                return fromTag((ByteTag) tag);
            case BYTE_ARRAY:
                return fromTag((ByteArrayTag) tag);
            case COMPOUND:
                return fromTag((CompoundTag) tag);
            case DOUBLE:
                return fromTag((DoubleTag) tag);
            case FLOAT:
                return fromTag((FloatTag) tag);
            case INT:
                return fromTag((IntTag) tag);
            case INT_ARRAY:
                return fromTag((IntArrayTag) tag);
            case LIST:
                return fromTag((ListTag<Tag>) tag);
            case LONG:
                return fromTag((LongTag) tag);
            case SHORT:
                return fromTag((ShortTag) tag);
            case STRING:
                return fromTag((StringTag) tag);
            default:
                return null;
        }
    }

    /**
     * Creates a Mojangson string from the given Byte Tag.
     *
     * @param tag the Byte Tag to convert
     * @return the converted Mojangson string
     */
    public static String fromTag(ByteTag tag) {
        return String.valueOf(tag.getValue()) + BYTE_SUFFIX;
    }

    /**
     * Creates a Mojangson string from the given ByteArray Tag.
     *
     * @param tag the ByteArray Tag to convert
     * @return the converted Mojangson string
     */
    public static String fromTag(ByteArrayTag tag) {
        StringBuilder builder = new StringBuilder();
        builder.append(ARRAY_START);
        boolean start = true;

        for (byte value : tag.getValue()) {
            ByteTag b = new ByteTag(value);
            if (start) {
                start = false;
            } else {
                builder.append(ELEMENT_SEPERATOR);
            }
            builder.append(fromTag(b));
        }
        builder.append(ARRAY_END);
        return builder.toString();
    }

    /**
     * Creates a Mojangson string from the given Compound Tag.
     *
     * @param tag the Compound Tag to convert
     * @return the converted Mojangson string
     */
    public static String fromTag(CompoundTag tag) {
        StringBuilder builder = new StringBuilder();
        builder.append(COMPOUND_START);
        boolean start = true;

        for (String key : tag.getValue().keySet()) {
            if (start) {
                start = false;
            } else {
                builder.append(ELEMENT_SEPERATOR);
            }

            builder.append(key).append(ELEMENT_PAIR_SEPERATOR);
            Tag value = tag.getValue().get(key);
            builder.append(fromGenericTag(value));
        }
        builder.append(COMPOUND_END);
        return builder.toString();
    }

    /**
     * Creates a Mojangson string from the given Double Tag.
     *
     * @param tag the Double Tag to convert
     * @return the converted Mojangson string
     */
    public static String fromTag(DoubleTag tag) {
        return String.valueOf(tag.getValue()) + MojangsonToken.DOUBLE_SUFFIX;
    }

    /**
     * Creates a Mojangson string from the given Float Tag.
     *
     * @param tag the Float Tag to convert
     * @return the converted Mojangson string
     */
    public static String fromTag(FloatTag tag) {
        return String.valueOf(tag.getValue()) + MojangsonToken.FLOAT_SUFFIX;
    }

    /**
     * Creates a Mojangson string from the given Int Tag.
     *
     * @param tag the Int Tag to convert
     * @return the converted Mojangson string
     */
    public static String fromTag(IntTag tag) {
        return String.valueOf(tag.getValue());
    }

    /**
     * Creates a Mojangson string from the given IntArray Tag.
     *
     * @param tag the IntArray Tag to convert
     * @return the converted Mojangson string
     */
    public static String fromTag(IntArrayTag tag) {
        StringBuilder builder = new StringBuilder();
        builder.append(ARRAY_START);
        boolean start = true;

        for (int value : tag.getValue()) {
            IntTag i = new IntTag(value);
            if (start) {
                start = false;
            } else {
                builder.append(ELEMENT_SEPERATOR);
            }
            builder.append(fromTag(i));
        }
        builder.append(ARRAY_END);
        return builder.toString();
    }

    /**
     * Creates a Mojangson string from the given List Tag.
     *
     * @param tag the List Tag to convert
     * @return the converted Mojangson string
     */
    public static String fromTag(ListTag<Tag> tag) {
        StringBuilder builder = new StringBuilder();
        builder.append(ARRAY_START);
        boolean start = true;

        for (Tag value : tag.getValue()) {
            if (start) {
                start = false;
            } else {
                builder.append(ELEMENT_SEPERATOR);
            }
            builder.append(fromGenericTag(value));
        }
        builder.append(ARRAY_END);
        return builder.toString();
    }

    /**
     * Creates a Mojangson string from the given Long Tag.
     *
     * @param tag the Long Tag to convert
     * @return the converted Mojangson string
     */
    public static String fromTag(LongTag tag) {
        return String.valueOf(tag.getValue()) + MojangsonToken.LONG_SUFFIX;
    }

    /**
     * Creates a Mojangson string from the given Short Tag.
     *
     * @param tag the Short Tag to convert
     * @return the converted Mojangson string
     */
    public static String fromTag(ShortTag tag) {
        return String.valueOf(tag.getValue()) + MojangsonToken.SHORT_SUFFIX;
    }

    /**
     * Creates a Mojangson string from the given String Tag.
     *
     * @param tag the String Tag to convert
     * @return the converted Mojangson string
     */
    public static String fromTag(StringTag tag) {
        return String.valueOf(MojangsonToken.STRING_QUOTES) + tag.getValue()
                + MojangsonToken.STRING_QUOTES;
    }
}
