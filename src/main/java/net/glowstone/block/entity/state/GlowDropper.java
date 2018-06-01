package net.glowstone.block.entity.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.dispenser.DefaultDispenseBehavior;
import net.glowstone.dispenser.DispenseBehavior;
import org.bukkit.Material;
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
    protected DispenseBehavior getDispenseBehavior(Material itemType) {
        return DefaultDispenseBehavior.INSTANCE;
    }
}
