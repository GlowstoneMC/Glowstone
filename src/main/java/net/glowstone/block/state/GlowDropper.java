package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import org.bukkit.block.Dropper;

public class GlowDropper extends GlowDispenser implements Dropper {

    public GlowDropper(GlowBlock block) {
        super(block);
    }

    @Override
    public void drop() {
        dispense();
    }

    @Override
    public boolean dispense() {
        // todo: drop, not dispense
        return super.dispense();
    }
}
