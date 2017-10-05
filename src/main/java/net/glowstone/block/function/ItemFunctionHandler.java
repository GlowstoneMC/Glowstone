package net.glowstone.block.function;

import net.glowstone.block.ItemTable;
import net.glowstone.block.itemtype.ItemType;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Assigns functions (that it has registered for) to items
 */
public interface ItemFunctionHandler extends BiConsumer<ItemType, ItemFunction> {
    @Override
    default void accept(ItemType item, ItemFunction itemFunction) {
        if (!Objects.equals(ItemTable.instance().getFunctionHandler(itemFunction.getFunctionality()), this)) {
            throw new IllegalArgumentException(getClass().getName() + " does not accept functions of type " + itemFunction.getFunctionality());
        }
    }
}
