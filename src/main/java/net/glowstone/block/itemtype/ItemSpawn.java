package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.entity.monster.*;
import net.glowstone.entity.passive.*;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemSpawn extends ItemType {
    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock against, BlockFace face, ItemStack holding, Vector clickedLoc) {
        GlowBlock target = against.getRelative(face);
        GlowEntity entity = null;

        switch (holding.getDurability()) {
            case 50:
                entity = new GlowCreeper(target.getLocation());
                break;
            case 51:
                entity = new GlowSkeleton(target.getLocation());
                break;
            case 52:
                entity = new GlowSpider(target.getLocation());
                break;
            //TODO: 53 Giant
            case 54:
                entity = new GlowZombie(target.getLocation());
                break;
            //TODO: 55 Slime
            case 56:
                entity = new GlowGhast(target.getLocation());
                break;
            //TODO: 57 Pig Zombie
            case 58:
                entity = new GlowEnderman(target.getLocation());
                break;
            //TODO: 59 Cave Spider
            case 60:
                entity = new GlowSilverfish(target.getLocation());
                break;
            case 61:
                entity = new GlowBlaze(target.getLocation());
                break;
            //TODO: 62 Magma Cube
            case 66:
                entity = new GlowWitch(target.getLocation());
                break;
            //TODO: 67 Endermite
            //TODO: 68 Guardian


            case 65:
                entity = new GlowBat(target.getLocation());
                break;
            case 90:
                entity = new GlowPig(target.getLocation());
                break;
            case 91:
                entity = new GlowSheep(target.getLocation());
                break;
            //TODO: 92 Cow
            case 93:
                entity = new GlowChicken(target.getLocation());
                break;
            //TODO: 94 Squid
            //TODO: 95 Wolf
            //TODO: 96 Mooshroom
            //TODO 98 Ocelot
            case 100:
                entity = new GlowHorse(target.getLocation());
                break;
            case 101:
                entity = new GlowRabbit(target.getLocation());
                break;
            //TODO: 120 Villager
        }

        if (entity == null) entity = new GlowPig(target.getLocation());

        entity.createSpawnMessage();

    }
}
