package net.glowstone.io.entity;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.inventory.ItemStack;

public class ItemEntityStore extends EntityStore<GlowItem> {

    public ItemEntityStore() {
        super(GlowItem.class, "Item");
    }

    @Override
    public GlowItem load(GlowServer server, GlowWorld world, CompoundTag compound) {
        GlowItem item = new GlowItem(server, world, null);
        load(item, compound);
        return item;
    }

    @Override
    public void load(GlowItem entity, CompoundTag compound) {
        if (compound.isCompound("Item")) {
            entity.setItemStack(readItemStack(compound.getCompound("Item")));
        }
        if (compound.isInt("Health")) {
            // entity.setHealth(((IntTag)compound.getValue().get("Health")).getValue());
        }
        if (compound.isInt("Age")) {
            entity.setTicksLived(compound.getInt("Age"));
        }
    }

    @Override
    public void save(GlowItem entity, CompoundTag tag) {
        super.save(entity, tag);

        ItemStack stack = entity.getItemStack();
        CompoundTag item = new CompoundTag();
        item.putShort("id", stack.getTypeId());
        item.putShort("Damage", stack.getDurability());
        item.putByte("Count", stack.getAmount());

        tag.putCompound("Item", item);
    }

    public ItemStack readItemStack(CompoundTag tag) {
        ItemStack stack = null;
        short id = tag.isShort("id") ? tag.getShort("id") : 0;
        short damage = tag.isShort("Damage") ? tag.getShort("Damage") : 0;
        byte count = tag.isByte("Count") ? tag.getByte("Count") : 0;
        if (id != 0 && count != 0) {
            stack = new ItemStack(id, count, damage);
        }
        return stack;
    }
}
