package net.glowstone.entity.projectile;

import java.util.function.Function;
import org.bukkit.Location;

public class GlowSplashPotionTest extends GlowProjectileTest<GlowSplashPotion> {
    protected GlowSplashPotionTest(
            Function<? super Location, ? extends GlowSplashPotion> entityCreator) {
        super(entityCreator);
    }

    public GlowSplashPotionTest() {
        this(GlowSplashPotion::new);
    }
}
