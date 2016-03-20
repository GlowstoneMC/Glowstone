package net.glowstone.io.entity;

import net.glowstone.entity.objects.GlowFallingBlock;
import net.glowstone.util.nbt.CompoundTag;

import org.bukkit.Location;
import org.bukkit.Material;

class FallingBlockStore extends EntityStore<GlowFallingBlock> {

    public FallingBlockStore() {
        super(GlowFallingBlock.class, "FallingSand");
    }


    @Override
    public GlowFallingBlock createEntity(Location location, CompoundTag compound) {
        // Falling block will be set by loading code below
        return new GlowFallingBlock(location, null, (byte) 0);
    }

    @Override
    public void load(GlowFallingBlock entity, CompoundTag tag) {
        super.load(entity, tag);

        if (tag.isString("Material")) {
            entity.setMaterial(Material.getMaterial(tag.getString("Material")));
        }
        /*if (tag.isString("Velocity")) {
            entity.setVelocity(Vector.deserialize());
        }*/
    }

    @Override
    public void save(GlowFallingBlock entity, CompoundTag tag) {
        super.save(entity, tag);

        tag.putString("Material", entity.getMaterial().toString());
        //tag.putString("Velocity", entity.getVelocity().serialize());
    }
}
