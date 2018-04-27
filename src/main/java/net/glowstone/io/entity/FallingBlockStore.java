package net.glowstone.io.entity;

import net.glowstone.constants.ItemIds;
import net.glowstone.entity.objects.GlowFallingBlock;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

class FallingBlockStore extends EntityStore<GlowFallingBlock> {

    public FallingBlockStore() {
        super(GlowFallingBlock.class, EntityType.FALLING_BLOCK);
    }


    @Override
    public GlowFallingBlock createEntity(Location location, CompoundTag compound) {
        // Falling block will be set by loading code below
        return new GlowFallingBlock(location, null, (byte) 0);
    }

    @Override
    public void load(GlowFallingBlock entity, CompoundTag tag) {
        super.load(entity, tag);
        tag.consumeString(name -> entity.setMaterial(ItemIds.getBlock(name)), "Block");
        tag.consumeByte(entity::setBlockData, "Data");
        tag.consumeBoolean(entity::setHurtEntities, "HurtEntities");
        tag.consumeBoolean(entity::setDropItem, "DropItem");
        tag.consumeCompound(entity::setBlockEntityCompoundTag, "TileEntityData");
    }

    @Override
    public void save(GlowFallingBlock entity, CompoundTag tag) {
        super.save(entity, tag);
        tag.putString("Block", ItemIds.getName(entity.getMaterial()));
        tag.putByte("Data", entity.getBlockData());
        tag.putBool("DropItem", entity.getDropItem());
        tag.putBool("HurtEntities", entity.canHurtEntities());
        if (entity.getBlockEntityCompoundTag() != null) {
            tag.putCompound("TileEntityData", entity.getBlockEntityCompoundTag());
        }
    }
}
