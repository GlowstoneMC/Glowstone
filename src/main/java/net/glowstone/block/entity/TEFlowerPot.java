package net.glowstone.block.entity;

import org.bukkit.material.MaterialData;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowFlowerPot;
import net.glowstone.constants.GlowBlockEntity;
import net.glowstone.constants.ItemIds;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.nbt.CompoundTag;

public class TEFlowerPot extends TileEntity {

    private MaterialData contents;

    public TEFlowerPot(GlowBlock block) {
        super(block);
        setSaveId("FlowerPot");
    }

    public MaterialData getContents() {
        return contents;
    }

    public void setContents(MaterialData contents) {
        this.contents = contents;
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);

        if (tag.isString("Item") && !tag.getString("Item").isEmpty() && tag.isByte("Data")) {
            contents = ItemIds.getMaterial(tag.getString("Item")).getNewData(tag.getByte("Data"));
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);

        if (contents != null) {
            tag.putString("Item", ItemIds.getName(contents.getItemType()));
            tag.putByte("Data", contents.getData());
        } else {
            // Mimics how Minecraft does it.
            tag.putString("Item", "");
            tag.putByte("Data", 0);
        }
    }

    @Override
    public GlowBlockState getState() {
        return new GlowFlowerPot(block);
    }

    @Override
    public void update(GlowPlayer player) {
        super.update(player);

        CompoundTag nbt = new CompoundTag();

        saveNbt(nbt);
        player.sendBlockEntityChange(getBlock().getLocation(), GlowBlockEntity.FLOWER_POT, nbt);
    }
}
