package net.glowstone.io.entity;

import net.glowstone.entity.monster.complex.GlowEnderDragon;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;

public class EnderDragonStore extends EntityStore<GlowEnderDragon> {

    public EnderDragonStore() {
        super(GlowEnderDragon.class, EntityType.ENDER_DRAGON);
    }

    @Override
    public void load(GlowEnderDragon entity, CompoundTag tag) {
        super.load(entity, tag);

        if (!tag.readInt("DragonPhase", phase -> entity.setPhase(EnderDragon.Phase.values()[phase])
        )) {
            entity.setPhase(EnderDragon.Phase.HOVER);
        }
    }

    @Override
    public void save(GlowEnderDragon entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putInt("DragonPhase", entity.getPhase().ordinal());
    }

    @Override
    public GlowEnderDragon createEntity(Location location, CompoundTag compound) {
        try {
            return GlowEnderDragon.class.getConstructor(Location.class).newInstance(location);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UnsupportedOperationException("Not implemented yet.");
        }
    }
}
