package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.EntityRegistry;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnMeta;
import org.bukkit.util.Vector;

public class ItemSpawn extends ItemType {
    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock against, BlockFace face, ItemStack holding, Vector clickedLoc) {
        GlowBlock target = against.getRelative(face);

        if (holding.hasItemMeta() && holding.getItemMeta() instanceof SpawnMeta) {
            SpawnMeta meta = (SpawnMeta) holding.getItemMeta();
            EntityType type = meta.getEntityType();

            if (type != null) {
                target.getWorld().spawn(target.getLocation(), EntityRegistry.getEntity(type), SpawnReason.SPAWNER_EGG);
            }
        }
    }
}
