package net.glowstone.block.itemtype;

import org.bukkit.Material;
import org.bukkit.entity.Egg;

public class ItemEggTest extends ItemProjectileTest<Egg> {
    public ItemEggTest() {
        super(new ItemEgg(), Material.EGG, Egg.class);
    }
}
