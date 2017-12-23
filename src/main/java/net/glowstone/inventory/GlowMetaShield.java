package net.glowstone.inventory;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.glowstone.block.blocktype.BlockBanner;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class GlowMetaShield extends GlowMetaItem implements BannerMeta {

    private List<Pattern> patterns = new ArrayList<>();
    private DyeColor baseColor = null;

    public GlowMetaShield(GlowMetaItem meta) {
        super(meta);
        if (!(meta instanceof GlowMetaShield)) {
            return;
        }
        GlowMetaShield banner = (GlowMetaShield) meta;
        patterns = banner.patterns;
        baseColor = banner.baseColor;
    }

    @Override
    public List<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    public void setPatterns(List<Pattern> patterns) {
        checkNotNull(patterns, "Pattern cannot be null!");
        this.patterns = patterns;
    }

    @Override
    public DyeColor getBaseColor() {
        return baseColor;
    }

    @Override
    public void setBaseColor(DyeColor dyeColor) {
        this.baseColor = dyeColor;
    }

    @Override
    public void addPattern(Pattern pattern) {
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
    public void setPattern(int i, Pattern pattern) {
        patterns.set(i, pattern);
    }

    @Override
    public int numberOfPatterns() {
        return patterns.size();
    }

    @Override
    void writeNbt(CompoundTag tag) {
        super.writeNbt(tag);
        if (baseColor != null) {
            tag.putInt("Base", baseColor.getWoolData());
        }
        tag.putCompoundList("Patterns", BlockBanner.toNbt(patterns));
    }

    @Override
    void readNbt(CompoundTag tag) {
        super.readNbt(tag);
        if (tag.isList("Patterns", TagType.COMPOUND)) {
            List<CompoundTag> patterns = tag.getCompoundList("Patterns");
            this.patterns = BlockBanner.fromNbt(patterns);
        }
        if (tag.isInt("Base")) {
            this.baseColor = DyeColor.getByWoolData((byte) tag.getInt("Base"));
        }
    }

    @Override
    public ItemMeta clone() {
        return new GlowMetaShield(this);
    }

    @Override
    public boolean isApplicable(Material material) {
        return material == Material.SHIELD;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = super.serialize();
        result.put("meta-type", "SHIELD");
        List<Map<String, String>> patternsList = new ArrayList<>();
        for (Pattern pattern : patterns) {
            patternsList.add(
                ImmutableMap.of(pattern.getPattern().toString(), pattern.getColor().toString()));
        }
        result.put("pattern", patternsList);
        if (baseColor != null) {
            result.put("baseColor", baseColor);
        }
        return result;
    }
}
