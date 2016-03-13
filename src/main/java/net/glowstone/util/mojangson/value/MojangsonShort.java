package net.glowstone.util.mojangson.value;

import net.glowstone.util.mojangson.MojangsonToken;
import net.glowstone.util.mojangson.ex.MojangsonParseException;

public class MojangsonShort implements MojangsonValue<Short> {
    private short value;

    public MojangsonShort() {

    }

    public MojangsonShort(short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }

    public void setValue(short value) {
        this.value = value;
    }

    @Override
    public Class getValueClass() {
        return short.class;
    }

    @Override
    public void write(StringBuilder builder) {
        builder.append(value).append(MojangsonToken.SHORT_SUFFIX);
    }

    @Override
    public void read(String string) throws MojangsonParseException {
        Character lastChar = string.charAt(string.length() - 1);
        if (lastChar.toString().toLowerCase().charAt(0) == MojangsonToken.SHORT_SUFFIX.getSymbol()) {
            string = string.substring(0, string.length() - 1);
        }

        try {
            value = Short.valueOf(string);
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + string + "\'", MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }
}
