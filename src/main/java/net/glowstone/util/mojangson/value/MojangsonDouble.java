package net.glowstone.util.mojangson.value;

import net.glowstone.util.mojangson.MojangsonToken;
import net.glowstone.util.mojangson.ex.MojangsonParseException;

public class MojangsonDouble implements MojangsonValue<Double> {
    private double value;

    public MojangsonDouble() {

    }

    public MojangsonDouble(double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public Class getValueClass() {
        return double.class;
    }

    @Override
    public void write(StringBuilder builder) {
        builder.append(value).append(MojangsonToken.DOUBLE_SUFFIX);
    }

    @Override
    public void read(String string) throws MojangsonParseException {
        Character lastChar = string.charAt(string.length() - 1);
        if (lastChar.toString().toLowerCase().charAt(0) == MojangsonToken.DOUBLE_SUFFIX.getSymbol()) {
            string = string.substring(0, string.length() - 1);
        }

        try {
            value = Double.valueOf(string);
        } catch (NumberFormatException nfe) {
            throw new MojangsonParseException("\'" + string + "\'", MojangsonParseException.ParseExceptionReason.INVALID_FORMAT_NUM);
        }
    }
}
