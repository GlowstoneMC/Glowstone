package net.glowstone.entity.passive;

import net.glowstone.entity.GlowAgeable;
import net.glowstone.entity.meta.MetadataIndex;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import java.util.Random;

public class GlowVillager extends GlowAgeable implements Villager {

    private Profession profession;

    public GlowVillager(Location location) {
        super(location, EntityType.VILLAGER, 20);
        Random r = new Random();
        setProfession(Profession.getProfession(r.nextInt(Profession.values().length)));
    }

    @Override
    public Profession getProfession() {
        return profession;
    }

    @Override
    public void setProfession(Profession profession) {
        this.profession = profession;
        metadata.set(MetadataIndex.VILLAGER_TYPE, profession.getId());
    }
}
