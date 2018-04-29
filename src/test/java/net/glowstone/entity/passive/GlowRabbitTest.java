package net.glowstone.entity.passive;

import static org.junit.Assert.assertEquals;

import java.util.EnumSet;
import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Material;
import org.junit.Test;

public class GlowRabbitTest extends GlowAnimalTest<GlowRabbit> {
    public GlowRabbitTest() {
        super(GlowRabbit::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        assertEquals(EnumSet.of(Material.YELLOW_FLOWER, Material.GOLDEN_CARROT, Material.CARROT_ITEM),
                entity.getBreedingFoods());
    }
}
