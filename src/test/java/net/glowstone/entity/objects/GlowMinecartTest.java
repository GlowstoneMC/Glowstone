package net.glowstone.entity.objects;

import com.binarytweed.test.DelegateRunningTo;
import net.glowstone.entity.GlowEntityTest;
import org.junit.runners.Parameterized;

@DelegateRunningTo(Parameterized.class)
public class GlowMinecartTest extends GlowEntityTest<GlowMinecart> {

    @Parameterized.Parameters(name = "{0}")
    public static GlowMinecart.MinecartType[] data() {
        return GlowMinecart.MinecartType.values();
    }

    public GlowMinecartTest(GlowMinecart.MinecartType type) {
        super(type.getCreator());
    }
}
