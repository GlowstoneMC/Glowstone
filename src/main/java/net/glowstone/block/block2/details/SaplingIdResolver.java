package net.glowstone.block.block2.details;

import net.glowstone.block.block2.IdResolver;
import net.glowstone.block.block2.sponge.BlockState;

public class SaplingIdResolver implements IdResolver {
    @Override
    public int getId(BlockState state, int suggested) {
        if (suggested >= TreeVariant.values().length) {
            suggested += 8 - TreeVariant.values().length;
        }
        return suggested;
    }
}
