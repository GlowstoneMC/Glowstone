package net.glowstone.inventory;

import com.google.common.collect.ImmutableMap;
import net.glowstone.block.blocktype.BlockBanner;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.apache.commons.lang.Validate;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GlowMetaBanner extends GlowMetaItem implements BannerMeta {

    private PatternType pattern = null; // TODO PatternType.builder().build();

    public GlowMetaBanner(GlowMetaItem meta) {
        super(meta);
        if (meta == null || !(meta instanceof GlowMetaBanner)) {
            return;
        }
        GlowMetaBanner banner = (GlowMetaBanner) meta;
        this.pattern = banner.pattern;
    }

    /* TODO
    @Override
    public void setPattern(PatternType pattern) {
        Validate.notNull(pattern, "Pattern cannot be null!");
        this.pattern = pattern;
    }

    @Override
    public PatternType getPattern() {
        return pattern;
    }
    */

    @Override
    void writeNbt(CompoundTag tag) {
        super.writeNbt(tag);
        CompoundTag blockEntityTag = new CompoundTag();

        blockEntityTag.putCompoundList("Patterns", BlockBanner.toNBT(pattern));
        tag.putCompound("BlockEntityTag", blockEntityTag);
    }

    @Override
    void readNbt(CompoundTag tag) {
        super.readNbt(tag);
        if (tag.isCompound("BlockEntityTag")) {
            CompoundTag blockEntityTag = tag.getCompound("BlockEntityTag");
            if (blockEntityTag.isList("Patterns", TagType.COMPOUND)) {
                List<CompoundTag> pattern = blockEntityTag.getCompoundList("Patterns");
                //TODO this.pattern = BlockBanner.fromNBT(pattern);
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
        /* TODO
        for (PatternType.BannerLayer layer : pattern.getLayers()) {
            patternsList.add(ImmutableMap.of(layer.getTexture().toString(), layer.getColor().toString()));
        }
        */
        result.put("pattern", patternsList);
        return result;
    }

    @Override
    public DyeColor getBaseColor() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setBaseColor(DyeColor dyeColor) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Pattern> getPatterns() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPatterns(List<Pattern> patterns) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addPattern(Pattern pattern) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Pattern getPattern(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Pattern removePattern(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPattern(int i, Pattern pattern) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int numberOfPatterns() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
