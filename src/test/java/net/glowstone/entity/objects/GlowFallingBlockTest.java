package net.glowstone.entity.objects;

import net.glowstone.block.data.SimpleBlockData;
import net.glowstone.entity.GlowEntityTest;
import org.bukkit.Material;

public class GlowFallingBlockTest extends GlowEntityTest<GlowFallingBlock> {
    public GlowFallingBlockTest() {
        // TODO: block entity
        super(location -> new GlowFallingBlock(location, new SimpleBlockData(Material.GRAVEL), null));
    }
}
