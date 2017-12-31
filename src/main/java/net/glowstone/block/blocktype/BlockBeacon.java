package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.entity.BeaconEntity;
import net.glowstone.block.entity.BlockEntity;
import org.bukkit.Material;

public class BlockBeacon extends BlockDirectDrops {

    public BlockBeacon() {
        super(Material.BEACON);
    }

    @Override
    public BlockEntity createBlockEntity(GlowBlock block) {
        return new BeaconEntity(block);
    }
}
