package net.glowstone.map;

import static net.glowstone.map.GlowMapCanvas.MAP_SIZE;

import net.glowstone.GlowServer;
import net.glowstone.ServerProvider;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.MaterialValueManager.ValueCollection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 * Glowstone's built-in map renderer.
 */
public final class GlowMapRenderer extends MapRenderer {

    private static final int MAP_SIGHT_DISTANCE_SQUARED = 64 * 64;
    private final GlowMapView map;

    public GlowMapRenderer(GlowMapView map) {
        super(false);
        this.map = map;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        World world = map.getWorld();
        int scaleShift = map.getScale().getValue();
        Location playerLoc = player.getLocation();
        int playerX = playerLoc.getBlockX();
        int playerZ = playerLoc.getBlockZ();
        int cornerX = map.getCenterX() - ((MAP_SIZE / 2) << scaleShift);
        int cornerZ = map.getCenterZ() - ((MAP_SIZE / 2) << scaleShift);
        for (int pixelX = 0; pixelX < MAP_SIZE; pixelX++) {
            for (int pixelY = 0; pixelY < MAP_SIZE; pixelY++) {
                int worldX = cornerX + (pixelX << scaleShift);
                int worldZ = cornerZ + (pixelY << scaleShift);
                if (((worldX - playerX) * (worldX - playerX)
                        + (worldZ - playerZ) * (worldZ - playerZ)) < MAP_SIGHT_DISTANCE_SQUARED) {
                    // TODO: Should the highest block be skipped over if it's e.g. a flower or a
                    // technical block?
                    byte blockColor =
                        colorFor(world.getHighestBlockAt(worldX, worldZ), worldX, worldZ);
                    canvas.setPixel(pixelX, pixelY, blockColor);
                }
            }
        }
    }

    /**
     * Based on https://minecraft.gamepedia.com/Slime#.22Slime_chunks.22 but simplified (doesn't
     * instantiate a Random, and doesn't vary with the world seed. Designed to be reproducible, so
     * that updating a map doesn't change the color unless the map contents have changed.
     */
    private static byte pseudoRandomShade(int worldX, int worldZ) {
        return (byte) ((
                ((worldX * worldX * 0x4c1906) + (worldX * 0x5ac0db) + ((worldZ * worldZ) * 0x4307a7)
                + (worldZ * 0x5f24f))) % 4);
    }

    private static byte colorFor(Block block, int worldX, int worldZ) {
        // TODO: Some blocks vary in map color based on block states (e.g. wood species)
        ValueCollection materialValues;
        materialValues = block instanceof GlowBlock ? ((GlowBlock) block).getMaterialValues()
            : ((GlowServer) ServerProvider.getServer()).getMaterialValueManager()
                .getValues(block.getType());
        byte baseColor = materialValues.getBaseMapColor();
        return (byte) (baseColor | pseudoRandomShade(worldX, worldZ));
    }
}
