package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Material;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;

public class GlowRabbitTest extends GlowAnimalTest<GlowRabbit> {
    public GlowRabbitTest() {
        super(GlowRabbit::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.DANDELION, Material.GOLDEN_CARROT, Material.CARROT),
                entity.getBreedingFoods());
    }
}
