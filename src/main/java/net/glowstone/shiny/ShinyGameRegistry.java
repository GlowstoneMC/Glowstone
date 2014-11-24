package net.glowstone.shiny;

import com.google.common.base.Optional;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.ItemType;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Implementation of {@link GameRegistry}.
 */
public class ShinyGameRegistry implements GameRegistry {

    private final Map<String, BlockType> blocks = new HashMap<>();
    private final Map<String, ItemType> items = new HashMap<>();
    private final Map<Object, String> idMap = new IdentityHashMap<>();

    private void register(BlockType block) {
        blocks.put(block.getId(), block);
        idMap.put(block, block.getId());
    }

    private void register(ItemType item) {
        items.put(item.getId(), item);
        idMap.put(item, item.getId());
    }

    @Override
    public Optional<BlockType> getBlock(String id) {
        return Optional.fromNullable(blocks.get(id));
    }

    @Override
    public Optional<ItemType> getItem(String id) {
        return Optional.fromNullable(items.get(id));
    }

    @Override
    public Optional<String> getId(Object obj) {
        return Optional.fromNullable(idMap.get(obj));
    }
}
