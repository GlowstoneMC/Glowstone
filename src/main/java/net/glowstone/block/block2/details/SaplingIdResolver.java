package net.glowstone.block.block2.details;

import net.glowstone.block.block2.IdResolver;
import net.glowstone.block.block2.sponge.BlockState;
import org.bukkit.TreeSpecies;

public class SaplingIdResolver implements IdResolver {
    @Override
    public int getId(BlockState state, int suggested) {
        if (suggested >= TreeSpecies.values().length) {
            suggested += 8 - TreeSpecies.values().length;
        }
        return suggested;
    }
}
