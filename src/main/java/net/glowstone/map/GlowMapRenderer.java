package net.glowstone.map;

import static net.glowstone.map.GlowMapCanvas.MAP_SIZE;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.material.MaterialData;

/**
 * Glowstone's built-in map renderer.
 */
public final class GlowMapRenderer extends MapRenderer {

    private final GlowMapView map;

    public GlowMapRenderer(GlowMapView map) {
        super(false);
        this.map = map;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        World world = map.getWorld();
        int scaleShift = map.getScale().getValue();
        for (int pixelX = 0; pixelX < MAP_SIZE; pixelX++) {
            for (int pixelY = 0; pixelY < MAP_SIZE; pixelY++) {
                int worldX = map.getCenterX() + (pixelX - MAP_SIZE/2) << scaleShift;
                int worldZ = map.getCenterZ() + (pixelY - MAP_SIZE/2) << scaleShift;
                byte blockColor = colorFor(world.getHighestBlockAt(worldX, worldZ),
                    worldX, worldZ);
                canvas.setPixel(pixelX, pixelY, blockColor);
            }
        }
    }

    private static byte pseudoRandomShade(int worldX, int worldZ) {
        return (byte) ((
                ((worldX * worldX * 0x4c1906) + (worldX * 0x5ac0db) + ((worldZ * worldZ) * 0x4307a7)
                + (worldZ * 0x5f24f))) % 4);
    }
    private static byte colorFor(Block block, int worldX, int worldZ) {
        byte base_color;
        switch (block.getType()) {
            case GRASS:
            case LONG_GRASS:
                base_color = 4;
                break;
            case SAND:
            case SANDSTONE:
            case SANDSTONE_STAIRS:
                base_color = 8;
                break;
            case HUGE_MUSHROOM_1:
                base_color = (byte) (isMushroomStem(block.getData()) ? 12 : 40);
                break;
            case HUGE_MUSHROOM_2:
                base_color = (byte) (isMushroomStem(block.getData()) ? 12 : 28);
                break;
            case STONE:
            case COBBLESTONE:
            case COBBLESTONE_STAIRS:
            case MOSSY_COBBLESTONE:
            case STONE_SLAB2:
            case DOUBLE_STONE_SLAB2:
                base_color = 44;
                break;
            case WATER:
                base_color = 48;
                break;
            // TODO: Add all the other materials
            default:
                base_color = 0;
        }
        return (byte) (base_color | pseudoRandomShade(worldX, worldZ));
    }

    private static boolean isMushroomStem(byte data) {
        return data == 10 || data == 15;
    }
}
