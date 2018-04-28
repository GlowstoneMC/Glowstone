package net.glowstone.io.entity;

import net.glowstone.entity.objects.GlowItem;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

class ItemStore extends EntityStore<GlowItem> {

    public ItemStore() {
        super(GlowItem.class, EntityType.DROPPED_ITEM);
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
        tag.readItem(entity::setItemStack, "Item");
        tag.readShort(entity::setTicksLived, "Age");
        tag.readShort(entity::setPickupDelay, "PickupDelay");
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
