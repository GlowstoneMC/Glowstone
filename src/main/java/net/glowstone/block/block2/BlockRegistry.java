package net.glowstone.block.block2;

import net.glowstone.block.block2.sponge.BlockType;

import java.util.HashMap;
import java.util.Map;

/**
 * The registry and lookup table for block types.
 */
public class BlockRegistry {

    public static final BlockRegistry instance = new BlockRegistry();

    static {
        Blocks.init();
    }

    private final Map<String, BlockType> types = new HashMap<>();
    private final Map<Integer, BlockType> oldIds = new HashMap<>();

    private BlockRegistry() {
    }

    public void register(BlockType type) {
        types.put(type.getId(), type);
        if (type instanceof GlowBlockType) {
            GlowBlockType glowType = (GlowBlockType) type;
            if (glowType.getOldId() != -1) {
                oldIds.put(glowType.getOldId(), type);
            }
        }
    }

    public BlockType getBlock(String id) {
        return types.get(id);
    }

    public BlockType getByOldId(int id) {
        return oldIds.get(id);
    }
}
