package net.glowstone.entity.passive;

import static org.junit.Assert.assertEquals;

import java.util.EnumSet;
import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Material;
import org.junit.Test;

public class GlowChickenTest extends GlowAnimalTest<GlowChicken> {

    public GlowChickenTest() {
        super(GlowChicken::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.SEEDS, Material.PUMPKIN_SEEDS, Material.MELON_SEEDS, Material.BEETROOT_SEEDS),
                entity.getBreedingFoods());
    }
}
