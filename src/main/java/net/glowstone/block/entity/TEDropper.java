package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.state.GlowDropper;

public class TEDropper extends TEDispenser {

    public TEDropper(GlowBlock block) {
        super(block);
    }

    @Override
    protected void setOwnSaveId() {
        setSaveId("dropper");
    }

    @Override
    public GlowBlockState getState() {
        return new GlowDropper(block);
    }
}
