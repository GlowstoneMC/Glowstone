package net.glowstone.io.entity;

import net.glowstone.entity.passive.GlowVillager;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.entity.Villager;

class VillagerStore extends AgeableStore<GlowVillager> {

    public VillagerStore() {
        super(GlowVillager.class, "Villager");
    }

    @Override
    public void load(GlowVillager entity, CompoundTag compound) {
        super.load(entity, compound);
        entity.setProfession(Villager.Profession.getProfession(compound.getInt("Profession")));
        //TODO: remaining data
    }

    @Override
    public void save(GlowVillager entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("Profession", entity.getProfession().getId());
        //TODO: remaining data
    }

}
