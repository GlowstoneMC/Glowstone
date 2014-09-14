package net.glowstone.io.entity;

import net.glowstone.entity.objects.GlowItemFrame;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;

import org.bukkit.Location;

class ItemFrameStore extends EntityStore<GlowItemFrame> {

    public ItemFrameStore() {
        super(GlowItemFrame.class, "ItemFrame");
    }

    public GlowItemFrame createEntity(Location location, CompoundTag compound) {
        // item frame will be set by loading code below
        return new GlowItemFrame(null, location, null);
    }

    @Override
    public void load(GlowItemFrame entity, CompoundTag tag) {
        super.load(entity, tag);

        if (tag.isInt("Facing")) {
            entity.setFacingDirectionNumber((tag.getInt("Facing")));
        }
        if (tag.isCompound("Item")) {
            entity.SetItemInFrame(NbtSerialization.readItem(tag.getCompound("Item")));
        }

        if (tag.isInt("Rotation")) {
            entity.setRotationNumber((tag.getInt("Rotation")));
        }
    }

    @Override
    public void save(GlowItemFrame entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("Facing", entity.getFacingNumber());
        tag.putCompound("Item", NbtSerialization.writeItem(entity.getItem(), -1));
        tag.putInt("Rotation", entity.getRotationNumber());
    }
}
