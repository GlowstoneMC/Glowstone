package net.glowstone.entity.passive;

import static org.junit.Assert.assertEquals;

import java.util.EnumSet;
import org.bukkit.Material;
import org.junit.Test;

public class GlowLlamaTest extends GlowChestedHorseTest<GlowLlama> {
    public GlowLlamaTest() {
        super(GlowLlama::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.HAY_BLOCK), entity.getBreedingFoods());
    }
}
