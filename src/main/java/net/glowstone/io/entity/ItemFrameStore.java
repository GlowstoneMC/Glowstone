package net.glowstone.io.entity;

import net.glowstone.entity.GlowHangingEntity.HangingFace;
import net.glowstone.entity.objects.GlowItemFrame;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.entity.EntityType;

class ItemFrameStore extends HangingStore<GlowItemFrame> {

    public ItemFrameStore() {
        super(GlowItemFrame.class, EntityType.ITEM_FRAME);
    }

    @Override
    public GlowItemFrame createEntity(Location location, CompoundTag compound) {
        // item frame will be set by loading code below
        return new GlowItemFrame(null, location, null);
    }

    @Override
    public void load(GlowItemFrame entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.consumeItem(entity::setItem, "Item");
        tag.consumeInt(rotation -> entity.setRotation(Rotation.values()[rotation]), "Rotation");
    }

    @Override
    public void save(GlowItemFrame entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putByte("Facing", HangingFace.getByBlockFace(entity.getFacing()).ordinal());
        tag.putCompound("Item", NbtSerialization.writeItem(entity.getItem(), -1));
        tag.putInt("Rotation", entity.getRotation().ordinal());
    }
}
