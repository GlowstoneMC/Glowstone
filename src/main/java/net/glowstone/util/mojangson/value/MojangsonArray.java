package net.glowstone.util.mojangson.value;

import net.glowstone.util.mojangson.MojangsonFinder;
import net.glowstone.util.mojangson.ex.MojangsonParseException;

import java.util.ArrayList;
import java.util.List;

import static net.glowstone.util.mojangson.MojangsonToken.*;

public class MojangsonArray<T extends MojangsonValue> extends ArrayList<T> implements MojangsonValue<List<T>> {

    private static final int C_ARRAY_START = 0;   // Parsing context
    private static final int C_ARRAY_ELEMENT = 1; // Parsing context

    public MojangsonArray() {

    }

    public MojangsonArray(List<T> list) {
        addAll(list);
    }

    @Override
    public void write(StringBuilder builder) {
        builder.append(ARRAY_START);
        boolean start = true;

        for (MojangsonValue value : this) {
            if (start) {
                start = false;
            } else {
                builder.append(ELEMENT_SEPERATOR);
            }
            value.write(builder);
        }
        builder.append(ARRAY_END);
    }

    @Override
    public List<T> getValue() {
        return this;
    }

    @Override
    public void read(String string) throws MojangsonParseException {
        int context = C_ARRAY_START;
        String tmpval = "";
        int scope = 0;

        for (int index = 0; index < string.length(); index++) {
            Character character = string.charAt(index);

            if (character == COMPOUND_START.getSymbol() || character == ARRAY_START.getSymbol()) {
                scope++;
            }
            if (character == COMPOUND_END.getSymbol() || character == ARRAY_END.getSymbol()) {
                scope--;
            }
            if (context == C_ARRAY_START) {
                if (character != ARRAY_START.getSymbol()) {
                    parseException(index, character);
                    return;
                }
                context++;
                continue;
            }
            if (context == C_ARRAY_ELEMENT) {
                if ((character == ELEMENT_SEPERATOR.getSymbol() || character == ARRAY_END.getSymbol()) && scope <= 1) {
                    add((T) MojangsonFinder.readFromValue(tmpval));
                    tmpval = "";
                    continue;
                }
                tmpval += character;
            }
        }
    }

    private void parseException(int index, char symbol) throws MojangsonParseException {
        throw new MojangsonParseException("Index: " + index + ", symbol: \'" + symbol + "\'", MojangsonParseException.ParseExceptionReason.UNEXPECTED_SYMBOL);
    }
}
