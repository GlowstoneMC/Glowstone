package net.glowstone.block.block2;

import java.util.Collection;

/**
 * Todo: Javadoc for BlockProperty.
 */
public interface BlockProperty {

    String getName();

    Type getType();

    interface IntegerProperty extends BlockProperty {

        int getMinimum();

        int getMaximum();

        int getDefault();

    }

    interface StringProperty extends BlockProperty {

        Collection<String> getValues();

        String getDefault();

    }

    enum Type {
        BOOLEAN,
        INTEGER,
        STRING
    }
}
