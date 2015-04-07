package net.glowstone.block.state;

import org.bukkit.block.FlowerPot;
import org.bukkit.material.MaterialData;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEFlowerPot;
import net.glowstone.block.entity.TileEntity;

public class GlowFlowerPot extends GlowBlockState implements FlowerPot {

    private MaterialData contents;

    public GlowFlowerPot(GlowBlock block) {
        super(block);
        // Pre-1.7 uses block data and post-1.7 uses NBT data for flower pot contents.
        if (getTileEntity() != null) {
            contents = ((TEFlowerPot) getTileEntity()).getContents();
        } else if (hasFlowerPotData()) {
            contents = ((org.bukkit.material.FlowerPot) getData()).getContents();
        }
    }

    private TileEntity getTileEntity() {
        return getBlock().getTileEntity();
    }

    private boolean hasFlowerPotData() {
        return getData() instanceof org.bukkit.material.FlowerPot;
    }

    @Override
    public MaterialData getContents() {
        return contents;
    }

    @Override
    public void setContents(MaterialData contents) {
        this.contents = contents;
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        // Pre-1.7 uses block data.
        if (getTileEntity() == null && hasFlowerPotData()) {
            ((org.bukkit.material.FlowerPot) getData()).setContents(contents);
        }

        boolean result = super.update(force, applyPhysics);
        // Post-1.7 uses NBT data.
        if (result && getTileEntity() != null) {
            TEFlowerPot pot = ((TEFlowerPot) getTileEntity());

            pot.setContents(contents);
            pot.updateInRange();
        }
        return result;
    }
}
