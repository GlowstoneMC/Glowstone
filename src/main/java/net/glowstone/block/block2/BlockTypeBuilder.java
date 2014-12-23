package net.glowstone.block.block2;

import net.glowstone.block.block2.behavior.BlockBehavior;
import net.glowstone.block.block2.types.DefaultBlockBehavior;
import net.glowstone.block.block2.behavior.ListBlockBehavior;
import net.glowstone.block.block2.sponge.BlockProperty;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Builder for defining {@link GlowBlockType}s.
 */
public final class BlockTypeBuilder {

    private final String id;
    private final List<BlockProperty<?>> propertyList = new LinkedList<>();
    private final List<BlockBehavior> behaviors = new LinkedList<>();

    public BlockTypeBuilder(String id) {
        this.id = id;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Attributes

    public BlockTypeBuilder oldId(int i) {
        // todo
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

    public BlockTypeBuilder enumProperty(String name, Class<? extends Enum> clazz) {
        return property(GlowBlockProperty.ofEnum(name, clazz));
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
        return new GlowBlockType(id, behavior, propertyList);
    }

    GlowBlockType register() {
        GlowBlockType type = build();
        BlockRegistry.instance.register(build());
        return type;
    }
}
