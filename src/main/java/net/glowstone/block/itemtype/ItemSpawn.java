package net.glowstone.block.itemtype;

import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.EntityRegistry;
import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ItemSpawn extends ItemType {
    @Override
    public void rightClickBlock(GlowPlayer player, GlowBlock against, BlockFace face, ItemStack holding, Vector clickedLoc) {
        GlowBlock target = against.getRelative(face);
        GlowEntity entity = null;

        Class<? extends GlowEntity> spawn = EntityRegistry.getEntity(holding.getDurability());
        try {

            Constructor<? extends GlowEntity> constructor = spawn.getConstructor(Location.class);
            entity = constructor.newInstance(target.getLocation());
            entity.createSpawnMessage();

        } catch (NoSuchMethodException e) {
            GlowServer.logger.warning("Invalid entity spawn");
            e.printStackTrace();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }


    }
}
