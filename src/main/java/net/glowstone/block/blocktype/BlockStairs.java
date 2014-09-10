package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Stairs;
import org.bukkit.util.Vector;

public class BlockStairs extends BlockType {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData data = state.getData();
        if (data instanceof Stairs) {
            ((Stairs) data).setFacingDirection(player.getDirection());

            if (face == BlockFace.DOWN || face != BlockFace.UP && clickedLoc.getY() >= 8) {
                ((Stairs) data).setInverted(true);
            }

            state.setData(data);
        } else {
            warnMaterialData(Stairs.class, data);
        }
    }
}
