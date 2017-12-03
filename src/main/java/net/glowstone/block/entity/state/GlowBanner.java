package net.glowstone.block.entity.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.BannerEntity;
import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class GlowBanner extends GlowBlockState implements Banner {

    private DyeColor base;
    private List<Pattern> patterns;

    public GlowBanner(GlowBlock block) {
        super(block);
        base = getBlockEntity().getBase();
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
    public DyeColor getBaseColor() {
        return base;
    }

    @Override
    public void setBaseColor(DyeColor dyeColor) {
        checkNotNull(base, "Base cannot be null");
        base = dyeColor;
    }

    @Override
    public List<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    public void setPatterns(List<Pattern> patterns) {
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
            banner.setBase(base);
            banner.setPatterns(patterns);
            getBlockEntity().updateInRange();
        }
        return result;
    }

    @Override
    public boolean isPlaced() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
