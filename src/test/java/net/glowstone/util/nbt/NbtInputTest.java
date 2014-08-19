package net.glowstone.util.nbt;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Tests for {@link NBTInputStream} and reading from {@link CompoundTag}s.
 */
public class NbtInputTest {

    @Test
    public void helloWorld() throws IOException {
        InputStream raw = getClass().getResourceAsStream("/nbt/hello_world.nbt");
        Assert.assertNotNull("Failed to get test resource /nbt/hello_world.nbt", raw);
        try (NBTInputStream in = new NBTInputStream(raw, false)) {
            Checks.checkHelloWorld(in.readCompound());
        }
    }

    @Test
    public void bigTest() throws IOException {
        InputStream raw = getClass().getResourceAsStream("/nbt/bigtest.nbt");
        Assert.assertNotNull("Failed to get test resource /nbt/bigtest.nbt", raw);
        try (NBTInputStream in = new NBTInputStream(raw)) {
            Checks.checkBigTest(in.readCompound());
        }
    }

}
