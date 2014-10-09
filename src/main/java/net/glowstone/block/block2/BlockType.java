package net.glowstone.block.block2;

import java.util.Collection;

/**
 * Todo: Javadoc for BlockType.
 */
public interface BlockType {

    String getId();

    String getFullId();

    boolean isBaseType();

    BlockType getBaseType();

    BlockType withProperty(BlockProperty prop, Object value);

    Collection<BlockProperty> getProperties();

    Object getProperty(BlockProperty prop);

}
