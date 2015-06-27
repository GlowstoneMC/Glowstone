package net.glowstone.inventory;

import com.google.common.collect.ImmutableMap;
import net.glowstone.block.blocktype.BlockBanner;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.apache.commons.lang3.Validate;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GlowMetaBanner extends GlowMetaItem implements BannerMeta {

    private List<Pattern> patterns = new ArrayList<>();

    public GlowMetaBanner(GlowMetaItem meta) {
        super(meta);
        if (meta == null || !(meta instanceof GlowMetaBanner)) {
            return;
        }
        GlowMetaBanner banner = (GlowMetaBanner) meta;
        this.patterns = banner.patterns;
    }

    @Override
    public void setPatterns(List<Pattern> patterns) {
        Validate.notNull(patterns, "Pattern cannot be null!");
        this.patterns = patterns;
    }

    @Override
    public List<Pattern> getPatterns() {
        return patterns;
    }

    @Override
    public DyeColor getBaseColor() {
        return getPattern(0).getColor(); // TODO: multiple colors?
    }

    @Override
    public void setBaseColor(DyeColor dyeColor) {
        /// TODO: where does this go? each org.bukkit.block.banner.Pattern has a "color"
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
        CompoundTag blockEntityTag = new CompoundTag();

        blockEntityTag.putCompoundList("Patterns", BlockBanner.toNBT(patterns));
        tag.putCompound("BlockEntityTag", blockEntityTag);
    }

    @Override
    void readNbt(CompoundTag tag) {
        super.readNbt(tag);
        if (tag.isCompound("BlockEntityTag")) {
            CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");
            if (blockEntityTag.isList("Patterns", TagType.COMPOUND)) {
                List<CompoundTag> patterns = blockEntityTag.getCompoundList("Patterns");
                this.patterns = BlockBanner.fromNBT(patterns);
            }
        }
    }

    @Override
    public void addItemFlags(ItemFlag... itemFlags) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeItemFlags(ItemFlag... itemFlags) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<ItemFlag> getItemFlags() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasItemFlag(ItemFlag itemFlag) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ItemMeta clone() {
        return new GlowMetaBanner(this);
    }

    @Override
    public boolean isApplicable(Material material) {
        return material == Material.BANNER;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = super.serialize();
        result.put("meta-type", "BANNER");
        List<Map<String, String>> patternsList = new ArrayList<>();
        for (Pattern pattern : patterns) {
            patternsList.add(ImmutableMap.of(pattern.getPattern().toString(), pattern.getColor().toString()));
        }
        result.put("pattern", patternsList);
        return result;
    }

}
