package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowVillager;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager.Profession;

class VillagerStore extends AgeableStore<GlowVillager> {

    public VillagerStore() {
        super(GlowVillager.class, EntityType.VILLAGER);
    }

    @Override
    public void load(GlowVillager entity, CompoundTag compound) {
        super.load(entity, compound);
        if (compound.isInt("Profession")) {
            entity.setProfession(Profession.values()[compound.getInt("Profession")]);
        } else {
            entity.setProfession(Profession.FARMER);
        }

        //TODO: remaining data
    }

    @Override
    public void save(GlowVillager entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("Profession", entity.getProfession().ordinal());
        //TODO: remaining data
    }

}
