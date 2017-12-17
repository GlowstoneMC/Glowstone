package net.glowstone.util.nbt;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import net.glowstone.util.IsFloatCloseTo;
import org.hamcrest.number.IsCloseTo;

/**
 * Checks for the included example NBT files.
 */
class Checks {

    public static final String BYTE_ARRAY_NAME = "byteArrayTest (the first 1000 values of (n*n*255+n*7)%100, starting with n=0 (0, 62, 34, 16, 8, ...))";

    private Checks() {
    }

    static void checkHelloWorld(CompoundTag compound) {
        assertThat("incorrect size", 1, is(compound.getValue().size()));
        assertThat("name is not string", compound.isString("name"), is(true));
        assertThat("name is incorrect", "Bananrama", is(compound.getString("name")));
    }

    static void checkBigTest(CompoundTag compound) {
        assertThat("incorrect size", compound.getValue().size(), is(11));

        // basic values
        assertThat("byteTest", compound.getByte("byteTest"), is((byte) 127));
        assertThat("shortTest", compound.getShort("shortTest"), is((short) 32767));
        assertThat("intTest", compound.getInt("intTest"), is(2147483647));
        assertThat("longTest", compound.getLong("longTest"), is(9223372036854775807L));
        assertThat("floatTest", compound.getFloat("floatTest"),
            IsFloatCloseTo.closeTo(0.49823147058486938f, 1e-16f));
        assertThat("doubleTest", compound.getDouble("doubleTest"),
            IsCloseTo.closeTo(0.49312871321823148, 1e-16));
        assertThat("stringTest", compound.getString("stringTest"),
            is("HELLO WORLD THIS IS A TEST STRING \u00c5\u00c4\u00d6!"));

        // byte array
        byte[] array = compound.getByteArray(BYTE_ARRAY_NAME);
        assertThat("byteArray size", array.length, is(1000));
        for (int i = 0; i < 1000; ++i) {
            assertThat("byteArrayTest[" + i + "]", array[i],
                is((byte) ((i * i * 255 + i * 7) % 100)));
        }

        // nested compound
        CompoundTag nested = compound.getCompound("nested compound test");
        CompoundTag egg = nested.getCompound("egg");
        assertThat("nested.egg.name", egg.getString("name"), is("Eggbert"));
        assertThat("nested.egg.value", egg.getFloat("value"), IsFloatCloseTo.closeTo(0.5f, 1e-10f));
        CompoundTag ham = nested.getCompound("ham");
        assertThat("nested.ham.name", ham.getString("name"), is("Hampus"));
        assertThat("nested.ham.value", ham.getFloat("value"),
            IsFloatCloseTo.closeTo(0.75f, 1e-10f));

        // simple list
        List<Long> longList = compound.getList("listTest (long)", TagType.LONG);
        assertThat("longList size", longList.size(), is(5));
        for (int i = 0; i < 5; ++i) {
            assertThat("longList[" + i + "]", longList.get(i), is((long) (11 + i)));
        }

        // compound list
        List<CompoundTag> compoundList = compound.getCompoundList("listTest (compound)");
        assertThat("compoundList size", compoundList.size(), is(2));
        for (int i = 0; i < 2; ++i) {
            CompoundTag child = compoundList.get(i);
            assertThat("compoundList[" + i + "].created-on", child.getLong("created-on"),
                is(1264099775885L));
            assertThat("compoundList[" + i + "].name", child.getString("name"),
                is("Compound tag #" + i));
        }
    }

}
