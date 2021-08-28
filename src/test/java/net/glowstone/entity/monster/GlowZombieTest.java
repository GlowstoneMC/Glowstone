package net.glowstone.entity.monster;

import org.bukkit.Location;

import java.util.function.Function;

public class GlowZombieTest extends GlowMonsterTest<GlowZombie> {
    protected GlowZombieTest(
            Function<Location, ? extends GlowZombie> entityCreator) {
        super(entityCreator);
    }
    public GlowZombieTest() {
        this(GlowZombie::new);
    }
}
