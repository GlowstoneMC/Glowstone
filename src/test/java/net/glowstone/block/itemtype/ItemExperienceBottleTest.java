package net.glowstone.block.itemtype;

import org.bukkit.Material;
import org.bukkit.entity.ThrownExpBottle;

public class ItemExperienceBottleTest extends ItemProjectileTest<ThrownExpBottle> {
    public ItemExperienceBottleTest() {
        super(new ItemExperienceBottle(), Material.EXPERIENCE_BOTTLE, ThrownExpBottle.class);
    }
}
