package net.glowstone.block.blocktype;

import java.util.Collection;
import java.util.Collections;
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

public class BlockPurpurPillar extends BlockNeedsTool {

    private static final byte AXIS_X = 0x4;
    private static final byte AXIS_Y = 0x0;
    private static final byte AXIS_Z = 0x8;

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);

        byte data = 0;
        switch (face) {
            case UP:
            case DOWN:
                data = AXIS_Y;
                break;
            case NORTH:
            case SOUTH:
                data = AXIS_Z;
                break;
            case EAST:
            case WEST:
                data = AXIS_X;
                break;
            default:
                // do nothing
                // TODO: should this raise a warning?
        }
        state.setRawData(data);
    }

    @NotNull
    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        return Collections.singletonList(new ItemStack(Material.PURPUR_PILLAR));
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return ToolType.PICKAXE;
    }
}
