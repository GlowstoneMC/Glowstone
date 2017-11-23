package net.glowstone.block.function;

import net.glowstone.block.itemtype.ItemType;

public class GlowItemFunctionHandler implements ItemFunctionHandler {
    @Override
    public void accept(ItemType item, ItemFunction itemFunction) {
        item.addFunction(itemFunction);
    }
}
