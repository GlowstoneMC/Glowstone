package net.glowstone.block.block2;

import java.util.Collection;

/**
 * One of the possible types a block could take on.
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
