package net.glowstone.block;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import org.junit.jupiter.api.Test;

class MaterialUtilTest {
    @Test
    public void testSanityChecks() {
        assertContainsAllAndNotOnly(MaterialUtil.SLABS, MaterialUtil.WOODEN_SLABS);
        assertContainsAllAndNotOnly(MaterialUtil.STAIRS, MaterialUtil.WOODEN_STAIRS);
        assertContainsAllAndNotOnly(MaterialUtil.BUTTONS, MaterialUtil.WOODEN_BUTTONS);
    }

    private static void assertContainsAllAndNotOnly(Collection<?> superset, Collection<?> subset) {
        assertTrue(superset.containsAll(subset));
        assertFalse(subset.containsAll(superset));
    }
}