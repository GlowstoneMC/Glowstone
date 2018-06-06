package net.glowstone.util.mojangson;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class MojangsonWriteTest {

    @DataProvider(name = "TagTypes")
    public static Object[][] getCases() {
        return new Object[][]{
                {new ByteTag((byte) 1), "{value:1b}"},
                {new DoubleTag((double) 1), "{value:1.0d}"},
                {new FloatTag((float) 1), "{value:1.0f}"},
                {new IntTag(1), "{value:1}"},
                {new ListTag<>(TagType.STRING,
                        Arrays.asList(new StringTag("1"), new StringTag("2"))), "{value:[\"1\",\"2\"]}"},
                {new LongTag((long) 1), "{value:1l}"},
                {new ShortTag((short) 1), "{value:1s}"},
                {new StringTag("1"), "{value:\"1\"}"}
        };
    }

    @Test(dataProvider = "TagTypes")
    public void canWriteTag(Tag key, String value) {
        CompoundTag top = new CompoundTag();
        top.getValue().put("value", key);
        String result = Mojangson.fromTag(top);
        assertThat(
            "Could not write case for " + key.getType().getName() + ": Wrong output.",
            result, is(value));
    }

}
