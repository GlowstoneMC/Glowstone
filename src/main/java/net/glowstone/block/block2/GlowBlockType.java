package net.glowstone.block.block2;

import java.util.*;

/**
 * Implementation of {@link BlockType}.
 */
public class GlowBlockType implements BlockType, Cloneable {

    private final String id;
    PropertyContainer properties;

    public GlowBlockType(String id, BlockProperty... properties) {
        this.id = id;
        this.properties = new PropertyContainer(properties);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public final String getFullId() {
        return id + '[' + properties + ']';
    }

    @Override
    public boolean isBaseType() {
        return BlockRegistry.instance.getBlock(id) == this;
    }

    @Override
    public BlockType getBaseType() {
        return BlockRegistry.instance.getBlock(id);
    }

    @Override
    public final <T> BlockType withProperty(BlockProperty<T> prop, T value) {
        return BlockRegistry.instance.modify(this, prop, value);
    }

    @Override
    public final Collection<BlockProperty<?>> getProperties() {
        return Collections.unmodifiableCollection(properties.keySet());
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T getProperty(BlockProperty<T> prop) {
        return (T) properties.get(prop);
    }

    @Override
    public final String toString() {
        return getFullId();
    }

    @Override
    protected final GlowBlockType clone() {
        try {
            return (GlowBlockType) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }
}
