package net.glowstone.block.itemtype;

import org.bukkit.Material;
import org.bukkit.entity.Snowball;

public class ItemSnowballTest extends ItemProjectileTest<Snowball> {
    public ItemSnowballTest() {
        super(new ItemSnowball(), Material.SNOW_BALL, Snowball.class);
    }
}
