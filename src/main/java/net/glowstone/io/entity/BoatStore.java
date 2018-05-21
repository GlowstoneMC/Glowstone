package net.glowstone.io.entity;

import java.util.Arrays;
import java.util.Optional;
import net.glowstone.entity.objects.GlowBoat;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.EntityType;

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
        return species == TreeSpecies.REDWOOD ? "spruce" : species.name().toLowerCase();
    }

    private TreeSpecies toTreeSpecies(String species) {
        if (species.equalsIgnoreCase("spruce")) {
            return TreeSpecies.REDWOOD;
        }
        Optional<TreeSpecies> any = Arrays.stream(TreeSpecies.values())
            .filter(t -> t.name().equalsIgnoreCase(species)).findAny();
        return any.orElse(TreeSpecies.GENERIC);
    }
}
