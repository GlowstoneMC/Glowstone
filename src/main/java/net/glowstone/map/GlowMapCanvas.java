package net.glowstone.map;

import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapFont;

import java.awt.*;

/**
 * Represents a canvas for drawing to a map. Each canvas is associated with a
 * specific {@link org.bukkit.map.MapRenderer} and represents that renderer's layer on the map.
 */
public final class GlowMapCanvas implements MapCanvas {

    public static final int MAP_SIZE = 128;
    
    private MapCursorCollection cursors = new MapCursorCollection();
    private final byte[] buffer = new byte[MAP_SIZE * MAP_SIZE];
    private final GlowMapView mapView;
    private byte[] base;
    
    protected GlowMapCanvas(GlowMapView mapView) {
        this.mapView = mapView;
    }

    @Override
    public GlowMapView getMapView() {
        return mapView;
    }

    @Override
    public MapCursorCollection getCursors() {
        return cursors;
    }

    @Override
    public void setCursors(MapCursorCollection cursors) {
        this.cursors = cursors;
    }

    @Override
    public void setPixel(int x, int y, byte color) {
        if (x < 0 || y < 0 || x >= MAP_SIZE || y >= MAP_SIZE) return;
        if (buffer[y * MAP_SIZE + x] != color) {
            buffer[y * MAP_SIZE + x] = color;
            // todo: mark dirty
        }
    }

    @Override
    public byte getPixel(int x, int y) {
        if (x < 0 || y < 0 || x >= MAP_SIZE || y >= MAP_SIZE) return 0;
        return buffer[y * MAP_SIZE + x];
    }

    @Override
    public byte getBasePixel(int x, int y) {
        if (x < 0 || y < 0 || x >= MAP_SIZE || y >= MAP_SIZE) return 0;
        return base[y * MAP_SIZE + x];
    }
    
    protected void setBase(byte[] base) {
        this.base = base;
    }
    
    protected byte[] getBuffer() {
        return buffer;
    }

    @Override
    public void drawImage(int x, int y, Image image) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void drawText(int x, int y, MapFont font, String text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
