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
    public void canParseType(TagType key, String value) {
        try {
            CompoundTag compound = Mojangson.parseCompound(value);
            Tag value = compound.getValue().get("value");

            // Checks if the TagType of the case and the parsed type are equal.
            if (value.getType() != key) {
                fail("Incorrect type parsing for case " + key.getName() + " (Got "
                    + value.getType().getName() + ") for Mojansgon: " + value);
            }
        } catch (MojangsonParseException e) {
            // Catches a parse failure.
            fail("Could not parse case for " + key.getName() + "( " + value + "): "
                    + e.getMessage());
        }
    }
}
