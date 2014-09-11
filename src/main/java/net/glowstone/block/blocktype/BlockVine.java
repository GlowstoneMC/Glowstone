package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Vine;
import org.bukkit.util.Vector;

public class BlockVine extends BlockClimbable {

    @Override
    public boolean canPlaceAt(GlowBlock block, BlockFace against) {
        return super.canPlaceAt(block, against) || 
                against == BlockFace.UP && isTargetOccluding(block, BlockFace.UP);
    }
    
    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        MaterialData data = state.getData();
        if (data instanceof Vine) {
            if (face == BlockFace.DOWN || face == BlockFace.UP) {
                return;
            } else {
                ((Vine) data).putOnFace(face.getOppositeFace());
            }
            state.setData(data);
        } else {
            warnMaterialData(Vine.class, data);
        }
    }

    @Override
    public boolean canAbsorb(GlowBlock block, BlockFace face, ItemStack holding) {
        return true;
    }

    @Override
    public boolean canOverride(GlowBlock block, BlockFace face, ItemStack holding) {
        return true;
    }
}
