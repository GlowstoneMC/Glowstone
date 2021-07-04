package net.glowstone.util.nbt;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;

public class CompoundTagTest {

    @Test
    public void testTwoEmptyCompoundTagsMatch() throws Exception {
        CompoundTag superCompoundTag = new CompoundTag();
        CompoundTag subCompoundTab = new CompoundTag();

        assertTrue(subCompoundTab.matches(superCompoundTag));
    }

    @Test
    public void testEmptyCompoundTagMatchesNonEmpty() throws Exception {
        CompoundTag superCompoundTag = new CompoundTag();
        superCompoundTag.putBool("foo", true);

        CompoundTag subCompoundTag = new CompoundTag();

        assertTrue(subCompoundTag.matches(superCompoundTag));
    }

    @Test
    public void testSingleBooleanCompoundTagMatches() throws Exception {
        CompoundTag superCompoundTag = new CompoundTag();
        superCompoundTag.putBool("foo", true);

        CompoundTag subCompoundTag = new CompoundTag();
        subCompoundTag.putBool("foo", true);

        assertTrue(subCompoundTag.matches(superCompoundTag));
    }

    @Test
    public void testSingleIntegerCompoundTagMatches() throws Exception {
        CompoundTag superCompoundTag = new CompoundTag();
        superCompoundTag.putInt("foo", 123);

        CompoundTag subCompoundTag = new CompoundTag();
        subCompoundTag.putInt("foo", 123);

        assertTrue(subCompoundTag.matches(superCompoundTag));
    }

    @Test
    public void testSingleStringCompoundTagMatches() throws Exception {
        CompoundTag superCompoundTag = new CompoundTag();
        superCompoundTag.putString("foo", "bar");

        CompoundTag subCompoundTag = new CompoundTag();
        subCompoundTag.putString("foo", "bar");

        assertTrue(subCompoundTag.matches(superCompoundTag));
    }

    @Test
    public void testSingleByteCompoundTagMatches() throws Exception {
        CompoundTag superCompoundTag = new CompoundTag();
        superCompoundTag.putByte("foo", 7);

        CompoundTag subCompoundTag = new CompoundTag();
        subCompoundTag.putByte("foo", 7);

        assertTrue(subCompoundTag.matches(superCompoundTag));
    }

    @Test
    public void testSingleLongCompoundTagMatches() throws Exception {
        CompoundTag superCompoundTag = new CompoundTag();
        superCompoundTag.putLong("foo", 1234567890);

        CompoundTag subCompoundTag = new CompoundTag();
        subCompoundTag.putLong("foo", 1234567890);

        assertTrue(subCompoundTag.matches(superCompoundTag));
    }

    @Test
    public void testSingleShortCompoundTagMatches() throws Exception {
        CompoundTag superCompoundTag = new CompoundTag();
        superCompoundTag.putShort("foo", 12);

        CompoundTag subCompoundTag = new CompoundTag();
        subCompoundTag.putShort("foo", 12);

        assertTrue(subCompoundTag.matches(superCompoundTag));
    }

    @Test
    public void testSingleDoubleCompoundTagMatches() throws Exception {
        CompoundTag superCompoundTag = new CompoundTag();
        superCompoundTag.putDouble("foo", 0.1);

        CompoundTag subCompoundTag = new CompoundTag();
        subCompoundTag.putDouble("foo", 0.1);

        assertTrue(subCompoundTag.matches(superCompoundTag));
    }

    @Test
    public void testSingleFloatCompoundTagMatches() throws Exception {
        CompoundTag superCompoundTag = new CompoundTag();
        superCompoundTag.putFloat("foo", 0.12);

        CompoundTag subCompoundTag = new CompoundTag();
        subCompoundTag.putFloat("foo", 0.12);

        assertTrue(subCompoundTag.matches(superCompoundTag));
    }

    @Test
    public void testSingleByteArrayCompoundTagMatches() throws Exception {
        CompoundTag superCompoundTag = new CompoundTag();
        superCompoundTag.putByteArray("foo", (byte) 12, (byte) 34);

        CompoundTag subCompoundTag = new CompoundTag();
        subCompoundTag.putByteArray("foo", (byte) 12, (byte) 34);

        assertTrue(subCompoundTag.matches(superCompoundTag));
    }

    @Test
    public void testSingleIntArrayCompoundTagMatches() throws Exception {
        CompoundTag superCompoundTag = new CompoundTag();
        superCompoundTag.putIntArray("foo", 123, 456);

        CompoundTag subCompoundTag = new CompoundTag();
        subCompoundTag.putIntArray("foo", 123, 456);

        assertTrue(subCompoundTag.matches(superCompoundTag));
    }

    @Test
    public void testSingleBooleanEmbeddedCompoundTagMatches() throws Exception {
        CompoundTag superEmbeddedCompoundTag = new CompoundTag();
        superEmbeddedCompoundTag.putBool("foo", true);
        CompoundTag superCompoundTag = new CompoundTag();
        superCompoundTag.putCompound("foo", superEmbeddedCompoundTag);

        CompoundTag subEmbeddedCompoundTag = new CompoundTag();
        subEmbeddedCompoundTag.putBool("foo", true);
        CompoundTag subCompoundTag = new CompoundTag();
        subCompoundTag.putCompound("foo", subEmbeddedCompoundTag);

        assertTrue(subCompoundTag.matches(superCompoundTag));
    }
}
