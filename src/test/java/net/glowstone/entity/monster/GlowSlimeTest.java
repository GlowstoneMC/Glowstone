package net.glowstone.entity.monster;

import org.bukkit.Location;

import java.util.function.Function;

public class GlowSlimeTest extends GlowMonsterTest<GlowSlime> {

    protected GlowSlimeTest(
            Function<Location, ? extends GlowSlime> entityCreator) {
        super(entityCreator);
    }

    public GlowSlimeTest() {
        this(GlowSlime::new);
    }
}
