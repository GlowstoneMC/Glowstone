package net.glowstone.block.state;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEBanner;
import org.apache.commons.lang.Validate;
import org.bukkit.BannerPattern;
import org.bukkit.DyeColor;
import org.bukkit.block.Banner;

public class GlowBanner extends GlowBlockState implements Banner {

    private DyeColor base;
    private BannerPattern pattern;

    public GlowBanner(GlowBlock block) {
        super(block);
        base = getTileEntity().getBase();
        pattern = getTileEntity().getPattern();
    }

    private TEBanner getTileEntity() {
        return (TEBanner) getBlock().getTileEntity();
    }

    @Override
    public void setBase(DyeColor base) {
        Validate.notNull(base, "Base cannot be null");
        this.base = base;
    }

    @Override
    public DyeColor getBase() {
        return base;
    }

    @Override
    public void setPattern(BannerPattern pattern) {
        Validate.notNull(pattern, "Pattern cannot be null");
        this.pattern = pattern;
    }

    @Override
    public BannerPattern getPattern() {
        return pattern;
    }

    @Override
    public boolean update(boolean force, boolean applyPhysics) {
        boolean result = super.update(force, applyPhysics);
        if (result) {
            TEBanner banner = getTileEntity();
            banner.setBase(base);
            banner.setPattern(pattern);
            getTileEntity().updateInRange();
        }
        return result;
    }
}
