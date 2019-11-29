package net.glowstone.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.World;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 * Represents a map item.
 */
public final class GlowMapView implements MapView {

    //private final Map<GlowPlayer, RenderData> renderCache =
    //        new HashMap<GlowPlayer, RenderData<>();
    private final List<MapRenderer> renderers = new ArrayList<>();
    private final Map<MapRenderer, Map<GlowPlayer, GlowMapCanvas>> canvases = new HashMap<>();
    @Getter
    private final int id;
    @Getter
    private Scale scale;
    @Getter
    @Setter
    private int centerX;
    @Getter
    @Setter
    private int centerZ;
    @Getter
    @Setter
    private World world;
    @Getter
    @Setter
    private boolean unlimitedTracking;

    protected GlowMapView(World world, int id) {
        this.world = world;
        this.id = id;
        centerX = world.getSpawnLocation().getBlockX();
        centerZ = world.getSpawnLocation().getBlockZ();
        scale = Scale.FAR;
        addRenderer(new GlowMapRenderer(this));
    }

    @Override
    public boolean isVirtual() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setScale(Scale scale) {
        if (scale == null) {
            throw new NullPointerException();
        }
        this.scale = scale;
    }

    @Override
    public List<MapRenderer> getRenderers() {
        // TODO: Defensive copy
        return renderers;
    }

    @Override
    public void addRenderer(MapRenderer renderer) {
        if (!renderers.contains(renderer)) {
            renderers.add(renderer);
            canvases.put(renderer, new HashMap<>());
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
