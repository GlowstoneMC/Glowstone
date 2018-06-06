package net.glowstone.util.mojangson;

import static org.testng.AssertJUnit.fail;

import net.glowstone.util.mojangson.ex.MojangsonParseException;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.Tag;
import net.glowstone.util.nbt.TagType;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class MojangsonParseTest {

    public static final Object[][] TAG_TYPES_AND_JSON = {
            {TagType.BYTE, "{value:1b}"},
            {TagType.DOUBLE, "{value:1.0}"},
            {TagType.FLOAT, "{value:1.0f}"},
            {TagType.INT, "{value:1}"},
            {TagType.LIST, "{value:[\"1\",\"2\"]}"},
            {TagType.LONG, "{value:1l}"},
            {TagType.SHORT, "{value:1s}"},
            {TagType.STRING, "{value:\"1\"}"}
    };

    @DataProvider(name = "TagTypes")
    public static Object[][] getCases() {
        return TAG_TYPES_AND_JSON;
    }

    @Test(dataProvider = "TagTypes")
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
}
