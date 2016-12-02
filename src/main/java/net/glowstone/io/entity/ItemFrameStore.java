package net.glowstone.io.entity;

import net.glowstone.entity.objects.GlowItemFrame;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.entity.EntityType;

class ItemFrameStore extends EntityStore<GlowItemFrame> {

    public ItemFrameStore() {
        super(GlowItemFrame.class, EntityType.ITEM_FRAME);
    }

    public GlowItemFrame createEntity(Location location, CompoundTag compound) {
        // item frame will be set by loading code below
        int facing = compound.getByte("Facing");
        GlowItemFrame itemFrame = new GlowItemFrame(null, location, null);
        itemFrame.setFacingDirectionNumber(facing);
        return itemFrame;
    }

    @Override
    public void load(GlowItemFrame entity, CompoundTag tag) {
        super.load(entity, tag);

        if (tag.isByte("Facing")) {
            entity.setFacingDirectionNumber(tag.getByte("Facing"));
        }
        if (tag.isCompound("Item")) {
            entity.setItemInFrame(NbtSerialization.readItem(tag.getCompound("Item")));
        }

        if (tag.isInt("Rotation")) {
            entity.setRotation(Rotation.values()[tag.getInt("Rotation")]);
        }
    }

    @Override
    public void save(GlowItemFrame entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putByte("Facing", entity.getFacingNumber());
        tag.putCompound("Item", NbtSerialization.writeItem(entity.getItem(), -1));
        tag.putInt("Rotation", entity.getRotation().ordinal());
    }
}
