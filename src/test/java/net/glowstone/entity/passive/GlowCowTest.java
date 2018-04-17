package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class GlowCowTest extends GlowAnimalTest<GlowCow> {

    public GlowCowTest() {
        super(GlowCow::new);
    }

    @Test
    @Override
    public void testGetBreedingFood() {
        assertEquals(EnumSet.of(Material.WHEAT), entity.getBreedingFood());
    }
}
