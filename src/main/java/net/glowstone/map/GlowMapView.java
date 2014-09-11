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
    
    //private final Map<GlowPlayer, RenderData> renderCache = new HashMap<GlowPlayer, RenderData<>();
    private final List<MapRenderer> renderers = new ArrayList<>();
    private final Map<MapRenderer, Map<GlowPlayer, GlowMapCanvas>> canvases = new HashMap<>();
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

    @Override
    public short getId() {
        return id;
    }

    @Override
    public boolean isVirtual() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Scale getScale() {
        return scale;
    }

    @Override
    public void setScale(Scale scale) {
        if (scale == null) {
            throw new NullPointerException();
        }
        this.scale = scale;
    }

    @Override
    public int getCenterX() {
        return x;
    }

    @Override
    public int getCenterZ() {
        return z;
    }

    @Override
    public void setCenterX(int x) {
        this.x = x;
    }

    @Override
    public void setCenterZ(int z) {
        this.z = z;
    }

    @Override
    public GlowWorld getWorld() {
        return world;
    }

    @Override
    public void setWorld(World world) {
        this.world = (GlowWorld) world;
    }

    @Override
    public List<MapRenderer> getRenderers() {
        return renderers;
    }

    @Override
    public void addRenderer(MapRenderer renderer) {
        if (!renderers.contains(renderer)) {
            renderers.add(renderer);
            canvases.put(renderer, new HashMap<GlowPlayer, GlowMapCanvas>());
            renderer.initialize(this);
        }
    }

    @Override
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
