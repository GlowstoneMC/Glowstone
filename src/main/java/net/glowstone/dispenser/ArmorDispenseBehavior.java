package net.glowstone.dispenser;

import java.util.ArrayList;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.blocktype.BlockDispenser;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;
import java.util.List;
import org.bukkit.entity.EntityType;

public class ArmorDispenseBehavior extends DefaultDispenseBehavior {
    DefaultDispenseBehavior defaultBehavior = new DefaultDispenseBehavior();
    
    @Override
    protected ItemStack dispenseStack(GlowBlock block, ItemStack stack) {
        Material armor = stack.getType();
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
            case ELYTRA: armor = Material.DIAMOND_CHESTPLATE; break;
            case SKULL_ITEM: armor = Material.DIAMOND_HELMET; break;
            case PUMPKIN: armor = Material.DIAMOND_HELMET; break;
        }
        
        //Find all nearby entities and see if they are players or armor stands
        List<LivingEntity> entities = new ArrayList<>();
        for (Entity cEntity : world.getNearbyEntities(location1, 3, 3, 3)) {
            switch (cEntity.getType()) {
                case PLAYER: entities.add((LivingEntity) cEntity); break;
                //case ARMOR_STAND: entities.add((LivingEntity) cEntity); break;
            }
        }  
        
        Location location;
        Boolean location1Test;
        Boolean location2Test;
        //Loop through entities to see if any are in the location where armor would be dispensed
        for (LivingEntity player : entities) {          
            location = player.getLocation().clone();
            location.setX(location.getBlockX());
            location.setY(location.getBlockY());
            location.setZ(location.getBlockZ());            
            location1Test = (location.getX() == location1.getX() && location.getY() == location1.getY() && location.getZ() == location1.getZ());
            location2Test = (location.getX() == location2.getX() && location.getY() == location2.getY() && location.getZ() == location2.getZ());
            if ((location1Test || location2Test)) {
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
                        if (stack.getType() == Material.ELYTRA && player.getType() != EntityType.PLAYER) return defaultBehavior.dispense(block, stack);
                        player.getEquipment().setChestplate(stack.clone());
                        stack.setType(Material.AIR);
                        return stack;
                    }
                }
                if (armor == Material.DIAMOND_HELMET) {
                    if (player.getEquipment().getHelmet().getType() == Material.AIR) {
                        player.getEquipment().setHelmet(stack.clone());
                        stack.setAmount(stack.getAmount() - 1);
                        if (stack.getAmount() == 0) stack.setType(Material.AIR);
                        return stack;
                    }
                }
                if (armor == Material.SHIELD && player.getType() == EntityType.PLAYER) {
                    if (player.getEquipment().getItemInOffHand().getType() == Material.AIR) {
                        player.getEquipment().setItemInOffHand(stack.clone());
                        stack.setType(Material.AIR);
                        return stack;
                    }
                }
            }
        }
        return defaultBehavior.dispense(block, stack); //Fallback
    }
}
