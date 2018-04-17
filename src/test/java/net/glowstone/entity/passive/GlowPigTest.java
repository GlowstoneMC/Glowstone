package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAnimalTest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class GlowPigTest extends GlowAnimalTest<GlowPig> {
    public GlowPigTest() {
        super(GlowPig::new);
    }

    @Test
    @Override
    public void testGetBreedingFood() {
        assertEquals(EnumSet.of(Material.POTATO_ITEM, Material.CARROT_ITEM, Material.BEETROOT),
                entity.getBreedingFood());
    }
}
