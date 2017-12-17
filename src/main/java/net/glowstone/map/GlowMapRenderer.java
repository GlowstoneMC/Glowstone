package net.glowstone.map;

import static net.glowstone.map.GlowMapCanvas.MAP_SIZE;

import net.glowstone.GlowServer;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.MaterialValueManager.ValueCollection;
import org.bukkit.Bukkit;
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

    private final GlowMapView map;

    public GlowMapRenderer(GlowMapView map) {
        super(false);
        this.map = map;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        // todo
    }

}
