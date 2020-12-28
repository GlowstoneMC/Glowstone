package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.MaterialMatcher;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

public class BlockQuartz extends BlockNeedsTool {

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        if (holding.getDurability() > 1) {
            switch (face) {
                case NORTH:
                case SOUTH:
                    state.setRawData((byte) 4);
                    break;
                case WEST:
                case EAST:
                    state.setRawData((byte) 3);
                    break;
                case UP:
                case DOWN:
                    state.setRawData((byte) 2);
                    break;
                default:
                    // do nothing
                    // TODO: should this raise a warning?
            }
        }
    }

    @NotNull
    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        //TODO use MaterialData instead of magic value if possible
        byte data = (byte) Math.min(2, block.getData());
        return Arrays.asList(new ItemStack(Material.QUARTZ_BLOCK, 1, data));
    }

    @Override
    public MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return ToolType.PICKAXE;
    }
}
