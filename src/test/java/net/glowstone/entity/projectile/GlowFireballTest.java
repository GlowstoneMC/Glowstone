package net.glowstone.entity.projectile;

import java.util.function.Function;
import org.bukkit.Location;

public class GlowFireballTest extends GlowProjectileTest<GlowFireball> {
    protected GlowFireballTest(
            Function<? super Location, ? extends GlowFireball> entityCreator) {
        super(entityCreator);
    }

    public GlowFireballTest() {
        this(GlowFireball::new);
    }
}
