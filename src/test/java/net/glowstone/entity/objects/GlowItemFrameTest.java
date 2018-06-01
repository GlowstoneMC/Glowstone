package net.glowstone.entity.objects;

import net.glowstone.entity.GlowHangingEntityTest;
import org.bukkit.block.BlockFace;

public class GlowItemFrameTest extends GlowHangingEntityTest<GlowItemFrame> {
    public GlowItemFrameTest() {
        super(location -> new GlowItemFrame(null, location, BlockFace.SOUTH));
    }
}
