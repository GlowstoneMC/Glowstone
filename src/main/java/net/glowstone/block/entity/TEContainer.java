package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.inventory.GlowInventory;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Base class for container tile entities (those with inventories).
 */
public abstract class TEContainer extends TileEntity {

    private final GlowInventory inventory;

    public TEContainer(GlowBlock block, GlowInventory inventory) {
        super(block);
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setContents(ItemStack... contents) {
        this.inventory.setContents(contents);
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        if (tag.isList("Items", TagType.COMPOUND)) {
            inventory.setContents(NbtSerialization.readInventory(tag.getCompoundList("Items"), 0, inventory.getSize()));
        }
        if (tag.isString("CustomName")) {
            inventory.setTitle(tag.getString("CustomName"));
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putCompoundList("Items", NbtSerialization.writeInventory(inventory.getContents(), 0));
        if (!inventory.getTitle().equals(inventory.getType().getDefaultTitle())) {
            tag.putString("CustomName", inventory.getTitle());
        }
    }
}
