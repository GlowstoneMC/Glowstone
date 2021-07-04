package net.glowstone.entity.projectile;

import org.bukkit.Location;

import java.util.function.Function;

public class GlowFireballTest extends GlowProjectileTest<GlowFireball> {
    protected GlowFireballTest(
            Function<? super Location, ? extends GlowFireball> entityCreator) {
        super(entityCreator);
    }

    public GlowFireballTest() {
        this(GlowFireball::new);
    }
}
