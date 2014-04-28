package net.glowstone.io.entity;

import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.entity.objects.GlowItem;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;

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
            entity.setItemStack(NbtSerialization.readItem(compound.getCompound("Item")));
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
        CompoundTag item = NbtSerialization.writeItem(entity.getItemStack(), 0);
        item.remove("Slot");
        tag.putCompound("Item", item);
    }
}
