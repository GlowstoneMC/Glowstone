package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.EntityRegistry;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.util.Vector;

public class ItemSpawn extends ItemType {
    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock against, BlockFace face, ItemStack holding, Vector clickedLoc) {
        Location location = against.getLocation().add(face.getModX(), face.getModY(), face.getModZ());
        // TODO: change mob spawner when clicked by monster egg
        // TODO: MonsterEgg meta. Eggs can hold entire entities now.
        if (holding.hasItemMeta() && holding.getItemMeta() instanceof SpawnEggMeta) {
            SpawnEggMeta meta = (SpawnEggMeta) holding.getItemMeta();
            EntityType type = meta.getSpawnedType();

            // TODO: check for fence/wall
            //if (face == BlockFace.UP && against instanceof BlockFence) {
                //location.add(0, 0.5, 0);
            //}

            if (type != null) {
                against.getWorld().spawn(location.add(0.5, 0, 0.5), EntityRegistry.getEntity(type), SpawnReason.SPAWNER_EGG);
            }
        }
    }
}
