package net.glowstone.entity.monster;

import java.util.function.Function;
import org.bukkit.Location;

public class GlowSlimeTest extends GlowMonsterTest<GlowSlime> {

    protected GlowSlimeTest(
            Function<Location, ? extends GlowSlime> entityCreator) {
        super(entityCreator);
    }

    public GlowSlimeTest() {
        this(GlowSlime::new);
    }
}
