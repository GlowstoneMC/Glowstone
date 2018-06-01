package net.glowstone.entity.monster;

import java.util.function.Function;
import org.bukkit.Location;

public class GlowZombieTest extends GlowMonsterTest<GlowZombie> {
    protected GlowZombieTest(
            Function<Location, ? extends GlowZombie> entityCreator) {
        super(entityCreator);
    }
    public GlowZombieTest() {
        this(GlowZombie::new);
    }
}
