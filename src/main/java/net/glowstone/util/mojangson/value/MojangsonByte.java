package net.glowstone.util.mojangson.value;

import net.glowstone.util.mojangson.MojangsonToken;
import net.glowstone.util.mojangson.ex.MojangsonParseException;

public class MojangsonByte implements MojangsonValue<Byte> {
    private byte value;

    public MojangsonByte() {

    }

    public MojangsonByte(byte value) {
        this.value = value;
    }

    public Byte getValue() {
        return value;
    }

    public void setValue(byte value) {
        this.value = value;
    }

    @Override
    public Class getValueClass() {
        return byte.class;
    }

    @Override
    public void write(StringBuilder builder) {
        builder.append(value).append(MojangsonToken.BYTE_SUFFIX);
    }

    @Override
    public void read(String string) throws MojangsonParseException {
        Character lastChar = string.charAt(string.length() - 1);
        if (lastChar.toString().toLowerCase().charAt(0) == MojangsonToken.BYTE_SUFFIX.getSymbol()) {
            string = string.substring(0, string.length() - 1);
        }

        try {
            value = Byte.valueOf(string);
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + string + "\'", MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }
}
