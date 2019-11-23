package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.ToolType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class BlockMagma extends BlockDirectDrops {
    public BlockMagma() {
        super(Material.MAGMA_BLOCK, ToolType.PICKAXE);
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState blockState, BlockFace face,
        ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, blockState, face, holding, clickedLoc);
        bubbleUp(blockState.getBlock().getRelative(BlockFace.UP));
    }

    private void bubbleUp(GlowBlock target) {
        // trigger bubble chain
        if (target.getType() == Material.WATER) {
            target.setType(Material.BUBBLE_COLUMN);
        }
        if (target.getType() == Material.BUBBLE_COLUMN) {
            Bukkit.getLogger().info("create whirlpool at " + target.getLocation());
            // todo: set 'drag' blockdata to true
        }
    }

    @Override
    public void onEntityStep(GlowBlock block, LivingEntity entity) {
        entity.damage(1.0, EntityDamageEvent.DamageCause.FIRE);
    }
}
