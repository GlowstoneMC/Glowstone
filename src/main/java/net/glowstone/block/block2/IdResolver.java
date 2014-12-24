package net.glowstone.block.block2;

import net.glowstone.block.block2.sponge.BlockState;

/**
 * Function used to provide deprecated block metadata values for BlockStates.
 */
public interface IdResolver {

    /**
     * Get the index for a specific blockstate.
     * @param state The state to determine the index for
     * @param suggested The suggested index based on property ordering
     * @return The index, or -1 for none
     */
    int getId(BlockState state, int suggested);

}
