package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEBanner;
import org.apache.commons.lang3.Validate;
import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;

import java.util.ArrayList;
import java.util.List;

public class GlowBanner extends GlowBlockState implements Banner {

    private DyeColor base;
    private List<Pattern> patterns = new ArrayList<>();

    public GlowBanner(GlowBlock block) {
        super(block);
        base = getTileEntity().getBase();
        patterns = getTileEntity().getPatterns();
    }

    private TEBanner getTileEntity() {
        return (TEBanner) getBlock().getTileEntity();
    }

    @Override
    public void setPattern(int i, Pattern pattern) {
        Validate.notNull(pattern, "Pattern cannot be null");
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
        Validate.notNull(base, "Base cannot be null");
        this.base = dyeColor;
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
        Validate.notNull(pattern, "Pattern cannot be null");
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
            TEBanner banner = getTileEntity();
            banner.setBase(base);
            banner.setPatterns(patterns);
            getTileEntity().updateInRange();
        }
        return result;
    }

    @Override
    public boolean isPlaced() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
