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

    <T> BlockType withProperty(BlockProperty<T> prop, T value);

    Collection<BlockProperty<?>> getProperties();

    <T> T getProperty(BlockProperty<T> prop);

}
