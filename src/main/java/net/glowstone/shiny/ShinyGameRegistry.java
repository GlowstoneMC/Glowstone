package net.glowstone.shiny;

import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.block.Block;
import org.spongepowered.api.item.Item;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Implementation of {@link GameRegistry}.
 */
public class ShinyGameRegistry implements GameRegistry {

    private final Map<String, Block> blocks = new HashMap<>();
    private final Map<String, Item> items = new HashMap<>();
    private final Map<Object, String> idMap = new IdentityHashMap<>();

    private void register(Block block) {
        blocks.put(block.getId(), block);
        idMap.put(block, block.getId());
    }

    private void register(Item item) {
        items.put(item.getID(), item);
        idMap.put(item, item.getID());
    }

    @Nullable
    @Override
    public Block getBlock(String id) {
        return blocks.get(id);
    }

    @Nullable
    @Override
    public Item getItem(String id) {
        return items.get(id);
    }

    @Nullable
    @Override
    public String getID(Object obj) {
        return idMap.get(obj);
    }
}
