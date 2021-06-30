package net.glowstone.map;

import java.awt.Image;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.net.message.play.game.MapDataMessage.Section;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 * Represents a canvas for drawing to a map. Each canvas is associated with a specific
 * {@link MapRenderer} and represents that renderer's layer on the map.
 */
public final class GlowMapCanvas implements MapCanvas {

    public static final int MAP_SIZE = 128;
    @Getter(AccessLevel.PROTECTED)
    private final byte[] buffer = new byte[MAP_SIZE * MAP_SIZE];
    @Getter
    private final MapView mapView;
    @Getter
    @Setter
    private MapCursorCollection cursors = new MapCursorCollection();
    private byte[] base;

    protected GlowMapCanvas(MapView mapView) {
        this.mapView = mapView;
    }

    /**
     * Creates a new GlowMapCanvas for the given {@link MapView} and applies all updates seen by the
     * given player.
     *
     * @param mapView The {@link MapView} to associate with this canvas and render
     * @param player  The player to pass to {@link MapRenderer#render(MapView, MapCanvas, Player)}
     * @return a new, rendered GlowMapCanvas
     */
    public static GlowMapCanvas createAndRender(MapView mapView, Player player) {
        GlowMapCanvas out = new GlowMapCanvas(mapView);
        out.update(player);
        return out;
    }

    /**
     * Applies all updates seen by the given player according to the {@link MapView}'s renderers.
     *
     * @param player The player to pass to {@link MapRenderer#render(MapView, MapCanvas, Player)}
     */
    public void update(Player player) {
        for (MapRenderer renderer : mapView.getRenderers()) {
            renderer.initialize(mapView);
            renderer.render(mapView, this, player);
        }
    }

    @Override
    public void setPixel(int x, int y, byte color) {
        if (x < 0 || y < 0 || x >= MAP_SIZE || y >= MAP_SIZE) {
            return;
        }
        if (buffer[y * MAP_SIZE + x] != color) {
            buffer[y * MAP_SIZE + x] = color;
            // todo: mark dirty
        }
    }

    @Override
    public byte getPixel(int x, int y) {
        if (x < 0 || y < 0 || x >= MAP_SIZE || y >= MAP_SIZE) {
            return 0;
        }
        return buffer[y * MAP_SIZE + x];
    }

    @Override
    public byte getBasePixel(int x, int y) {
        if (x < 0 || y < 0 || x >= MAP_SIZE || y >= MAP_SIZE) {
            return 0;
        }
        return base[y * MAP_SIZE + x];
    }

    protected void setBase(byte... base) {
        this.base = base;
    }

    @Override
    public void drawImage(int x, int y, Image image) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawText(int x, int y, MapFont font, String text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Converts a snapshot of this canvas to a {@link Section} for transmission to the client.
     *
     * @return a {@link Section} holding a copy of this canvas's contents
     */
    public Section toSection() {
        return new Section(
            MAP_SIZE, MAP_SIZE, mapView.getCenterX(), mapView.getCenterZ(), buffer.clone());
    }
}
