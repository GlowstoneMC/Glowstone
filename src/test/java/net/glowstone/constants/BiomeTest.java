package net.glowstone.constants;

import org.bukkit.block.Biome;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests for the GlowBiome class.
 */
public class BiomeTest {

    @Test
    public void testAllBiomes() {
        for (Biome biome : Biome.values()) {
            int id = GlowBiome.getId(biome);
            assertFalse("No id specified for biome " + biome, id == -1);
            assertEquals("Mapping for id " + id + " mismatch", biome, GlowBiome.getBiome(id));
        }
    }

}
