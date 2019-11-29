package net.glowstone.block.flattening;

import java.util.HashMap;
import java.util.Map;
import net.glowstone.block.flattening.generated.FlatteningUtil;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GlowBlockData implements BlockData {
    private final Material material;
    private Map<String, Object> propertyMap;

    protected GlowBlockData(Material material) {
        this.material = material;
        propertyMap = new HashMap<>(FlatteningUtil.getDefaultProperties(material)); // TODO: use better mapping/properties system
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    @Override
    public String getAsString() {
        // TODO: What is the syntax for this? (1.13)
        return null;
    }

    @Override
    public @NotNull String getAsString(boolean hideUnspecified) {
        // TODO: What is the syntax for this? (1.13)
        return hideUnspecified ? null : getAsString();
    }

    @Override
    public @NotNull BlockData merge(@NotNull BlockData blockData) {
        return clone(); // TODO
    }

    @Override
    public boolean matches(@Nullable BlockData blockData) {
        return false; // TODO
    }

    @Override
    public abstract BlockData clone();

    public abstract int serialize();
}
