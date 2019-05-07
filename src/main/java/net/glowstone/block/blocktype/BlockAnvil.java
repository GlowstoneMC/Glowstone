package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowAnvilInventory;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockAnvil extends BlockFalling {

    public BlockAnvil() {
        super(Material.ANVIL);
    }

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
        Vector clickedLoc) {
        return player.openInventory(new GlowAnvilInventory(player)) != null;
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        // This is replicated from BlockNeedsTool and has been copy/pasted because classes cannot
        // extend 2 parents
        ToolType neededTool = ToolType.PICKAXE;
        if (tool == null || !neededTool.matches(tool.getType())) {
            return Collections.emptyList();
        }

        return getMinedDrops(block);
    }

    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        ItemStack drop = new ItemStack(Material.ANVIL, 1, (short) (block.getData() / 4));
        return Arrays.asList(drop);
    }
}
