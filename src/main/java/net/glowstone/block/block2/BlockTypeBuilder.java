package net.glowstone.block.block2;

import net.glowstone.block.block2.details.BlockFacing;
import net.glowstone.block.block2.details.DefaultBlockBehavior;
import net.glowstone.block.block2.details.ListBlockBehavior;
import net.glowstone.block.block2.details.SlabHalf;
import net.glowstone.block.block2.sponge.BlockProperty;
import net.glowstone.block.block2.sponge.BlockState;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builder for defining {@link GlowBlockType}s.
 */
public final class BlockTypeBuilder {

    private final String id;
    private final List<BlockProperty<?>> propertyList = new LinkedList<>();
    private final List<BlockBehavior> behaviors = new LinkedList<>();
    private IdResolver idResolver;
    private int oldId = -1;

    public BlockTypeBuilder(String id) {
        this.id = id;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Attributes

    public BlockTypeBuilder oldId(int oldId) {
        this.oldId = oldId;
        return this;
    }

    public BlockTypeBuilder behavior(BlockBehavior behavior) {
        behaviors.add(behavior);
        return this;
    }

    public BlockTypeBuilder behavior(BlockBehavior first, BlockBehavior... rest) {
        behaviors.add(first);
        Collections.addAll(behaviors, rest);
        return this;
    }

    public BlockTypeBuilder idResolver(IdResolver resolver) {
        this.idResolver = resolver;
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////
    // BlockProperties

    public BlockTypeBuilder property(BlockProperty<?> prop) {
        propertyList.add(prop);
        return this;
    }

    public BlockTypeBuilder booleanProperty(String name) {
        return property(GlowBlockProperty.ofBoolean(name));
    }

    public BlockTypeBuilder rangeProperty(String name, int min, int max) {
        return property(GlowBlockProperty.ofRange(name, min, max));
    }

    public <E extends Enum> BlockTypeBuilder enumProperty(String name, E[] vals) {
        return property(GlowBlockProperty.ofEnum(name, vals));
    }

    public <E extends Enum> BlockTypeBuilder enumProperty(String name, E[] vals, Map<E, String> names) {
        return property(GlowBlockProperty.ofNamedEnum(name, vals, names));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Common block archetypes

    public BlockTypeBuilder stairs() {
        return enumProperty("facing", BlockFacing.CARDINAL).enumProperty("half", SlabHalf.values());
    }

    public BlockTypeBuilder door() {
        return this; // todo
    }

    ////////////////////////////////////////////////////////////////////////////
    // Completion

    public GlowBlockType build() {
        BlockBehavior behavior;
        if (behaviors.isEmpty()) {
            behavior = DefaultBlockBehavior.instance;
        } else {
            behavior = new ListBlockBehavior(behaviors);
        }
        if (idResolver == null) {
            idResolver = DefaultIdResolver.instance;
        }
        return new GlowBlockType(id, behavior, propertyList, idResolver);
    }

    GlowBlockType register() {
        GlowBlockType type = build();
        BlockRegistry.instance.register(type);
        if (oldId >= 0) {
            BlockRegistry.instance.registerOldId(oldId, type);
        }
        return type;
    }

    private static class DefaultIdResolver implements IdResolver {
        private static final DefaultIdResolver instance = new DefaultIdResolver();

        @Override
        public int getId(BlockState state, int suggested) {
            return suggested;
        }
    }
}
