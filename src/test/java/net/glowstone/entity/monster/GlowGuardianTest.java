package net.glowstone.entity.monster;

import java.util.function.Function;
import org.bukkit.Location;

public class GlowGuardianTest extends GlowMonsterTest<GlowGuardian> {

    public GlowGuardianTest() {
        this(GlowGuardian::new);
    }

    protected GlowGuardianTest(
            Function<Location, ? extends GlowGuardian> entityCreator) {
        super(entityCreator);
    }
}
