package net.glowstone.constants;

import org.bukkit.block.Biome;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link GlowBiome}.
 */
public class BiomeTest {

    @EnumSource(Biome.class)
    @ParameterizedTest
    public void testIdMapping(Biome biome) {
        int id = GlowBiome.getId(biome);
        assertThat("No id specified for biome " + biome, id == -1, is(false));
        assertThat("Mapping for id " + id + " mismatch", GlowBiome.getBiome(id), is(biome));
    }

}
