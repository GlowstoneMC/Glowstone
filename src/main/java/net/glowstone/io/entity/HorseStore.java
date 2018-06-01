package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowHorse;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;

public class HorseStore extends AbstractHorseStore<GlowHorse> {

    public HorseStore() {
        super(GlowHorse.class, EntityType.HORSE, GlowHorse::new);
    }

    @Override
    public void load(GlowHorse entity, CompoundTag compound) {
        super.load(entity, compound);
        compound.readBoolean("EatingHaystack", entity::setEatingHay);
        compound.readInt("Variant", variant -> {
            entity.setStyle(Horse.Style.values()[variant >>> 8]);
            entity.setColor(Horse.Color.values()[variant & 0xFF]);
        });
        compound.readInt("Temper", entity::setTemper);
        compound.readItem("ArmorItem", entity.getInventory()::setArmor);
        compound.readItem("SaddleItem", entity.getInventory()::setSaddle);
    }

    @Override
    public void save(GlowHorse entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("EatingHaystack", entity.isEatingHay());
        tag.putInt("Variant",
            entity.getStyle().ordinal() << 8 | entity.getColor().ordinal() & 0xFF);
        tag.putInt("Temper", entity.getTemper());
        if (entity.getInventory().getArmor() != null) {
            tag.putCompound("ArmorItem",
                NbtSerialization.writeItem(entity.getInventory().getArmor(), -1));
        }
        if (entity.getInventory().getSaddle() != null) {
            tag.putCompound("SaddleItem",
                NbtSerialization.writeItem(entity.getInventory().getSaddle(), -1));
        }
    }
}
