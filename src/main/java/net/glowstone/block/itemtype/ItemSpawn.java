package net.glowstone.block.itemtype;

import net.glowstone.block.GlowBlock;
import net.glowstone.entity.EntityRegistry;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowMetaSpawn;
import net.glowstone.io.entity.EntityStorage;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemSpawn extends ItemType {

    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock against, BlockFace face,
        ItemStack holding, Vector clickedLoc, EquipmentSlot hand) {
        Location location = against.getLocation()
            .add(face.getModX(), face.getModY(), face.getModZ());
        // TODO: change mob spawner when clicked by monster egg
        if (holding.hasItemMeta() && holding.getItemMeta() instanceof GlowMetaSpawn) {
            GlowMetaSpawn meta = (GlowMetaSpawn) holding.getItemMeta();
            EntityType type = meta.getSpawnedType();
            CompoundTag tag = meta.getEntityTag();

            // TODO: check for fence/wall
            //if (face == BlockFace.UP && against instanceof BlockFence) {
            //location.add(0, 0.5, 0);
            //}

            if (type != null) {
                GlowEntity entity = against.getWorld()
                    .spawn(location.add(0.5, 0, 0.5), EntityRegistry.getEntity(type),
                        SpawnReason.SPAWNER_EGG);
                if (tag != null) {
                    EntityStorage.load(entity, tag);
                }
                if (player.getGameMode() != GameMode.CREATIVE) {
                    holding.setAmount(holding.getAmount() - 1);
                }
            }
        }
    }
}
