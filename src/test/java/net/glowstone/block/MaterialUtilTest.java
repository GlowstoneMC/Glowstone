package net.glowstone.block;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MaterialUtilTest {
    @Test
    public void testSanityChecks() {
        assertTrue(MaterialUtil.SLABS.containsAll(MaterialUtil.WOODEN_SLABS));
        assertTrue(MaterialUtil.STAIRS.containsAll(MaterialUtil.WOODEN_STAIRS));
        assertTrue(MaterialUtil.BUTTONS.containsAll(MaterialUtil.WOODEN_BUTTONS));
    }
}