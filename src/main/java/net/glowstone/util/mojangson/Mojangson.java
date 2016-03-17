package net.glowstone.util.mojangson;

import net.glowstone.util.mojangson.ex.MojangsonParseException;
import net.glowstone.util.nbt.IntTag;
import net.glowstone.util.nbt.StringTag;
import net.glowstone.util.nbt.Tag;

import static net.glowstone.util.mojangson.MojangsonToken.STRING_QUOTES;

public class Mojangson {

    /**
     * Detects the Tag type of the Mojangson string, and parses it.
     * @param mojangson The Mojangson string
     * @return The parsed NBT Tag
     * @throws MojangsonParseException if the given Mojangson string could not be parsed.
     */
    public static Tag parseTag(String mojangson) throws MojangsonParseException {
        if (mojangson.startsWith(String.valueOf(STRING_QUOTES.getSymbol())) && mojangson.endsWith(String.valueOf(STRING_QUOTES.getSymbol()))) {
            return parseString(mojangson);
        }
        return parseInt(mojangson);
    }

    public static IntTag parseInt(String mojangson) throws MojangsonParseException {
        try {
            return new IntTag(Integer.valueOf(mojangson));
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + mojangson + "\'", MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }

    public static StringTag parseString(String mojangson) {
        Character lastChar = mojangson.charAt(mojangson.length() - 1);
        Character firstChar = mojangson.charAt(0);

        if (firstChar == STRING_QUOTES.getSymbol() && lastChar == STRING_QUOTES.getSymbol()) {
            return new StringTag(mojangson.substring(1, mojangson.length() - 1));
        } else {
            return new StringTag(mojangson);
        }
    }

}
