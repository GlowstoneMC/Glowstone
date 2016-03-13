package net.glowstone.util.mojangson;

import net.glowstone.util.mojangson.ex.MojangsonParseException;
import net.glowstone.util.mojangson.value.*;

import static net.glowstone.util.mojangson.MojangsonToken.*;

public class MojangsonFinder {

    /**
     * Automatically detects the appropriate MojangsonValue from the given value.
     * @param value The value to parse
     * @return The resulting MojangsonValue. If the type couldn't be found, it falls back to MojangsonInt
     * @throws MojangsonParseException if the given value could not be parsed
     */
    public static MojangsonValue readFromValue(String value) throws MojangsonParseException {
        if (value.startsWith(String.valueOf(STRING_QUOTES.getSymbol())) && value.endsWith(String.valueOf(STRING_QUOTES.getSymbol()))) {
            MojangsonValue val = new MojangsonString();
            val.read(value);
            return val;
        }
        if (value.startsWith(String.valueOf(ARRAY_START.getSymbol())) && value.endsWith(String.valueOf(ARRAY_END.getSymbol()))) {
            MojangsonValue val = new MojangsonArray();
            val.read(value);
            return val;
        }
        if (value.startsWith(String.valueOf(COMPOUND_START.getSymbol())) && value.endsWith(String.valueOf(COMPOUND_END.getSymbol()))) {
            MojangsonValue val = new MojangsonCompound();
            val.read(value);
            return val;
        }
        if (value.endsWith(String.valueOf(BYTE_SUFFIX.getSymbol()))) {
            MojangsonValue val = new MojangsonByte();
            val.read(value);
            return val;
        }
        if (value.endsWith(String.valueOf(DOUBLE_SUFFIX.getSymbol())) || value.contains(".")) {
            MojangsonValue val = new MojangsonDouble();
            val.read(value);
            return val;
        }
        if (value.endsWith(String.valueOf(LONG_SUFFIX.getSymbol()))) {
            MojangsonValue val = new MojangsonLong();
            val.read(value);
            return val;
        }
        if (value.endsWith(String.valueOf(FLOAT_SUFFIX.getSymbol()))) {
            MojangsonValue val = new MojangsonFloat();
            val.read(value);
            return val;
        }
        if (value.endsWith(String.valueOf(SHORT_SUFFIX.getSymbol()))) {
            MojangsonValue val = new MojangsonShort();
            val.read(value);
            return val;
        }
        MojangsonValue val = new MojangsonInt();
        val.read(value);
        return val;
    }
}
