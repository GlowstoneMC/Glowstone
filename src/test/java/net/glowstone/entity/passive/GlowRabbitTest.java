package net.glowstone.entity.passive;

import java.util.EnumSet;
import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Material;
import org.testng.Assert;
import org.testng.annotations.Test;

public class GlowRabbitTest extends GlowAnimalTest<GlowRabbit> {
    public GlowRabbitTest() {
        super(GlowRabbit::new);
    }

    @Test
    @Override
    public void testGetBreedingFoods() {
        Assert.assertEquals(entity.getBreedingFoods(), EnumSet.of(Material.YELLOW_FLOWER, Material.GOLDEN_CARROT, Material.CARROT_ITEM));
    }
}
