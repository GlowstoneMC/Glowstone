package net.glowstone.entity.objects;

import net.glowstone.entity.GlowEntityTest;
import org.bukkit.Material;

public class GlowFallingBlockTest extends GlowEntityTest<GlowFallingBlock> {
    public GlowFallingBlockTest() {
        super(location -> new GlowFallingBlock(location, Material.GRAVEL, (byte) 0));
    }
}
