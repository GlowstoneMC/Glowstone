package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.BlockEntity;
import net.glowstone.block.entity.FurnaceEntity;
import net.glowstone.chunk.GlowChunk;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.MaterialMatcher;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Furnace;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

public class BlockFurnace extends BlockContainer {

    public BlockFurnace() {
        setDrops(new ItemStack(Material.FURNACE));
    }

    @Override
    public BlockEntity createBlockEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new FurnaceEntity(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        MaterialData data = state.getData();
        if (data instanceof Furnace) {
            ((Furnace) data).setFacingDirection(getOppositeBlockFace(player.getLocation(), false));
            state.setData(data);
        } else {
            warnMaterialData(Furnace.class, data);
        }
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return ToolType.PICKAXE;
    }

    @Override
    public void receivePulse(GlowBlock block) {
        ((FurnaceEntity) block.getBlockEntity()).burn();
    }

    @Override
    public int getPulseTickSpeed(GlowBlock block) {
        return 1;
    }

    @Override
    public boolean isPulseOnce(GlowBlock block) {
        return false;
    }
}
