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
import org.bukkit.entity.Player;
import net.glowstone.inventory.GlowInventory;

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
                return ((GlowInventory) ((Player) player).getInventory()).tryToFillSlots(stack, 36, 40);
            }
        }
        return defaultBehavior.dispense(block, stack); //Fallback
    }
}
