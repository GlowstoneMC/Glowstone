package net.glowstone.constants;

import net.glowstone.testutils.ParameterUtils;
import org.bukkit.block.Biome;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Tests for {@link GlowBiome}.
 */
@RunWith(Parameterized.class)
public class BiomeTest {

    private final Biome biome;

    public BiomeTest(Biome biome) {
        this.biome = biome;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ParameterUtils.enumCases(Biome.values());
    }

    @Test
    public void testIdMapping() {
        int id = GlowBiome.getId(biome);
        assertFalse("No id specified for biome " + biome, id == -1);
        assertEquals("Mapping for id " + id + " mismatch", biome, GlowBiome.getBiome(id));
    }

}
