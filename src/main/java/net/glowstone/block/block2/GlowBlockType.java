package net.glowstone.block.block2;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.translation.Translation;

import java.util.*;

/**
 * Implementation of {@link org.spongepowered.api.block.BlockType}.
 */
public class GlowBlockType implements BlockType {

    private final String id;
    private final BlockBehavior behavior;

    private final List<GlowBlockState> states;
    private final Map<String, BlockTrait<?>> properties;

    public GlowBlockType(String id, BlockBehavior behavior, List<BlockTrait<?>> propertyList) {
        this.id = id;
        this.behavior = behavior;

        if (propertyList.isEmpty()) {
            properties = new HashMap<>();
            states = Arrays.asList(new GlowBlockState(this, Collections.EMPTY_LIST));
            return;
        }

        properties = new HashMap<>();
        states = new ArrayList<>();

        List<Pair<BlockTrait<?>, List<Comparable<?>>>> allValues = new LinkedList<>();
        for (BlockTrait<?> prop : propertyList) {
            properties.put(prop.getName(), prop);
            allValues.add(0, new Pair<>(prop, new ArrayList<>(prop.getPossibleValues())));
        }

        // generate all possible states from list of properties
        int i = 0;
        while (true) {
            ImmutableMap.Builder<BlockTrait<?>, Comparable<?>> builder = ImmutableMap.builder();
            int j = i;
            for (Pair<BlockTrait<?>, List<Comparable<?>>> pair : allValues) {
                List<Comparable<?>> list = pair.second;
                builder.put(pair.first, list.get(j % list.size()));
                j /= list.size();
            }
            if (j > 0) {
                // end when we hit indexes not covered by the above
                break;
            }

            states.add(new GlowBlockState(this, builder.build()));
            ++i;
        }
    }

    public BlockBehavior getBehavior() {
        return behavior;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public BlockState getDefaultState() {
        return null;
    }

    @Override
    public java.util.Optional<ItemType> getItem() {
        return null;
    }

    public boolean getTickRandomly() {
        throw new UnsupportedOperationException();
    }

    public void setTickRandomly(boolean tickRandomly) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<BlockTrait<?>> getTraits() {
        return null;
    }

    @Override
    public java.util.Optional<BlockTrait<?>> getTrait(String blockTrait) {
        return null;
    }

    List<GlowBlockState> getAllStates() {
        return states;
    }

    Collection<String> getPropertyNames() {
        return Collections.unmodifiableCollection(properties.keySet());
    }

    Optional<BlockTrait<?>> getPropertyByName(String name) {
        return Optional.fromNullable(properties.get(name));
    }

    public BlockState withTrait(GlowBlockState state, BlockTrait<?> property, Object value) {
        ImmutableMap.Builder<BlockTrait<?>, Object> builder = ImmutableMap.builder();
        state.getTraitMap().entrySet().stream().filter(entry -> !entry.getKey().equals(property)).forEach(entry -> {
            builder.put(entry.getKey(), entry.getValue());
        });
        builder.put(property, value);
        ImmutableMap<BlockTrait<?>, Object> map = builder.build();

        for (BlockState available : states) {
            if (available != null && available.getTraits().equals(map)) {
                return available;
            }
        }

        throw new IllegalArgumentException("No state of " + this + " with values " + map);
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
    public Translation getTranslation() {
        return null;
    }

    private static class Pair<A, B> {
        final A first;
        final B second;

        Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }
}
