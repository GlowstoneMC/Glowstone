package net.glowstone.block.block2;

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

    private final Map<String, GlowBlockType> types = new HashMap<>();

    private BlockRegistry() {
    }

    public void register(GlowBlockType type) {
        if (types.containsKey(type.getId())) {
            throw new IllegalArgumentException("Cannot register duplicate '" + type.getId() + "'");
        }
        types.put(type.getId(), type);
    }

    public GlowBlockType getBlock(String id) {
        return types.get(id);
    }

    public BlockBehavior getBehavior(Material material) {
        return DefaultIdTable.INSTANCE.getBaseType(material.getId()).getBehavior();
    }
}
