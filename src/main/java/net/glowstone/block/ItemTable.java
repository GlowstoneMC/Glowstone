package net.glowstone.block;

import net.glowstone.block.blocktype.BlockType;
import net.glowstone.block.itemtype.ItemType;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * The lookup table for block and item types.
 */
public final class ItemTable {

    private static final ItemTable INSTANCE = new ItemTable();

    private final Map<Integer, ItemType> idToType = new HashMap<>();
    private final Map<ItemType, Integer> typeToId = new HashMap<>();

    private int nextBlockId, nextItemId;

    private ItemTable() {
        registerDefaults();
    }

    public static ItemTable instance() {
        return INSTANCE;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Registration

    private void registerDefaults() {
        reg(Material.AIR, null);
    }

    private void reg(Material material, ItemType type) {
        idToType.put(material.getId(), type);
        typeToId.put(type, material.getId());

        if (material.isBlock()) {
            nextBlockId = Math.max(nextBlockId, material.getId() + 1);
        } else {
            nextItemId = Math.max(nextItemId, material.getId() + 1);
        }
    }

    public void register(ItemType type) {
        int id;
        if (type instanceof BlockType) {
            id = nextBlockId;
        } else {
            id = nextItemId;
        }

        while (idToType.containsKey(id)) {
            ++id;
        }

        idToType.put(id, type);
        typeToId.put(type, id);

        if (type instanceof BlockType) {
            nextBlockId = id + 1;
        } else {
            nextItemId = id + 1;
        }
    }

    private ItemType createDefault(int id) {
        Material material = Material.getMaterial(id);
        if (material == null) {
            return null;
        }

        ItemType result;
        if (material.isBlock()) {
            result = new BlockType();
        } else {
            result = new ItemType();
        }
        reg(material, result);
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Type access

    public ItemType getItem(int id) {
        ItemType type = idToType.get(id);
        if (type == null) {
            type = createDefault(id);
        }
        return type;
    }

    public BlockType getBlock(int id) {
        ItemType itemType = getItem(id);
        if (itemType instanceof BlockType) {
            return (BlockType) itemType;
        }
        return null;
    }

    public ItemType getItem(Material mat) {
        return getItem(mat.getId());
    }

    public BlockType getBlock(Material mat) {
        return getBlock(mat.getId());
    }

    public int getId(ItemType type) {
        if (typeToId.containsKey(type)) {
            return typeToId.get(type);
        } else {
            return 0;
        }
    }

}
