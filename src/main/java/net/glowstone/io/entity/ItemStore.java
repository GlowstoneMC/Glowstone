package net.glowstone.io.entity;

import net.glowstone.entity.objects.GlowItem;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;

class ItemStore extends EntityStore<GlowItem> {

    public ItemStore() {
        super(GlowItem.class, "Item");
    }

    @Override
    public GlowItem createEntity(Location location, CompoundTag compound) {
        // item will be set by loading code below
        return new GlowItem(location, null);
    }

    // todo: the following tags
    // - "Health"
    // - "Owner"
    // - "Thrower"

    @Override
    public void load(GlowItem entity, CompoundTag tag) {
        super.load(entity, tag);

        if (tag.isCompound("Item")) {
            entity.setItemStack(NbtSerialization.readItem(tag.getCompound("Item")));
        }
        if (tag.isShort("Age")) {
            entity.setTicksLived(tag.getShort("Age"));
        }
        if (tag.isShort("PickupDelay")) {
            entity.setPickupDelay(tag.getShort("PickupDelay"));
        }
    }

    @Override
    public void save(GlowItem entity, CompoundTag tag) {
        super.save(entity, tag);

        tag.putCompound("Item", NbtSerialization.writeItem(entity.getItemStack(), -1));
        tag.putShort("Age", entity.getTicksLived());
        tag.putShort("Health", 5);
        tag.putShort("PickupDelay", entity.getPickupDelay());
    }
}
