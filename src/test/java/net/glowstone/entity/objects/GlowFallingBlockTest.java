package net.glowstone.entity.objects;

import net.glowstone.block.data.BlockDataManager;
import net.glowstone.entity.GlowEntityTest;
import org.bukkit.Material;

public class GlowFallingBlockTest extends GlowEntityTest<GlowFallingBlock> {
    public GlowFallingBlockTest() {
        super(location -> new GlowFallingBlock(location, new BlockDataManager().createBlockData(Material.GRAVEL), null));
    }
}
