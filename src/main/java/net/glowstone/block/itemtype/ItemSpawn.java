package net.glowstone.block.itemtype;

import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.EntityRegistry;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

public class ItemSpawn extends ItemType {
    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock against, BlockFace face, ItemStack holding, Vector clickedLoc) {
        GlowBlock target = against.getRelative(face);
        GlowEntity entity = null;

        Class<? extends GlowEntity> spawn = EntityRegistry.getEntity(holding.getDurability());
        try {
            Constructor<? extends GlowEntity> constructor = spawn.getConstructor(Location.class);
            entity = constructor.newInstance(target.getLocation());
            CreatureSpawnEvent spawnEvent = new CreatureSpawnEvent((LivingEntity) entity, SpawnReason.SPAWNER_EGG);
            if (!spawnEvent.isCancelled()) {
                entity.createSpawnMessage();
            } else {
                // TODO: separate spawning and construction for better event cancellation
                entity.remove();
            }
        } catch (NoSuchMethodException e) {
            GlowServer.logger.log(Level.WARNING, "Invalid entity spawn: ", e);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            GlowServer.logger.log(Level.SEVERE, "Unable to spawn entity: ", e);
        }


    }
}
