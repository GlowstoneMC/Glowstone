package net.glowstone.dispenser;

import java.util.ArrayList;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.blocktype.BlockDispenser;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Entity;
import java.util.List;
import org.bukkit.entity.Player;
import net.glowstone.inventory.GlowInventory;

public class ArmorDispenseBehavior extends DefaultDispenseBehavior {
    DefaultDispenseBehavior defaultBehavior = new DefaultDispenseBehavior();
    
    @Override
    protected ItemStack dispenseStack(GlowBlock block, ItemStack stack) {
        BlockFace facing = BlockDispenser.getFacing(block);
        GlowBlock target = block.getRelative(facing);
        Location targetLocation = target.getLocation();
        
        // Find all nearby entities and see if they are players or armor stands
        List<LivingEntity> entities = new ArrayList<>();
        for (Entity entity : targetLocation.getWorld().getNearbyEntities(targetLocation, 3, 3, 3)) {
            switch (entity.getType()) {
                case PLAYER: entities.add((LivingEntity) entity); break;
                // case ARMOR_STAND: entities.add((LivingEntity) cEntity); break;
            }
        }  
        
        Location location;
        boolean targetLocationTest1;
        boolean targetLocationTest2;
        // Loop through entities to see if any are in the location where armor would be dispensed
        for (LivingEntity player : entities) {          
            location = player.getLocation().clone();
            location.setX(location.getBlockX());
            location.setY(location.getBlockY());
            location.setZ(location.getBlockZ());            
            targetLocationTest1 = (player.getLocation().getBlockX() == targetLocation.getX() && player.getLocation().getBlockY() == targetLocation.getY() && player.getLocation().getBlockZ() == targetLocation.getZ());
            targetLocationTest2 = (player.getEyeLocation().getBlockX() == targetLocation.getX() && player.getEyeLocation().getBlockY() == targetLocation.getY() && player.getEyeLocation().getBlockZ() == targetLocation.getZ());
            if ((targetLocationTest1 || targetLocationTest2)) {
                return ((GlowInventory) ((Player) player).getInventory()).tryToFillSlots(stack, 36, 40);
            }
        }
        return defaultBehavior.dispense(block, stack); // Fallback
    }
}
