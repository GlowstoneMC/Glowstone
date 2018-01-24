package net.glowstone.block.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.blocktype.BlockBanner;
import net.glowstone.block.entity.state.GlowBanner;
import net.glowstone.constants.GlowBlockEntity;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;

public class BannerEntity extends BlockEntity {

    @Getter
    @Setter
    private DyeColor base = DyeColor.WHITE;
    private List<Pattern> patterns = new ArrayList<>();

    public BannerEntity(GlowBlock block) {
        super(block);
        setSaveId("minecraft:banner");
    }

    @Override
    public void loadNbt(CompoundTag tag) {
        super.loadNbt(tag);
        if (tag.isList("Patterns", TagType.COMPOUND)) {
            List<CompoundTag> pattern = tag.getCompoundList("Patterns");
            patterns = BlockBanner.fromNbt(pattern);
        }

        if (tag.isInt("Base")) {
            base = DyeColor.getByDyeData((byte) tag.getInt("Base"));
        }
    }

    @Override
    public void saveNbt(CompoundTag tag) {
        super.saveNbt(tag);
        tag.putCompoundList("Patterns", BlockBanner.toNbt(patterns));
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

    public List<Pattern> getPatterns() {
        // TODO: Defensive copy?
        return patterns;
    }

    public void setPatterns(List<Pattern> patterns) {
        // TODO: Defensive copy
        this.patterns = patterns;
    }
}
