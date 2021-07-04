package net.glowstone.util.mojangson;

import net.glowstone.util.mojangson.ex.MojangsonParseException;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.Tag;
import net.glowstone.util.nbt.TagType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MojangsonParseTest {

    public static Collection<Arguments> getCases() {
        return Arrays.asList(
            Arguments.of(TagType.BYTE, "{value:1b}"),
            Arguments.of(TagType.DOUBLE, "{value:1.0}"),
            Arguments.of(TagType.FLOAT, "{value:1.0f}"),
            Arguments.of(TagType.INT, "{value:1}"),
            Arguments.of(TagType.LIST, "{value:[\"1\",\"2\"]}"),
            Arguments.of(TagType.LONG, "{value:1l}"),
            Arguments.of(TagType.SHORT, "{value:1s}"),
            Arguments.of(TagType.STRING, "{value:\"1\"}")
        );
    }

    @MethodSource("getCases")
    @ParameterizedTest
    public void canParseType(TagType key, String json) {
        try {
            CompoundTag compound = Mojangson.parseCompound(json);
            Tag value = compound.getValue().get("value");

            // Checks if the TagType of the case and the parsed type are equal.
            if (value.getType() != key) {
                fail("Incorrect type parsing for case " + key.getName() + " (Got "
                    + value.getType().getName() + ") for Mojansgon: " + json);
            }
        } catch (MojangsonParseException e) {
            // Catches a parse failure.
            fail("Could not parse case for " + key.getName() + "( " + json + "): "
                    + e.getMessage());
        }
    }

    public static Collection<Arguments> getBooleanCases() {
        return Arrays.asList(
                Arguments.of("{value:true}", true),
                Arguments.of("{value:false}", false),
                Arguments.of("{value:1b}", true),
                Arguments.of("{value:0b}", false),
                Arguments.of("{value:1s}", true),
                Arguments.of("{value:0s}", false),
                Arguments.of("{value:1l}", true),
                Arguments.of("{value:0l}", false),
                Arguments.of("{value:1.0f}", true),
                Arguments.of("{value:0.0f}", false),
                Arguments.of("{value:1.0d}", true),
                Arguments.of("{value:0.0d}", false)
        );
    }

    @MethodSource("getBooleanCases")
    @ParameterizedTest
    public void canParseBoolean(String json, boolean expected) throws MojangsonParseException {
        CompoundTag compound = Mojangson.parseCompound(json);
        boolean bool = compound.getBoolean("value");

        assertEquals(expected, bool);
    }
}
