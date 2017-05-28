package net.glowstone.util.nbt;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link NBTInputStream} and reading from {@link CompoundTag}s.
 */
public class NbtInputTest {

    @Test
    public void helloWorld() throws IOException {
        InputStream raw = getClass().getResourceAsStream("/nbt/hello_world.nbt");
        assertThat("Failed to get test resource /nbt/hello_world.nbt", raw, notNullValue());
        try (NBTInputStream in = new NBTInputStream(raw, false)) {
            Checks.checkHelloWorld(in.readCompound());
        }
    }

    @Test
    public void bigTest() throws IOException {
        InputStream raw = getClass().getResourceAsStream("/nbt/bigtest.nbt");
        assertThat("Failed to get test resource /nbt/bigtest.nbt", raw, notNullValue());
        try (NBTInputStream in = new NBTInputStream(raw)) {
            Checks.checkBigTest(in.readCompound());
        }
    }

}
