package net.glowstone.io.entity;

import net.glowstone.entity.monster.GlowEnderman;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;

class EndermanStore extends MonsterStore<GlowEnderman> {

    public EndermanStore() {
        super(GlowEnderman.class, EntityType.ENDERMAN, GlowEnderman::new);
    }

    @Override
    public void load(GlowEnderman entity, CompoundTag compound) {
        super.load(entity, compound);
        // Load carried block
        compound.tryGetBlockData("carriedBlockState")
                .ifPresent(entity::setCarriedBlock);
    }

    @Override
    public void save(GlowEnderman entity, CompoundTag tag) {
        super.save(entity, tag);
        BlockData carried = entity.getCarriedBlock();
        // Save the carried block, if there is one.
        if (carried != null && carried.getMaterial() != Material.AIR) {
            tag.putCompound("carriedBlockState", NbtSerialization.writeBlockData(carried));
        }
    }
}
