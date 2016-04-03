package net.glowstone.util.mojangson;

import net.glowstone.util.mojangson.ex.MojangsonParseException;
import net.glowstone.util.nbt.*;

import java.util.ArrayList;
import java.util.List;

import static net.glowstone.util.mojangson.MojangsonToken.*;

public class Mojangson {

    /*
     * You shall not construct.
     */
    private Mojangson() {}

    /**
     * Detects the Tag type of the Mojangson string, and parses it. Convenience method for other parse methods.
     * This method will fall back to an IntTag if it could not find an appropriate Tag type, and to String if the value
     * could not be parsed as an Integer either.
     * @param mojangson The Mojangson string
     * @return The parsed NBT Tag
     * @throws MojangsonParseException if the given Mojangson string could not be parsed.
     */
    public static Tag parseTag(String mojangson) throws MojangsonParseException {
        if (mojangson.startsWith(String.valueOf(STRING_QUOTES.getSymbol())) && mojangson.endsWith(String.valueOf(STRING_QUOTES.getSymbol()))) {
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
        if (mojangson.startsWith(String.valueOf(ARRAY_START.getSymbol())) && mojangson.endsWith(String.valueOf(ARRAY_END.getSymbol()))) {
            return parseArray(mojangson);
        }
        if (mojangson.startsWith(String.valueOf(COMPOUND_START.getSymbol())) && mojangson.endsWith(String.valueOf(COMPOUND_END.getSymbol()))) {
            return parseCompound(mojangson);
        }
        try {
            return parseInt(mojangson);
        } catch (MojangsonParseException e) {
            return parseString(mojangson);
        }
    }

    /**
     * Parses an Integer value from a Mojangson string as an NBT IntTag
     * @param mojangson The Mojangson string
     * @return the parsed IntTag NBT value
     * @throws MojangsonParseException if the Mojangson string could not be parsed as an Integer value.
     */
    public static IntTag parseInt(String mojangson) throws MojangsonParseException {
        try {
            return new IntTag(Integer.valueOf(mojangson));
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + mojangson + "\'", MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }

    /**
     * Parses an String value from a Mojangson string as an NBT StringTag
     * @param mojangson The Mojangson string
     * @return the parsed StringTag NBT value
     */
    public static StringTag parseString(String mojangson) {
        Character lastChar = mojangson.charAt(mojangson.length() - 1);
        Character firstChar = mojangson.charAt(0);

        if (firstChar == STRING_QUOTES.getSymbol() && lastChar == STRING_QUOTES.getSymbol()) {
            return new StringTag(mojangson.substring(1, mojangson.length() - 1));
        } else {
            return new StringTag(mojangson);
        }
    }

    /**
     * Parses a Long value from a Mojangson string as an NBT LongTag
     * @param mojangson The Mojangson string
     * @return the parsed LongTag NBT value
     * @throws MojangsonParseException if the Mojangson string could not be parsed as a Long value.
     */
    public static LongTag parseLong(String mojangson) throws MojangsonParseException {
        Character lastChar = mojangson.charAt(mojangson.length() - 1);
        if (lastChar.toString().toLowerCase().charAt(0) == MojangsonToken.LONG_SUFFIX.getSymbol()) {
            mojangson = mojangson.substring(0, mojangson.length() - 1);
        }

        try {
            return new LongTag(Long.valueOf(mojangson));
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + mojangson + "\'", MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }

    /**
     * Parses a Double value from a Mojangson string as an NBT DoubleTag
     * @param mojangson The Mojangson string
     * @return the parsed DoubleTag NBT value
     * @throws MojangsonParseException if the Mojangson string could not be parsed as a Double value.
     */
    public static DoubleTag parseDouble(String mojangson) throws MojangsonParseException {
        Character lastChar = mojangson.charAt(mojangson.length() - 1);
        if (lastChar.toString().toLowerCase().charAt(0) == MojangsonToken.DOUBLE_SUFFIX.getSymbol()) {
            mojangson = mojangson.substring(0, mojangson.length() - 1);
        }

        try {
            return new DoubleTag(Double.valueOf(mojangson));
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + mojangson + "\'", MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }

    /**
     * Parses a Float value from a Mojangson string as an NBT FloatTag
     * @param mojangson The Mojangson string
     * @return the parsed FloatTag NBT value
     * @throws MojangsonParseException if the Mojangson string could not be parsed as a Flaot value.
     */
    public static FloatTag parseFloat(String mojangson) throws MojangsonParseException {
        Character lastChar = mojangson.charAt(mojangson.length() - 1);
        if (lastChar.toString().toLowerCase().charAt(0) == MojangsonToken.FLOAT_SUFFIX.getSymbol()) {
            mojangson = mojangson.substring(0, mojangson.length() - 1);
        }

        try {
            return new FloatTag(Float.valueOf(mojangson));
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + mojangson + "\'", MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }

    /**
     * Parses a Short value from a Mojangson string as an NBT ShortTag
     * @param mojangson The Mojangson string
     * @return the parsed ShortTag NBT value
     * @throws MojangsonParseException if the Mojangson string could not be parsed as a Short value.
     */
    public static ShortTag parseShort(String mojangson) throws MojangsonParseException {
        Character lastChar = mojangson.charAt(mojangson.length() - 1);
        if (lastChar.toString().toLowerCase().charAt(0) == MojangsonToken.SHORT_SUFFIX.getSymbol()) {
            mojangson = mojangson.substring(0, mojangson.length() - 1);
        }

        try {
            return new ShortTag(Short.valueOf(mojangson));
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + mojangson + "\'", MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }

    /**
     * Parses a Byte value from a Mojangson string as an NBT ByteTag
     * @param mojangson The Mojangson string
     * @return the parsed ByteTag NBT value
     * @throws MojangsonParseException if the Mojangson string could not be parsed as a Byte value.
     */
    public static ByteTag parseByte(String mojangson) throws MojangsonParseException {
        Character lastChar = mojangson.charAt(mojangson.length() - 1);
        if (lastChar.toString().toLowerCase().charAt(0) == BYTE_SUFFIX.getSymbol()) {
            mojangson = mojangson.substring(0, mojangson.length() - 1);
        }

        try {
            return new ByteTag(Byte.valueOf(mojangson));
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + mojangson + "\'", MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }

    /**
     * Parses a Compound from a Mojangson string as an NBT CompoundTag
     * @param mojangson The Mojangson string
     * @return the parsed CompoundTag NBT value
     * @throws MojangsonParseException if the Mojangson string could not be parsed as a Compound value.
     */
    public static CompoundTag parseCompound(String mojangson) throws MojangsonParseException {
        final int parseCompoundStart = 0;      // Parsing context magic value
        final int parseCompoundPairKey = 1;   // Parsing context magic value
        final int parseCompoundPairValue = 2; // Parsing context magic value

        int context = parseCompoundStart; // The current context of the parser
        String tmpval = "", tmpkey = ""; // Temporary key/value being parsed, in its raw form
        int scope = 0; // The scope level of the compound, this allows coherent nested arrays and compounds.
        boolean inString = false; // The current character is part of a string inclusion
        CompoundTag tag = new CompoundTag();

        for (int index = 0; index < mojangson.length(); index++) {
            Character character = mojangson.charAt(index);

            if (character == STRING_QUOTES.getSymbol()) {
                inString = !inString;
            }
            if (character == WHITE_SPACE.getSymbol()) {
                if (!inString)
                    continue;
            }
            if ((character == COMPOUND_START.getSymbol() || character == ARRAY_START.getSymbol()) && !inString) {
                scope++;
            }
            if ((character == COMPOUND_END.getSymbol() || character == ARRAY_END.getSymbol()) && !inString) {
                scope--;
            }
            if (context == parseCompoundStart) {
                if (character != COMPOUND_START.getSymbol()) {
                    throw new MojangsonParseException("Index: " + index + ", symbol: \'" + character + "\'", MojangsonParseException.ParseExceptionReason.UNEXPECTED_SYMBOL);
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
                if ((character == ELEMENT_SEPERATOR.getSymbol() || character == COMPOUND_END.getSymbol()) && scope <= 1 && !inString) {
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
     * Parses an Array value from a Mojangson string
     * @param mojangson The Mojangson string
     * @return a ByteArrayTag value if the array contains byte values, an IntArrayTag value if the array contains int values or a ListTag with the array's elements.
     * @throws MojangsonParseException if the Mojangson string could not be parsed as an Array value.
     */
    public static Tag parseArray(String mojangson) throws MojangsonParseException {
        final int parseArrayStart = 0;   // Parsing context magic value
        final int parseArrayElement = 1; // Parsing context magic value

        int context = parseArrayStart; // The current context of the parser
        String tmpval = ""; // Temporary value being parsed, in its raw form
        int scope = 0; // The scope level of the array, this allows coherent nested arrays and compounds.
        boolean inString = false; // The current character is part of a string inclusion
        TagType tagType = null; // The element content type.
        List<Tag> values = new ArrayList<>();

        for (int index = 0; index < mojangson.length(); index++) {
            Character character = mojangson.charAt(index);

            if (character == STRING_QUOTES.getSymbol()) {
                inString = !inString;
            }
            if (character == WHITE_SPACE.getSymbol()) {
                if (!inString)
                    continue;
            }
            if ((character == COMPOUND_START.getSymbol() || character == ARRAY_START.getSymbol()) && !inString) {
                scope++;
            }
            if ((character == COMPOUND_END.getSymbol() || character == ARRAY_END.getSymbol()) && !inString) {
                scope--;
            }
            if (context == parseArrayStart) {
                if (character != ARRAY_START.getSymbol()) {
                    throw new MojangsonParseException("Index: " + index + ", symbol: \'" + character + "\'", MojangsonParseException.ParseExceptionReason.UNEXPECTED_SYMBOL);
                }
                context++;
                continue;
            }
            if (context == parseArrayElement) {
                if ((character == ELEMENT_SEPERATOR.getSymbol() || character == ARRAY_END.getSymbol()) && scope <= 1 && !inString) {
                    if (tmpval.length() == 0) {
                        continue;
                    }
                    Tag val = (Tag) parseTag(tmpval);

                    if (tagType == null) {
                        tagType = val.getType();
                    } else if (tagType != val.getType()) {
                        throw new MojangsonParseException("Index: " + index + ", value: \'" + tmpval + "\'", MojangsonParseException.ParseExceptionReason.INCOMPATIBLE_TYPE);
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
     * Creates a Mojangosn string from the given NBT tag.
     *
     * @param nbt the NBT to convert
     * @return the converted Mojangson string
     */
    public static String fromNBT(Tag nbt) {
        return nbt.toMojangson();
    }

}
