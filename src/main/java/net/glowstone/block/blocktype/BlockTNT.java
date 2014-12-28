package net.glowstone.block.blocktype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowTNTPrimed;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

public class BlockTNT extends BlockType {

    /**
     * Convert a TNT block into a primed TNT entity.
     * @param tntBlock The block to ignite.
     * @param ignitedByExplosion True if another explosion caused this ignition.
     */
    public static void igniteBlock(GlowBlock tntBlock, boolean ignitedByExplosion) {
        tntBlock.setType(Material.AIR);
        World world = tntBlock.getWorld();
        GlowTNTPrimed tnt = (GlowTNTPrimed) world.spawnEntity(tntBlock.getLocation().add(0.5, 0, 0.5), EntityType.PRIMED_TNT);
        tnt.setIgnitedByExplosion(ignitedByExplosion);
        world.playSound(tntBlock.getLocation(), Sound.FUSE, 1, 1);
    }

}
