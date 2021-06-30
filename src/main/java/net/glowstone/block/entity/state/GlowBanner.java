package net.glowstone.block.entity.state;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import lombok.Getter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.BannerEntity;
import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class GlowBanner extends GlowBlockState implements Banner {

    @Getter
    private DyeColor baseColor;
    private List<Pattern> patterns;

    /**
     * Creates an entity for the given banner block.
     *
     * @param block the block this banner occupies
     */
    public GlowBanner(GlowBlock block) {
        super(block);
        baseColor = getBlockEntity().getBase();
        patterns = getBlockEntity().getPatterns();
    }

    private BannerEntity getBlockEntity() {
        return (BannerEntity) getBlock().getBlockEntity();
    }

    @Override
    public void setPattern(int i, Pattern pattern) {
        checkNotNull(pattern, "Pattern cannot be null");
        patterns.set(i, pattern);
    }

    @Override
    public int numberOfPatterns() {
        return patterns.size();
    }

    @Override
    public void setBaseColor(DyeColor dyeColor) {
        checkNotNull(baseColor, "Base cannot be null");
        baseColor = dyeColor;
    }

    @Override
    public List<Pattern> getPatterns() {
        // TODO: Defensive copy
        return patterns;
    }

    @Override
    public void setPatterns(List<Pattern> patterns) {
        // TODO: Defensive copy
        this.patterns = patterns;
    }

    @Override
    public void addPattern(Pattern pattern) {
        checkNotNull(pattern, "Pattern cannot be null");
        patterns.add(pattern);
    }

    @Override
    public Pattern getPattern(int i) {
        return patterns.get(i);
    }

    @Override
    public Pattern removePattern(int i) {
        return patterns.remove(i);
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            BannerEntity banner = getBlockEntity();
            banner.setBase(baseColor);
            banner.setPatterns(patterns);
            getBlockEntity().updateInRange();
        }
        return result;
    }

    @Override
    public boolean isPlaced() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        throw new UnsupportedOperationException();
    }
}
