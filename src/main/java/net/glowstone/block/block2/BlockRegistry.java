package net.glowstone.block.block2;

import net.glowstone.block.block2.behavior.BlockBehavior;
import net.glowstone.block.block2.types.DefaultBlockBehavior;
import net.glowstone.block.block2.sponge.BlockType;
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

    @Deprecated
    public BlockType getByOldId(int id) {
        return oldIds.get(id);
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
        return getBehavior(getByOldId(material.getId()));
    }
}
