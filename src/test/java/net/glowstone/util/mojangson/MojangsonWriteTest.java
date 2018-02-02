package net.glowstone.util.mojangson;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import net.glowstone.util.nbt.ByteTag;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.DoubleTag;
import net.glowstone.util.nbt.FloatTag;
import net.glowstone.util.nbt.IntTag;
import net.glowstone.util.nbt.ListTag;
import net.glowstone.util.nbt.LongTag;
import net.glowstone.util.nbt.ShortTag;
import net.glowstone.util.nbt.StringTag;
import net.glowstone.util.nbt.Tag;
import net.glowstone.util.nbt.TagType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class MojangsonWriteTest {

    private final AbstractMap.Entry<Tag, String> testCase; // The tag to write, the expected output.

    public MojangsonWriteTest(Tag tag, String expected) {
        this.testCase = new AbstractMap.SimpleImmutableEntry<>(tag, expected);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getCases() {
        return Arrays.asList(
            new Object[]{new ByteTag((byte) 1), "{value:1b}"},
            new Object[]{new DoubleTag((double) 1), "{value:1.0d}"},
            new Object[]{new FloatTag((float) 1), "{value:1.0f}"},
            new Object[]{new IntTag(1), "{value:1}"},
            new Object[]{new ListTag<>(TagType.STRING,
                Arrays.asList(new StringTag("1"), new StringTag("2"))), "{value:[\"1\",\"2\"]}"},
            new Object[]{new LongTag((long) 1), "{value:1l}"},
            new Object[]{new ShortTag((short) 1), "{value:1s}"},
            new Object[]{new StringTag("1"), "{value:\"1\"}"}
        );
    }

    @Test
    public void canWriteTag() {
        CompoundTag top = new CompoundTag();
        top.getValue().put("value", testCase.getKey());
        String result = Mojangson.fromTag(top);
        assertThat(
            "Could not write case for " + testCase.getKey().getType().getName() + ": Wrong output.",
            result, is(testCase.getValue()));
    }

}
