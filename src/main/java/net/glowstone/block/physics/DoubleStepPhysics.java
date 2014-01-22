package net.glowstone.block.physics;

import net.glowstone.block.GlowBlockState;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;

public class DoubleStepPhysics extends DefaultBlockPhysics {

    @Override
    public GlowBlockState placeAgainst(GlowBlockState block, int type, short data, BlockFace against) {
        if (against == BlockFace.UP && type == Material.STEP.getId()) {
            GlowBlockState possibleStair = block.getBlock().getRelative(against.getOppositeFace()).getState();
            if (possibleStair.getType() == Material.STEP && possibleStair.getRawData() == data) {
                possibleStair.setType(Material.DOUBLE_STEP);
                possibleStair.setData(new MaterialData(Material.DOUBLE_STEP, (byte) data));
                return possibleStair;
            }
        }
        return super.placeAgainst(block, type, data, against);
    }
}