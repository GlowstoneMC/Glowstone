package net.glowstone.block.block2;

import net.glowstone.block.block2.sponge.BlockProperty;

import java.util.LinkedList;
import java.util.List;

/**
 * Builder for defining {@link GlowBlockType}s.
 */
public final class BlockTypeBuilder {

    private final String id;
    private final List<BlockProperty<?>> propertyList = new LinkedList<>();

    public BlockTypeBuilder(String id) {
        this.id = id;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Attributes

    public BlockTypeBuilder oldId(int i) {
        // todo
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
        return new GlowBlockType(id, propertyList);
    }

    GlowBlockType register() {
        GlowBlockType type = build();
        BlockRegistry.instance.register(build());
        return type;
    }
}
