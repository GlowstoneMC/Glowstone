package net.glowstone.io.entity;

import net.glowstone.entity.objects.GlowEnderCrystal;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class EnderCrystalStore extends EntityStore<GlowEnderCrystal> {

    public EnderCrystalStore() {
        super(GlowEnderCrystal.class, EntityType.ENDER_CRYSTAL);
    }

    @Override
    public GlowEnderCrystal createEntity(Location location, CompoundTag compound) {
        return new GlowEnderCrystal(location);
    }

    @Override
    public void load(GlowEnderCrystal entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.readBoolean("ShowBottom", entity::setShowingBottom);
        CompoundTag beamTarget = tag.tryGetCompound("BeamTarget");
        if (beamTarget != null) {
            int x = beamTarget.getInt("X");
            int y = beamTarget.getInt("Y");
            int z = beamTarget.getInt("Z");
            entity.setBeamTarget(new Location(entity.getWorld(), x, y, z));
        }
    }

    @Override
    public void save(GlowEnderCrystal entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putBool("ShowBottom", entity.isShowingBottom());

        if (entity.getBeamTarget() != null) {
            CompoundTag beamTarget = new CompoundTag();
            beamTarget.putInt("X", entity.getBeamTarget().getBlockX());
            beamTarget.putInt("Y", entity.getBeamTarget().getBlockY());
            beamTarget.putInt("Z", entity.getBeamTarget().getBlockZ());
            tag.putCompound("BeamTarget", beamTarget);
        }
    }
}
