package net.glowstone.util.nbt;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Checks for the included example NBT files.
 */
class Checks {

    private Checks() {}

    public static final String BYTE_ARRAY_NAME = "byteArrayTest (the first 1000 values of (n*n*255+n*7)%100, starting with n=0 (0, 62, 34, 16, 8, ...))";

    static void checkHelloWorld(CompoundTag compound) {
        assertEquals("incorrect size", compound.getValue().size(), 1);
        assertTrue("name is not string", compound.isString("name"));
        assertEquals("name is incorrect", compound.getString("name"), "Bananrama");
    }

    static void checkBigTest(CompoundTag compound) {
        assertEquals("incorrect size", 11, compound.getValue().size());

        // basic values
        assertEquals("byteTest", 127, compound.getByte("byteTest"));
        assertEquals("shortTest", 32767, compound.getShort("shortTest"));
        assertEquals("intTest", 2147483647, compound.getInt("intTest"));
        assertEquals("longTest", 9223372036854775807L, compound.getLong("longTest"));
        assertEquals("floatTest", 0.49823147058486938, compound.getFloat("floatTest"), 1e-16);
        assertEquals("doubleTest", 0.49312871321823148, compound.getDouble("doubleTest"), 1e-16);
        assertEquals("stringTest", "HELLO WORLD THIS IS A TEST STRING \u00c5\u00c4\u00d6!", compound.getString("stringTest"));

        // byte array
        byte[] array = compound.getByteArray(BYTE_ARRAY_NAME);
        assertEquals("byteArray size", 1000, array.length);
        for (int i = 0; i < 1000; ++i) {
            assertEquals("byteArrayTest[" + i + "]", (i * i * 255 + i * 7) % 100, array[i]);
        }

        // nested compound
        CompoundTag nested = compound.getCompound("nested compound test");
        CompoundTag egg = nested.getCompound("egg");
        assertEquals("nested.egg.name", "Eggbert", egg.getString("name"));
        assertEquals("nested.egg.value", 0.5, egg.getFloat("value"), 1e-10);
        CompoundTag ham = nested.getCompound("ham");
        assertEquals("nested.ham.name", "Hampus", ham.getString("name"));
        assertEquals("nested.ham.value", 0.75, ham.getFloat("value"), 1e-10);

        // simple list
        List<Long> longList = compound.getList("listTest (long)", TagType.LONG);
        assertEquals("longList size", 5, longList.size());
        for (int i = 0; i < 5; ++i) {
            assertEquals("longList[" + i + "]", 11 + i, (long) longList.get(i));
        }

        // compound list
        List<CompoundTag> compoundList = compound.getCompoundList("listTest (compound)");
        assertEquals("compoundList size", 2, compoundList.size());
        for (int i = 0; i < 2; ++i) {
            CompoundTag child = compoundList.get(i);
            assertEquals("compoundList[" + i + "].created-on", 1264099775885L, child.getLong("created-on"));
            assertEquals("compoundList[" + i + "].name", "Compound tag #" + i, child.getString("name"));
        }
    }

}
