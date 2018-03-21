package net.glowstone.entity.objects;

import net.glowstone.entity.GlowHangingEntityTest;
import org.bukkit.block.BlockFace;

public class GlowLeashHitchTest extends GlowHangingEntityTest<GlowLeashHitch> {
    public GlowLeashHitchTest() {
        super(location -> new GlowLeashHitch(location, BlockFace.SOUTH));
    }
}
