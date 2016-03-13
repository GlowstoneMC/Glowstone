package net.glowstone.util.mojangson.value;

import net.glowstone.util.mojangson.MojangsonToken;
import net.glowstone.util.mojangson.ex.MojangsonParseException;

public class MojangsonLong implements MojangsonValue<Long> {
    private long value;

    public MojangsonLong() {

    }

    public MojangsonLong(long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    @Override
    public void write(StringBuilder builder) {
        builder.append(value).append(MojangsonToken.LONG_SUFFIX);
    }

    @Override
    public void read(String string) throws MojangsonParseException {
        Character lastChar = string.charAt(string.length() - 1);
        if (lastChar.toString().toLowerCase().charAt(0) == MojangsonToken.LONG_SUFFIX.getSymbol()) {
            string = string.substring(0, string.length() - 1);
        }

        try {
            value = Long.valueOf(string);
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + string + "\'", MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }
}
