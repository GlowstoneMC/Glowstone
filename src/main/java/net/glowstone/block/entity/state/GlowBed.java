package net.glowstone.block.entity.state;

import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.BedEntity;
import org.bukkit.DyeColor;
import org.bukkit.block.Bed;

public class GlowBed extends GlowBlockState implements Bed {

    @Getter
    @Setter
    private DyeColor color;

    public GlowBed(GlowBlock block) {
        super(block);
        color = DyeColor.getByWoolData((byte) getBlockEntity().getColor());
    }

    public BedEntity getBlockEntity() {
        return (BedEntity) getBlock().getBlockEntity();
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            BedEntity bed = getBlockEntity();
            bed.setColor(color.getWoolData());
            getBlockEntity().updateInRange();
        }
        return result;
    }
}
