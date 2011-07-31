package net.glowstone.map;

import java.awt.Image;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapFont;

/**
 * Represents a canvas for drawing to a map. Each canvas is associated with a
 * specific {@link MapRenderer} and represents that renderer's layer on the map.
 */
public final class GlowMapCanvas implements MapCanvas {
    
    private MapCursorCollection cursors = new MapCursorCollection();
    private final byte[] buffer = new byte[128 * 128];
    private final GlowMapView mapView;
    private byte[] base;
    
    protected GlowMapCanvas(GlowMapView mapView) {
        this.mapView = mapView;
    }

    public GlowMapView getMapView() {
        return mapView;
    }

    public MapCursorCollection getCursors() {
        return cursors;
    }

    public void setCursors(MapCursorCollection cursors) {
        this.cursors = cursors;
    }

    public void setPixel(int x, int y, byte color) {
        if (x < 0 || y < 0 || x >= 128 || y >= 128) return;
        if (buffer[y * 128 + x] != color) {
            buffer[y * 128 + x] = color;
            // TODO: mark dirty.
        }
    }

    public byte getPixel(int x, int y) {
        if (x < 0 || y < 0 || x >= 128 || y >= 128) return 0;
        return buffer[y * 128 + x];
    }

    public byte getBasePixel(int x, int y) {
        if (x < 0 || y < 0 || x >= 128 || y >= 128) return 0;
        return base[y * 128 + x];
    }
    
    protected void setBase(byte[] base) {
        this.base = base;
    }
    
    protected byte[] getBuffer() {
        return buffer;
    }

    public void drawImage(int x, int y, Image image) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void drawText(int x, int y, MapFont font, String text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
