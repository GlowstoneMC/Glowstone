package net.glowstone.block.entity;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowBed;
import net.glowstone.constants.GlowBlockEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.nbt.CompoundTag;

public class BedEntity extends BlockEntity {
    @Getter
    @Setter
    private int color;

    public BedEntity(GlowBlock block) {
        super(block);
        setSaveId("minecraft:bed");
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        color = tag.getInt("color");
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putInt("color", color);
    }

    @Override
    public GlowBlockState getState() {
        return new GlowBed(block);
    }

    @Override
    public void update(GlowPlayer player) {
        super.update(player);
        CompoundTag nbt = new CompoundTag();
        saveNbt(nbt);
        player.sendBlockEntityChange(block.getLocation(), GlowBlockEntity.BED, nbt);
    }
}
