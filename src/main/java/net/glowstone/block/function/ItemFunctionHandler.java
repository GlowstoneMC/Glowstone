package net.glowstone.block.function;

import net.glowstone.block.itemtype.ItemType;

import java.util.function.BiConsumer;

/**
 * Assigns functions (that it has registered for) to items
 */
public interface ItemFunctionHandler extends BiConsumer<ItemType, ItemFunction> {
    @Override
    void accept(ItemType item, ItemFunction itemFunction);
}
