package net.glowstone.io.entity;

import net.glowstone.entity.objects.GlowFallingBlock;
import net.glowstone.io.nbt.NbtSerialization;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;

class FallingBlockStore extends EntityStore<GlowFallingBlock> {

    public FallingBlockStore() {
        super(GlowFallingBlock.class, EntityType.FALLING_BLOCK);
    }


    @Override
    public GlowFallingBlock createEntity(Location location, CompoundTag compound) {
        // Falling block will be set by loading code below
        // TODO: 1.13 block entity
        return new GlowFallingBlock(location, null, null);
    }

    @Override
    public void load(GlowFallingBlock entity, CompoundTag tag) {
        super.load(entity, tag);
        BlockData data = NbtSerialization.readBlockData(tag.getCompound("BlockState"));
        entity.setBlockData(data);
        tag.readBoolean("HurtEntities", entity::setHurtEntities);
        tag.readBoolean("DropItem", entity::setDropItem);
        tag.readCompound("TileEntityData", entity::setBlockEntityCompoundTag);
    }

    @Override
    public void save(GlowFallingBlock entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putCompound("BlockState", NbtSerialization.writeBlockData(entity.getBlockData()));
        tag.putBool("DropItem", entity.getDropItem());
        tag.putBool("HurtEntities", entity.canHurtEntities());
        if (entity.getBlockEntityCompoundTag() != null) {
            tag.putCompound("TileEntityData", entity.getBlockEntityCompoundTag());
        }
    }
}
