package net.glowstone.block.block2;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.util.Cycleable;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Implementation of {@link BlockState}.
 */
public final class GlowBlockState implements BlockState {

    private final GlowBlockType type;
    private final Collection<BlockTrait<?>> traits;

    public GlowBlockState(GlowBlockType type, Collection<BlockTrait<?>> traits) {
        this.type = type;
        this.traits = traits;
    }

    @Override
    public BlockType getType() {
        return (BlockType) type;
    }

    @Override
    public BlockState withExtendedProperties(Location<World> location) {
        return null;
    }

    @Override
    public BlockState cycleValue(Key<? extends BaseValue<? extends Cycleable<?>>> key) {
        return null;
    }

    @Override
    public BlockSnapshot snapshotFor(Location<World> location) {
        return null;
    }

    @Override
    public <T extends Comparable<T>> java.util.Optional<T> getTraitValue(BlockTrait<T> blockTrait) {
        return null;
    }

    @Override
    public java.util.Optional<BlockTrait<?>> getTrait(String blockTrait) {
        return null;
    }

    @Override
    public java.util.Optional<BlockState> withTrait(BlockTrait<?> trait, Object value) {
        return null;
    }

    @Override
    public Collection<BlockTrait<?>> getTraits() {
        return traits;
    }

    @Override
    public Collection<?> getTraitValues() {
        return null;
    }

    @Override
    public Map<BlockTrait<?>, ?> getTraitMap() {
        return null;
    }

    @Override
    public Collection<String> getPropertyNames() {
        return type.getPropertyNames();
    }

    @Override
    public Optional<BlockTrait<?>> getPropertyByName(String name) {
        return type.getPropertyByName(name);
    }

    @Override
    public Optional<? extends Comparable<?>> getPropertyValue(String name) {
        Optional<BlockTrait<?>> prop = getPropertyByName(name);
        if (!prop.isPresent()) {
            return Optional.absent();
        }
        return Optional.of(properties.get(prop.get()));
    }

    @Override
    public BlockState withProperty(BlockTrait<?> property, Comparable<?> value) {
        return type.withProperty(this, property, GlowBlockTrait.validate(property, value));
    }

    @Override
    public BlockState cycleProperty(BlockTrait<?> property) {
        return withProperty(property, GlowBlockTrait.cycle(property, properties.get(property)));
    }

    @Override
    @Deprecated
    public byte getDataValue() {
        return (byte) DefaultIdTable.INSTANCE.getChildId(this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("GlowBlockState[");
        builder.append(type.getId());
        for (Map.Entry<BlockTrait<?>, Comparable<?>> entry : properties.entrySet()) {
            builder.append(',').append(entry.getKey().getName()).append('=').append(entry.getValue());
        }
        return builder.append(']').toString();
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<ImmutableDataManipulator<?, ?>> getManipulators() {
        return null;
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        return null;
    }

    @Override
    public <T extends Property<?, ?>> java.util.Optional<T> getProperty(Direction direction, Class<T> clazz) {
        return null;
    }

    @Override
    public <T extends Property<?, ?>> java.util.Optional<T> getProperty(Class<T> propertyClass) {
        return null;
    }

    @Override
    public Collection<Property<?, ?>> getApplicableProperties() {
        return null;
    }

    @Override
    public boolean supports(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        return false;
    }

    @Override
    public <E> java.util.Optional<BlockState> transform(Key<? extends BaseValue<E>> key, Function<E, E> function) {
        return null;
    }

    @Override
    public <E> java.util.Optional<BlockState> with(Key<? extends BaseValue<E>> key, E value) {
        return null;
    }

    @Override
    public java.util.Optional<BlockState> with(BaseValue<?> value) {
        return null;
    }

    @Override
    public java.util.Optional<BlockState> with(ImmutableDataManipulator<?, ?> valueContainer) {
        return null;
    }

    @Override
    public java.util.Optional<BlockState> with(Iterable<ImmutableDataManipulator<?, ?>> valueContainers) {
        return null;
    }

    @Override
    public java.util.Optional<BlockState> without(Class<? extends ImmutableDataManipulator<?, ?>> containerClass) {
        return null;
    }

    @Override
    public BlockState merge(BlockState that) {
        return null;
    }

    @Override
    public BlockState merge(BlockState that, MergeFunction function) {
        return null;
    }

    @Override
    public List<ImmutableDataManipulator<?, ?>> getContainers() {
        return null;
    }

    @Override
    public <E> java.util.Optional<E> get(Key<? extends BaseValue<E>> key) {
        return null;
    }

    @Override
    public <E, V extends BaseValue<E>> java.util.Optional<V> getValue(Key<V> key) {
        return null;
    }

    @Override
    public boolean supports(Key<?> key) {
        return false;
    }

    @Override
    public BlockState copy() {
        return null;
    }

    @Override
    public Set<Key<?>> getKeys() {
        return null;
    }

    @Override
    public Set<ImmutableValue<?>> getValues() {
        return null;
    }
}
