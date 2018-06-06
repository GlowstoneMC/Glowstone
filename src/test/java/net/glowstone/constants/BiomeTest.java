package net.glowstone.constants;

import static org.hamcrest.CoreMatchers.is;
import static org.testng.AssertJUnit.assertThat;

import java.util.Iterator;
import net.glowstone.TestUtils;
import org.bukkit.block.Biome;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests for {@link GlowBiome}.
 */
public class BiomeTest {

    @DataProvider(name = "biome")
    public static Iterator<Object[]> biome() {
        return TestUtils.enumAsDataProvider(Biome.class);
    }

    @Test(dataProvider = "biome")
    public void testIdMapping(Biome biome) {
        int id = GlowBiome.getId(biome);
        assertThat("No id specified for biome " + biome, id == -1, is(false));
        assertThat("Mapping for id " + id + " mismatch", GlowBiome.getBiome(id), is(biome));
    }

}
