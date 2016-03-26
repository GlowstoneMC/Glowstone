package net.glowstone.io.entity;

import net.glowstone.constants.ItemIds;
import net.glowstone.entity.objects.GlowFallingBlock;
import net.glowstone.util.nbt.CompoundTag;

import org.bukkit.Location;

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

        if (tag.isString("Block")) {
            entity.setMaterial(ItemIds.getBlock(tag.getString("Block")));
        }
        if (tag.isByte("Data")) {
            entity.setBlockData(tag.getByte("Data"));
        }
        entity.setHurtEntities(tag.getBool("HurtEntities"));
        entity.setDropItem(tag.getBool("DropItem"));
        if (tag.isCompound("TileEntityData")) {
            entity.setTileEntityCompoundTag(tag.getCompound("TileEntityData"));
        }
    }

    @Override
    public void save(GlowFallingBlock entity, CompoundTag tag) {
        super.save(entity, tag);

        tag.putString("Block", ItemIds.getName(entity.getMaterial()));
        tag.putByte("Data", entity.getBlockData());
        tag.putBool("DropItem", entity.getDropItem());
        tag.putBool("HurtEntities", entity.canHurtEntities());
        if (entity.getTileEntityCompoundTag() != null) {
            tag.putCompound("TileEntityData", entity.getTileEntityCompoundTag());
        }
    }
}
