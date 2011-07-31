package net.glowstone.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.glowstone.GlowWorld;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.World;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 * Represents a map item.
 */
public final class GlowMapView implements MapView {
    
    //private final Map<GlowPlayer, RenderData> renderCache = new HashMap<GlowPlayer, RenderData>();
    private final List<MapRenderer> renderers = new ArrayList<MapRenderer>();
    private final Map<MapRenderer, Map<GlowPlayer, GlowMapCanvas>> canvases = new HashMap<MapRenderer, Map<GlowPlayer, GlowMapCanvas>>();
    private final short id;
    private Scale scale;
    private int x, z;
    private GlowWorld world;
    
    protected GlowMapView(GlowWorld world, short id) {
        this.world = world;
        this.id = id;
        this.x = world.getSpawnLocation().getBlockX();
        this.z = world.getSpawnLocation().getBlockZ();
        this.scale = Scale.FAR;
        addRenderer(new GlowMapRenderer(this));
    }

    public short getId() {
        return id;
    }

    public boolean isVirtual() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        if (scale == null) {
            throw new NullPointerException();
        }
        this.scale = scale;
    }

    public int getCenterX() {
        return x;
    }

    public int getCenterZ() {
        return z;
    }

    public void setCenterX(int x) {
        this.x = x;
    }

    public void setCenterZ(int z) {
        this.z = z;
    }

    public GlowWorld getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = (GlowWorld) world;
    }

    public List<MapRenderer> getRenderers() {
        return renderers;
    }

    public void addRenderer(MapRenderer renderer) {
        if (!renderers.contains(renderer)) {
            renderers.add(renderer);
            canvases.put(renderer, new HashMap<GlowPlayer, GlowMapCanvas>());
            renderer.initialize(this);
        }
    }

    public boolean removeRenderer(MapRenderer renderer) {
        if (renderers.contains(renderer)) {
            renderers.remove(renderer);
            canvases.remove(renderer);
            return true;
        } else {
            return false;
        }
    }
    
}
