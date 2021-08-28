package net.glowstone.io.entity;

import net.glowstone.entity.objects.GlowBoat;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Optional;

public class BoatStore extends EntityStore<GlowBoat> {

    public BoatStore() {
        super(GlowBoat.class, EntityType.BOAT);
    }

    @Override
    public GlowBoat createEntity(Location location, CompoundTag compound) {
        return new GlowBoat(location);
    }

    @Override
    public void load(GlowBoat entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readString("Type", type -> entity.setWoodType(toTreeSpecies(type)));
    }

    @Override
    public void save(GlowBoat entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putString("Type", toString(entity.getWoodType()));
    }

    private String toString(TreeSpecies species) {
        return species == TreeSpecies.REDWOOD ? "spruce" : species.name().toLowerCase(); // NON-NLS
    }

    private TreeSpecies toTreeSpecies(String species) {
        if (species.equalsIgnoreCase("spruce")) { // NON-NLS
            return TreeSpecies.REDWOOD;
        }
        Optional<TreeSpecies> any = Arrays.stream(TreeSpecies.values())
            .filter(t -> t.name().equalsIgnoreCase(species)).findAny();
        return any.orElse(TreeSpecies.GENERIC);
    }
}
