package net.glowstone.util.mojangson;

import net.glowstone.util.nbt.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MojangsonWriteTest {

    public static Collection<Arguments> getCases() {
        return Arrays.asList(
            Arguments.of(new ByteTag((byte) 1), "{value:1b}"),
            Arguments.of(new DoubleTag((double) 1), "{value:1.0d}"),
            Arguments.of(new FloatTag((float) 1), "{value:1.0f}"),
            Arguments.of(new IntTag(1), "{value:1}"),
            Arguments.of(new ListTag<>(TagType.STRING,
                Arrays.asList(new StringTag("1"), new StringTag("2"))), "{value:[\"1\",\"2\"]}"),
            Arguments.of(new LongTag((long) 1), "{value:1l}"),
            Arguments.of(new ShortTag((short) 1), "{value:1s}"),
            Arguments.of(new StringTag("1"), "{value:\"1\"}")
        );
    }

    @MethodSource("getCases")
    @ParameterizedTest
    public void canWriteTag(Tag key, String value) {
        CompoundTag top = new CompoundTag();
        top.getValue().put("value", key);
        String result = Mojangson.fromTag(top);
        assertThat(
            "Could not write case for " + key.getType().getName() + ": Wrong output.",
            result, is(value));
    }

}
