package net.glowstone.block.data;

import org.bukkit.Material;

// Temporary
public interface BlockData {

    Material getMaterial();

    default Class<? extends BlockData> getBaseClass() {
        return BlockData.class;
    }
}
