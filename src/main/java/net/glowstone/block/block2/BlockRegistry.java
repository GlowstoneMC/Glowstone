package net.glowstone.block.block2;

import net.glowstone.block.block2.sponge.BlockState;
import net.glowstone.block.block2.sponge.BlockType;
import net.glowstone.block.block2.details.DefaultBlockBehavior;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * The registry and lookup table for block types.
 */
public final class BlockRegistry {

    public static final BlockRegistry instance = new BlockRegistry();

    static {
        Blocks.init();
    }

    private final Map<String, BlockType> types = new HashMap<>();
    private final Map<Integer, BlockType> oldIds = new HashMap<>();

    private BlockRegistry() {
    }

    public void register(BlockType type) {
        if (types.containsKey(type.getId())) {
            throw new IllegalArgumentException("Cannot register duplicate '" + type.getId() + "'");
        }
        types.put(type.getId(), type);
    }

    void registerOldId(int oldId, GlowBlockType type) {
        oldIds.put(oldId, type);
    }

    public BlockType getBlock(String id) {
        return types.get(id);
    }

    @Deprecated
    public BlockType getByTypeId(int id) {
        return oldIds.get(id);
    }

    public BlockState getByFullId(int id) {
        BlockType type = getByTypeId(id >> 4);
        if (type != null) {
            return type.getStateFromDataValue((byte)(id & 0xf));
        }
        return null;
    }

    public BlockBehavior getBehavior(BlockType type) {
        if (type instanceof GlowBlockType) {
            return ((GlowBlockType) type).getBehavior();
        } else {
            return DefaultBlockBehavior.instance;
        }
    }

    public BlockBehavior getBehavior(String id) {
        return getBehavior(getBlock(id));
    }

    public BlockBehavior getBehavior(Material material) {
        return getBehavior(getByTypeId(material.getId()));
    }
}
