package net.glowstone.entity.passive;

import static org.junit.Assert.assertEquals;

import java.util.EnumSet;
import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Material;
import org.junit.Test;

public class GlowPigTest extends GlowAnimalTest<GlowPig> {
    public GlowPigTest() {
        super(GlowPig::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.POTATO, Material.CARROT, Material.BEETROOT),
                entity.getBreedingFoods());
    }
}
