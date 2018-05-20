package net.glowstone.block.function;

import net.glowstone.block.blocktype.BlockType;
import net.glowstone.block.itemtype.ItemType;

public class GlowBlockFunctionHandler implements ItemFunctionHandler {
    @Override
    public void accept(ItemType item, ItemFunction itemFunction) {
        if (item instanceof BlockType) {
            item.addFunction(itemFunction);
        } else {
            throw new IllegalArgumentException(item + " does not accept functions of type " + itemFunction.getFunctionality());
        }
    }
}
