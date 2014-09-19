package net.glowstone.util.nbt;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for {@link NBTOutputStream} and constructing {@link CompoundTag}s.
 */
public class NbtOutputTest {

    @Test
    public void helloWorld() throws IOException {
        CompoundTag compound = new CompoundTag();
        compound.putString("name", "Bananrama");
        Checks.checkHelloWorld(compound);
        Checks.checkHelloWorld(saveLoad(compound));
    }

    @Test
    public void bigTest() throws IOException {
        CompoundTag compound = new CompoundTag();

        // basic values
        compound.putByte("byteTest", 127);
        compound.putShort("shortTest", 32767);
        compound.putInt("intTest", 2147483647);
        compound.putLong("longTest", 9223372036854775807L);
        compound.putFloat("floatTest", 0.49823147058486938);
        compound.putDouble("doubleTest", 0.49312871321823148);
        compound.putString("stringTest", "HELLO WORLD THIS IS A TEST STRING \u00c5\u00c4\u00d6!");

        // byte array
        byte[] array = new byte[1000];
        for (int i = 0; i < 1000; ++i) {
            array[i] = (byte) ((i * i * 255 + i * 7) % 100);
        }
        compound.putByteArray(Checks.BYTE_ARRAY_NAME, array);

        // nested compound
        CompoundTag nested = new CompoundTag();
        CompoundTag egg = new CompoundTag();
        egg.putString("name", "Eggbert");
        egg.putFloat("value", 0.5);
        nested.putCompound("egg", egg);
        CompoundTag ham = new CompoundTag();
        ham.putString("name", "Hampus");
        ham.putFloat("value", 0.75);
        nested.putCompound("ham", ham);
        compound.putCompound("nested compound test", nested);

        // simple list
        List<Long> longList = new ArrayList<>(5);
        for (int i = 0; i < 5; ++i) {
            longList.add((long) (11 + i));
        }
        compound.putList("listTest (long)", TagType.LONG, longList);

        // compound list
        List<CompoundTag> compoundList = new ArrayList<>(2);
        for (int i = 0; i < 2; ++i) {
            CompoundTag child = new CompoundTag();
            child.putLong("created-on", 1264099775885L);
            child.putString("name", "Compound tag #" + i);
            compoundList.add(child);
        }
        compound.putCompoundList("listTest (compound)", compoundList);

        // check it out
        Checks.checkBigTest(compound);
        Checks.checkBigTest(saveLoad(compound));
    }

    private CompoundTag saveLoad(CompoundTag tag) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        try (NBTOutputStream out = new NBTOutputStream(bytesOut)) {
            out.writeTag(tag);
        }
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytesOut.toByteArray());
        try (NBTInputStream in = new NBTInputStream(bytesIn)) {
            return in.readCompound();
        }
    }

}
