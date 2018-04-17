package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GlowChickenTest extends GlowAnimalTest<GlowChicken> {

    public GlowChickenTest() {
        super(GlowChicken::new);
    }

    @Test
    @Override
    public void testGetBreedingFood() {
        assertEquals(EnumSet.of(Material.SEEDS, Material.PUMPKIN_SEEDS, Material.MELON_SEEDS, Material.BEETROOT_SEEDS),
                entity.getBreedingFood());
    }
}
