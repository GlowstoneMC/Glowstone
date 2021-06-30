package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class BlockDaylightDetector extends BlockType {

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
                                 Vector clickedLoc) {
        // TODO: use inverted block state
        if (block.getType() == Material.DAYLIGHT_DETECTOR) {
            //block.setType(Material.DAYLIGHT_DETECTOR_INVERTED);
        } else {
            block.setType(Material.DAYLIGHT_DETECTOR);
        }
        return true;
    }
}
