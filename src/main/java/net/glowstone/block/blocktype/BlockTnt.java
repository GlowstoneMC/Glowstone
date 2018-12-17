package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.GlowTntPrimed;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class BlockTnt extends BlockType {

    /**
     * Convert a TNT block into a primed TNT entity with the player who ignited the TNT.
     *
     * @param tntBlock The block to ignite.
     * @param ignitedByExplosion True if another explosion caused this ignition.
     * @param player The player who ignited the TNT.
     */
    public static void igniteBlock(
        Block tntBlock, boolean ignitedByExplosion, GlowPlayer player) {
        tntBlock.setType(Material.AIR);
        World world = tntBlock.getWorld();
        GlowTntPrimed tnt = (GlowTntPrimed) world
            .spawnEntity(tntBlock.getLocation().add(0.5, 0, 0.5), EntityType.PRIMED_TNT);
        tnt.setSource(player);
        tnt.setIgnitedByExplosion(ignitedByExplosion);
        world.playSound(tntBlock.getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 1);
    }

    /**
     * Convert a TNT block into a primed TNT entity.
     *
     * @param tntBlock The block to ignite.
     * @param ignitedByExplosion True if another explosion caused this ignition.
     */
    public static void igniteBlock(Block tntBlock, boolean ignitedByExplosion) {
        igniteBlock(tntBlock, ignitedByExplosion, null);
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding,
        GlowBlockState oldState) {
        updatePhysics(block);
    }

    @Override
    public void onNearBlockChanged(GlowBlock block, BlockFace face, GlowBlock changedBlock,
        Material oldType, byte oldData, Material newType, byte newData) {
        updatePhysics(block);
    }

    @Override
    public void updatePhysics(GlowBlock me) {
        super.updatePhysics(me);
        if (me.isBlockIndirectlyPowered()) {
            igniteBlock(me, false, null);
        }
    }

}
