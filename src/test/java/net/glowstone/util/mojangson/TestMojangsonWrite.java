package net.glowstone.util.mojangson;

import net.glowstone.util.nbt.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class TestMojangsonWrite {

    private final Pair<Tag, String> testCase; // The tag to write, the expected output.

    public TestMojangsonWrite(Tag tag, String expected) {
        this.testCase = new ImmutablePair<>(tag, expected);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCases() {
        return Arrays.asList(
                new Object[]{new ByteTag((byte) 1), "{value:1b}"},
                new Object[]{new DoubleTag((double) 1), "{value:1.0d}"},
                new Object[]{new FloatTag((float) 1), "{value:1.0f}"},
                new Object[]{new IntTag(1), "{value:1}"},
                new Object[]{new ListTag<>(TagType.STRING, Arrays.asList(new StringTag("1"), new StringTag("2"))), "{value:[\"1\",\"2\"]}"},
                new Object[]{new LongTag((long) 1), "{value:1l}"},
                new Object[]{new ShortTag((short) 1), "{value:1s}"},
                new Object[]{new StringTag("1"), "{value:\"1\"}"}
        );
    }

    @Test
    public void canWriteTag() {
        CompoundTag top = new CompoundTag();
        top.getValue().put("value", testCase.getKey());
        String result = Mojangson.fromCompoundTag(top);
        Assert.assertEquals("Could not write case for " + testCase.getKey().getType().getName() + ": Wrong output.", testCase.getValue(), result);
    }

}
