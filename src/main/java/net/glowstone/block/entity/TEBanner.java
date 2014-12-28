package net.glowstone.block.entity;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.blocktype.BlockBanner;
import net.glowstone.block.state.GlowBanner;
import net.glowstone.constants.GlowBlockEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.BannerPattern;
import org.bukkit.DyeColor;

import java.util.List;

public class TEBanner extends TileEntity {

    private DyeColor base = DyeColor.WHITE;
    private BannerPattern pattern = BannerPattern.builder().build();

    public TEBanner(GlowBlock block) {
        super(block);
        setSaveId("Banner");
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        if (tag.isList("Patterns", TagType.COMPOUND)) {
            List<CompoundTag> pattern = tag.getCompoundList("Patterns");
            this.pattern = BlockBanner.fromNBT(pattern);
        }

        if (tag.isInt("Base")) {
            this.base = DyeColor.getByDyeData((byte) tag.getInt("Base"));
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putCompoundList("Patterns", BlockBanner.toNBT(pattern));
        tag.putInt("Base", base.getDyeData());
    }

    @Override
    public GlowBlockState getState() {
        return new GlowBanner(block);
    }

    @Override
    public void update(GlowPlayer player) {
        super.update(player);
        CompoundTag nbt = new CompoundTag();
        saveNbt(nbt);
        player.sendBlockEntityChange(getBlock().getLocation(), GlowBlockEntity.BANNER, nbt);
    }

    public void setPattern(BannerPattern pattern) {
        this.pattern = pattern;
    }

    public void setBase(DyeColor base) {
        this.base = base;
    }

    public DyeColor getBase() {
        return base;
    }

    public BannerPattern getPattern() {
        return pattern;
    }
}
