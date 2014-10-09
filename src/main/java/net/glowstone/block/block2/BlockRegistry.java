package net.glowstone.block.block2;

import java.util.HashMap;
import java.util.Map;

/**
 * Todo: Javadoc for BlockRegistry.
 */
public class BlockRegistry {

    public static final BlockRegistry instance = new BlockRegistry();

    private final Map<String, Registration> types = new HashMap<>();

    private BlockRegistry() {
        registerBuiltins();
    }

    private void registerBuiltins() {
        register(new BlockStone());
    }

    public void register(BlockType base) {
        Registration reg = new Registration(base);
        types.put(base.getId(), reg);
    }

    public BlockType getBlock(String id) {
        if (types.containsKey(id)) {
            return types.get(id).type;
        } else {
            return null;
        }
    }

    BlockType modify(GlowBlockType type, BlockProperty prop, Object value) {
        if (!type.getProperties().contains(prop)) {
            throw new IllegalArgumentException();
        }
        if (type.getProperty(prop).equals(value)) {
            return type;
        }
        Map<PropertyContainer, BlockType> variants = types.get(type.getId()).variants;
        PropertyContainer key = type.properties.with(prop, value);
        if (!variants.containsKey(key)) {
            GlowBlockType newType = type.clone();
            newType.properties = key;
            variants.put(key, newType);
            return newType;
        } else {
            return variants.get(key);
        }
    }

    private static final class Registration {
        private final BlockType type;
        private final Map<PropertyContainer, BlockType> variants;

        private Registration(BlockType type) {
            this.type = type;
            this.variants = new HashMap<>();
        }
    }
}
