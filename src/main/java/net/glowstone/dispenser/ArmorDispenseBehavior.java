/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.glowstone.dispenser;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.blocktype.BlockDispenser;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.List;

/**
 *
 * @author cag45
 */
public class ArmorDispenseBehavior extends DefaultDispenseBehavior {
    DefaultDispenseBehavior defaultBehavior = new DefaultDispenseBehavior();
    
    @Override
    protected ItemStack dispenseStack(GlowBlock block, ItemStack stack) {
        Material armor = stack.clone().getType();
        BlockFace facing = BlockDispenser.getFacing(block);
        GlowBlock target = block.getRelative(facing);
        Location location1 = target.getLocation();
        Location location2 = location1.clone();
        location2.setY(location2.getY() - 1);
        World world = location1.getWorld();
        
        switch (armor) { //Get the type of armor in stack
            case LEATHER_HELMET: armor = Material.DIAMOND_HELMET; break;
            case GOLD_HELMET: armor = Material.DIAMOND_HELMET; break;
            case IRON_HELMET: armor = Material.DIAMOND_HELMET; break;
            case CHAINMAIL_HELMET: armor = Material.DIAMOND_HELMET; break;
            case LEATHER_BOOTS: armor = Material.DIAMOND_BOOTS; break;
            case GOLD_BOOTS: armor = Material.DIAMOND_BOOTS; break;
            case IRON_BOOTS: armor = Material.DIAMOND_BOOTS; break;
            case CHAINMAIL_BOOTS: armor = Material.DIAMOND_BOOTS; break;
            case LEATHER_LEGGINGS: armor = Material.DIAMOND_LEGGINGS; break;
            case GOLD_LEGGINGS: armor = Material.DIAMOND_LEGGINGS; break;
            case IRON_LEGGINGS: armor = Material.DIAMOND_LEGGINGS; break;
            case CHAINMAIL_LEGGINGS: armor = Material.DIAMOND_LEGGINGS; break;
            case LEATHER_CHESTPLATE: armor = Material.DIAMOND_CHESTPLATE; break;
            case GOLD_CHESTPLATE: armor = Material.DIAMOND_CHESTPLATE; break;
            case IRON_CHESTPLATE: armor = Material.DIAMOND_CHESTPLATE; break;
            case CHAINMAIL_CHESTPLATE: armor = Material.DIAMOND_CHESTPLATE; break;
        }
        
        List<Player> players = world.getPlayers();
        Player player;
        Location location;
        Boolean location1Test;
        Boolean location2Test;
        int i = 0;
        //Loop through players to see if any are in the location where armor would be dispensed
        while (i < players.size()) {
            player = players.get(i);            
            location = player.getLocation().clone();
            location.setX(location.getBlockX());
            location.setY(location.getBlockY());
            location.setZ(location.getBlockZ());            
            location1Test = (location.getX() == location1.getX() && location.getY() == location1.getY() && location.getZ() == location1.getZ());
            location2Test = (location.getX() == location2.getX() && location.getY() == location2.getY() && location.getZ() == location2.getZ());
            if (location1Test || location2Test) {
                //Check if the aproperiate armor slot is empty, if so equip armor.
                if (armor == Material.DIAMOND_BOOTS) {
                    if (player.getEquipment().getBoots().getType() == Material.AIR) {
                        player.getEquipment().setBoots(stack.clone());
                        stack.setType(Material.AIR);
                        return stack;
                    }
                }
                if (armor == Material.DIAMOND_LEGGINGS) {
                    if (player.getEquipment().getLeggings().getType() == Material.AIR) {
                        player.getEquipment().setLeggings(stack.clone());
                        stack.setType(Material.AIR);
                        return stack;
                    }
                }
                if (armor == Material.DIAMOND_CHESTPLATE) {
                    if (player.getEquipment().getChestplate().getType() == Material.AIR) {
                        player.getEquipment().setChestplate(stack.clone());
                        stack.setType(Material.AIR);
                        return stack;
                    }
                }
                if (armor == Material.DIAMOND_HELMET) {
                    if (player.getEquipment().getHelmet().getType() == Material.AIR) {
                        player.getEquipment().setHelmet(stack.clone());
                        stack.setType(Material.AIR);
                        return stack;
                    }
                }
            }
            i += 1; //Continue loop
        }
        return defaultBehavior.dispense(block, stack); //Fallback
    }
}
