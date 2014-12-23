package net.glowstone.block.block2;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import net.glowstone.block.block2.sponge.BlockProperty;
import net.glowstone.block.block2.sponge.BlockState;

import java.util.Collection;
import java.util.Map;

/**
 * Implementation of {@link BlockState}.
 */
public final class GlowBlockState implements BlockState {

    private final GlowBlockType type;
    private final ImmutableMap<BlockProperty<?>, Comparable<?>> properties;
    private byte index = -1;

    public GlowBlockState(GlowBlockType type, ImmutableMap<BlockProperty<?>, Comparable<?>> properties) {
        this.type = type;
        this.properties = properties;
    }

    void setIndex(byte index) {
        this.index = index;
    }

    @Override
    public GlowBlockType getType() {
        return type;
    }

    @Override
    public ImmutableMap<BlockProperty<?>, ? extends Comparable<?>> getProperties() {
        return properties;
    }

    @Override
    public Collection<String> getPropertyNames() {
        return type.getPropertyNames();
    }

    @Override
    public Optional<BlockProperty<?>> getPropertyByName(String name) {
        return type.getPropertyByName(name);
    }

    @Override
    public Optional<? extends Comparable<?>> getPropertyValue(String name) {
        Optional<BlockProperty<?>> prop = getPropertyByName(name);
        if (!prop.isPresent()) {
            return Optional.absent();
        }
        return Optional.of(properties.get(prop.get()));
    }

    @Override
    public BlockState withProperty(BlockProperty<?> property, Comparable<?> value) {
        return type.withProperty(this, property, GlowBlockProperty.validate(property, value));
    }

    @Override
    public BlockState cycleProperty(BlockProperty<?> property) {
        return withProperty(property, GlowBlockProperty.cycle(property, properties.get(property)));
    }

    @Override
    @Deprecated
    public byte getDataValue() {
        return index;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("GlowBlockState{");
        builder.append(type.getId());
        for (Map.Entry<BlockProperty<?>, Comparable<?>> entry : properties.entrySet()) {
            builder.append(',').append(entry.getKey().getName()).append('=').append(entry.getValue());
        }
        return builder.append('}').toString();
    }
}
