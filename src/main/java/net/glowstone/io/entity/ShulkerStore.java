package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowShulker;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;

public class ShulkerStore extends MonsterStore<GlowShulker> {

    public ShulkerStore() {
        super(GlowShulker.class, EntityType.SHULKER, GlowShulker::new);
    }

    @Override
    public void load(GlowShulker entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readByte("Peek", entity::setShieldHeight);
        tag.readByte("AttachFace", direction -> entity.setDirection(GlowShulker.Facing.values()[direction])
        );
        tag.readByte("Color", color -> entity.setColor(DyeColor.getByWoolData(color)));
    }

    @Override
    public void save(GlowShulker entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putByte("Peek", entity.getShieldHeight());
        tag.putByte("AttachFace", entity.getFacingDirection().ordinal());
        tag.putInt("APX", entity.getLocation().getBlockX());
        tag.putInt("APY", entity.getLocation().getBlockY());
        tag.putInt("APZ", entity.getLocation().getBlockZ());
        tag.putByte("Color", entity.getColor().getWoolData());
    }
}
