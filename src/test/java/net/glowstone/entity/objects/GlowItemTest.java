package net.glowstone.entity.objects;

import net.glowstone.entity.GlowEntityTest;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GlowItemTest extends GlowEntityTest<GlowItem> {
    public GlowItemTest() {
        super(location -> new GlowItem(location, new ItemStack(Material.DIRT)));
    }
}
