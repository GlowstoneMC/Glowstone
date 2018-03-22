package net.glowstone.entity.objects;

import net.glowstone.entity.GlowEntityTest;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

@RunWith(Parameterized.class)
public class GlowMinecartTest extends GlowEntityTest<GlowMinecart> {

    @Parameterized.Parameters(name = "{0}")
    public static GlowMinecart.MinecartType[] data() {
        return GlowMinecart.MinecartType.values();
    }

    public GlowMinecartTest(GlowMinecart.MinecartType type) {
        super(type.getCreator());
    }
}
