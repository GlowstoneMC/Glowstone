package net.glowstone.block.blocktype;

import java.util.concurrent.ThreadLocalRandom;
import net.glowstone.GlowServer;
import net.glowstone.GlowWorld;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.monster.GlowPigZombie;
import net.glowstone.util.pattern.PortalShape;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class BlockPortal extends BlockType {
    @Override
    public void onNearBlockChanged(final GlowBlock block, BlockFace face, GlowBlock changedBlock, Material oldType, byte oldData, Material newType, byte newData) {
        if (newType == oldType || (oldType != Material.PORTAL && oldType != Material.OBSIDIAN)) {
            return;
        }
        int faceData = block.getData() & 3;
        BlockFace left = faceData == 1 ? BlockFace.WEST : faceData == 2 ? BlockFace.NORTH : null;
        if (left == null) {
            return;
        }
        PortalShape shape = new PortalShape(block.getLocation(), left);
        if (shape.validate() && shape.getPortalBlockCount() == shape.getHeight() * shape.getWidth()) {
            return;
        }
        block.setType(Material.AIR);
    }

    @Override
    public void updateBlock(GlowBlock block) {
        // remove invalid portal blocks
        if ((block.getData() & 3) == 0) {
           block.setType(Material.AIR);
            System.out.println(block.getLocation());
            return;
        }
        GlowWorld world = block.getWorld();
        GlowServer server = world.getServer();
        // No pigman spawns without nether
        if (!server.getAllowNether() ||
            // Pigmen only spawn in overworld
            world.getEnvironment() != World.Environment.NORMAL ||
                //!server.spigot().getSpigotConfig(). // TODO: Implement Spigot config
                //    getBoolean("enable-zombie-pigmen-portal-spawns") || // Pigmen spawning explicitly disabled
            // Increasing spawn chance with increasing difficulty.
            // If random * 2000 is 0, it is still not bigger than the ordinal of peaceful (0)
            ThreadLocalRandom.current().nextInt(200) > block.getWorld().getDifficulty().ordinal()
        ) {
            return;
        }

        Location location = block.getLocation();
        //move down to the bottom of the portal
        while (location.getBlock().getType() == Material.PORTAL && location.getY() > 0) {
            location.subtract(0, 1, 0);
        }
        world.spawn(location.add(.5, 2.1, .5), GlowPigZombie.class,
            CreatureSpawnEvent.SpawnReason.NETHER_PORTAL);
    }

    @Override
    public boolean canTickRandomly() {
        return true;
    }
}
