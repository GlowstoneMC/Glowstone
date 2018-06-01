package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.state.GlowDropper;

public class DropperEntity extends DispenserEntity {

    public DropperEntity(GlowBlock block) {
        super(block);
    }

    @Override
    protected void setOwnSaveId() {
        setSaveId("minecraft:dropper");
    }

    @Override
    public GlowBlockState getState() {
        return new GlowDropper(block);
    }
}
