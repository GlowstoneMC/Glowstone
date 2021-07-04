package net.glowstone.entity.projectile;

import org.bukkit.Location;

import java.util.function.Function;

public class GlowSplashPotionTest extends GlowProjectileTest<GlowSplashPotion> {
    protected GlowSplashPotionTest(
            Function<? super Location, ? extends GlowSplashPotion> entityCreator) {
        super(entityCreator);
    }

    public GlowSplashPotionTest() {
        this(GlowSplashPotion::new);
    }
}
