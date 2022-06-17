package net.glowstone.block.entity.state;

import lombok.Getter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.BannerEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

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
    public void setPattern(int i, @NotNull Pattern pattern) {
        checkNotNull(pattern, "Pattern cannot be null");
        patterns.set(i, pattern);
    }

    @Override
    public int numberOfPatterns() {
        return patterns.size();
    }

    @Override
    public void setBaseColor(@NotNull DyeColor dyeColor) {
        checkNotNull(baseColor, "Base cannot be null");
        baseColor = dyeColor;
    }

    @Override
    public @NotNull List<Pattern> getPatterns() {
        // Defensive copy
        return new ArrayList<>(patterns);
    }

    @Override
    public void setPatterns(@NotNull List<Pattern> patterns) {
        // Defensive copy
        this.patterns = new ArrayList<>(patterns);
    }

    @Override
    public void addPattern(@NotNull Pattern pattern) {
        checkNotNull(pattern, "Pattern cannot be null");
        patterns.add(pattern);
    }

    @Override
    public @NotNull Pattern getPattern(int i) {
        return patterns.get(i);
    }

    @Override
    public @NotNull Pattern removePattern(int i) {
        return patterns.remove(i);
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean successful = super.update(force, applyPhysics);
        if (successful) {
            BannerEntity banner = getBlockEntity();
            banner.setBase(baseColor);
            banner.setPatterns(patterns);
            getBlockEntity().updateInRange();
        }
        return successful;
    }

    @Override
    public boolean isPlaced() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable Component customName() {
        return null;
    }

    @Override
    public void customName(@Nullable Component customName) {

    }

    @Override
    public @Nullable String getCustomName() {
        return null;
    }

    @Override
    public void setCustomName(@Nullable String name) {

    }
}
