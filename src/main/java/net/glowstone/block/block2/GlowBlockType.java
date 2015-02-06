package net.glowstone.block.block2;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import net.glowstone.block.block2.sponge.BlockProperty;
import net.glowstone.block.block2.sponge.BlockState;
import net.glowstone.block.block2.sponge.BlockType;

import java.util.*;

/**
 * Implementation of {@link BlockType}.
 */
public class GlowBlockType implements BlockType {

    private final String id;
    private final BlockBehavior behavior;

    private final List<GlowBlockState> states;
    private final Map<String, BlockProperty<?>> properties;

    public GlowBlockType(String id, BlockBehavior behavior, List<BlockProperty<?>> propertyList) {
        this.id = id;
        this.behavior = behavior;

        if (propertyList.isEmpty()) {
            properties = new HashMap<>();
            states = Arrays.asList(new GlowBlockState(this, ImmutableMap.<BlockProperty<?>, Comparable<?>>of()));
            return;
        }

        properties = new HashMap<>();
        states = new ArrayList<>();

        List<Pair<BlockProperty<?>, List<Comparable<?>>>> allValues = new LinkedList<>();
        for (BlockProperty<?> prop : propertyList) {
            properties.put(prop.getName(), prop);
            allValues.add(0, new Pair<BlockProperty<?>, List<Comparable<?>>>(prop, new ArrayList<Comparable<?>>(prop.getValidValues())));
        }

        // generate all possible states from list of properties
        int i = 0;
        while (true) {
            ImmutableMap.Builder<BlockProperty<?>, Comparable<?>> builder = ImmutableMap.builder();
            int j = i;
            for (Pair<BlockProperty<?>, List<Comparable<?>>> pair : allValues) {
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
    public BlockState getDefaultState() {
        return DefaultIdTable.INSTANCE.getChildType(this, 0);
    }

    @Override
    public BlockState getStateFromDataValue(byte data) {
        // nb: according to Sponge, always returns a value, but may
        // return null here if that value is invalid
        return DefaultIdTable.INSTANCE.getChildType(this, data & 0xf);
    }

    @Override
    public boolean getTickRandomly() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTickRandomly(boolean tickRandomly) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final String toString() {
        return "GlowBlockType[" + id + "]";
    }

    List<GlowBlockState> getAllStates() {
        return states;
    }

    Collection<String> getPropertyNames() {
        return Collections.unmodifiableCollection(properties.keySet());
    }

    Optional<BlockProperty<?>> getPropertyByName(String name) {
        return Optional.<BlockProperty<?>>fromNullable(properties.get(name));
    }

    BlockState withProperty(GlowBlockState state, BlockProperty<?> property, Comparable<?> value) {
        ImmutableMap.Builder<BlockProperty<?>, Comparable<?>> builder = ImmutableMap.builder();
        for (Map.Entry<BlockProperty<?>, ? extends Comparable<?>> entry : state.getProperties().entrySet()) {
            if (!entry.getKey().equals(property)) {
                builder.put(entry.getKey(), entry.getValue());
            }
        }
        builder.put(property, value);
        ImmutableMap<BlockProperty<?>, Comparable<?>> map = builder.build();

        for (BlockState available : states) {
            if (available != null && available.getProperties().equals(map)) {
                return available;
            }
        }

        throw new IllegalArgumentException("No state of " + this + " with values " + map);
    }

    private static class Pair<A, B> {
        final A first;
        final B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }
}
