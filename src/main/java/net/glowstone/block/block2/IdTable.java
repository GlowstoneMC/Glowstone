package net.glowstone.block.block2;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.glowstone.block.block2.sponge.BlockState;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Table for BlockType and BlockState id values, for legacy and storage.
 */
public class IdTable {

    private final BiMap<Integer, GlowBlockType> base = HashBiMap.create();
    private final Map<GlowBlockType, BlockState[]> child = new IdentityHashMap<>();

    public GlowBlockType getBaseType(int id) {
        return base.get(id);
    }

    public BlockState getChildType(GlowBlockType base, int id) {
        BlockState[] array = child.get(base);
        if (array == null || id < 0 || id >= array.length) {
            return null;
        } else {
            return array[id];
        }
    }

    public BlockState getFullType(int id) {
        return getChildType(getBaseType(id >> 4), id & 0xf);
    }

    public int getBaseId(GlowBlockType type) {
        return base.inverse().get(type);
    }

    public int getChildId(GlowBlockState state) {
        BlockState[] array = child.get(state.getType());
        if (array != null) {
            for (int i = 0; i < array.length; ++i) {
                if (array[i] == state) {
                    return i;
                }
            }
        }
        return 0;
    }

    public int getFullId(GlowBlockState state) {
        return (getBaseId(state.getType()) << 4) | (getChildId(state) & 0xf);
    }

    protected final void bind(int id, GlowBlockType type, IdResolver resolver) {
        base.put(id, type);
        BlockState[] array = new BlockState[16];
        List<GlowBlockState> states = type.getAllStates();
        for (int i = 0; i < states.size(); i++) {
            GlowBlockState state = states.get(i);
            int childId = resolver.getId(state, i);
            if (childId >= 0 && childId < 16) {
                array[childId] = state;
            }
        }
        child.put(type, array);
    }

    protected final void bind(int id, GlowBlockType type) {
        base.put(id, type);
        List<GlowBlockState> states = type.getAllStates();
        BlockState[] array = new BlockState[Math.min(states.size(), 16)];
        for (int i = 0; i < states.size() && i < 16; i++) {
            array[i] = states.get(i);
        }
        child.put(type, array);
    }

    /**
     * Function used to provide old block metadata values for BlockStates.
     */
    public interface IdResolver {
        /**
         * Get the index for a specific BlockState.
         * @param state The state to determine the index for
         * @param suggested The suggested index based on property ordering
         * @return The index, or -1 for none
         */
        int getId(BlockState state, int suggested);
    }

}
