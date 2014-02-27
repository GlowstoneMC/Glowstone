package net.glowstone.block;

import net.glowstone.GlowServer;
import net.glowstone.block.blocktype.BlockMobSpawner;
import net.glowstone.block.blocktype.BlockNote;
import net.glowstone.block.blocktype.BlockSign;
import net.glowstone.block.blocktype.BlockType;
import net.glowstone.block.itemtype.ItemSign;
import net.glowstone.block.itemtype.ItemSugarcane;
import net.glowstone.block.itemtype.ItemType;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

/**
 * The lookup table for block and item types.
 */
public final class ItemTable {

    private static final ItemTable INSTANCE = new ItemTable();

    static {
        INSTANCE.registerBuiltins();
    }

    public static ItemTable instance() {
        return INSTANCE;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Data

    private final Map<Integer, ItemType> idToType = new HashMap<>();

    private int nextBlockId, nextItemId;

    ////////////////////////////////////////////////////////////////////////////
    // Registration

    private void registerBuiltins() {
        reg(Material.NOTE_BLOCK, new BlockNote());
        reg(Material.MOB_SPAWNER, new BlockMobSpawner());
        reg(Material.SIGN_POST, new BlockSign());
        reg(Material.WALL_SIGN, new BlockSign());

        reg(Material.SIGN, new ItemSign());
        reg(Material.SUGAR_CANE, new ItemSugarcane());
    }

    private void reg(Material material, ItemType type) {
        if (material.isBlock() != (type instanceof BlockType)) {
            throw new IllegalArgumentException("Cannot mismatch item and block: " + material + ", " + type);
        }

        idToType.put(material.getId(), type);
        type.setId(material.getId());

        GlowServer.logger.info("Registered built-in: " + type);

        if (material.isBlock()) {
            nextBlockId = Math.max(nextBlockId, material.getId() + 1);
        } else {
            nextItemId = Math.max(nextItemId, material.getId() + 1);
        }
    }

    /**
     * Register a new, non-Vanilla ItemType. It will be assigned an ID automatically.
     * @param type the ItemType to register.
     */
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
        type.setId(id);

        if (type instanceof BlockType) {
            nextBlockId = id + 1;
        } else {
            nextItemId = id + 1;
        }
    }

    private ItemType createDefault(int id) {
        Material material = Material.getMaterial(id);
        if (material == null || id == 0) {
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

}
