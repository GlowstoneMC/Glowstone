package net.glowstone.block.blocktype;

import net.glowstone.GlowChunk;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.entity.TEBanner;
import net.glowstone.block.entity.TileEntity;
import net.glowstone.block.state.GlowBanner;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.block.banner.Pattern;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.material.Banner;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class BlockBanner extends BlockType {

    public BlockBanner() {
        setDrops(new ItemStack(Material.BANNER));
    }

    @Override
    public Collection<ItemStack> getDrops(GlowBlock block, ItemStack tool) {
        GlowBanner state = (GlowBanner) block.getState();
        ItemStack drop = new ItemStack(Material.BANNER, 1);
        BannerMeta meta = (BannerMeta) drop.getItemMeta();
        meta.setPatterns(state.getPatterns());
        drop.setItemMeta(meta);
        drop.setDurability(state.getBaseColor().getDyeData());

        return Arrays.asList(drop);
    }

    @Override
    public TileEntity createTileEntity(GlowChunk chunk, int cx, int cy, int cz) {
        return new TEBanner(chunk.getBlock(cx, cy, cz));
    }

    @Override
    public void placeBlock(GlowPlayer player, GlowBlockState state, BlockFace face, ItemStack holding, Vector clickedLoc) {
        super.placeBlock(player, state, face, holding, clickedLoc);
        MaterialData data = state.getData();
        if (!(data instanceof Banner)) {
            warnMaterialData(Banner.class, data);
            return;
        }
        Banner banner = (Banner) data;
        if (banner.isWallBanner()) {
            banner.setFacingDirection(face);
        } else {
            banner.setFacingDirection(player.getFacing().getOppositeFace());
        }
    }

    @Override
    public void afterPlace(GlowPlayer player, GlowBlock block, ItemStack holding, GlowBlockState oldState) {
        GlowBanner banner = (GlowBanner) block.getState();
        banner.setBaseColor(DyeColor.getByDyeData((byte) holding.getDurability()));
        BannerMeta meta = (BannerMeta) holding.getItemMeta();
        meta.setPatterns(meta.getPatterns());
        banner.update();
    }

    public static List<CompoundTag> toNBT(List<Pattern> banner) {
        List<CompoundTag> patterns = new ArrayList<>();
        for (Pattern pattern : banner) {
            CompoundTag layerTag = new CompoundTag();
            layerTag.putString("Pattern", pattern.getPattern().getIdentifier());
            layerTag.putInt("Color", pattern.getColor().getDyeData());
            patterns.add(layerTag);
        }
        return patterns;
    }

    public static List<Pattern> fromNBT(List<CompoundTag> tag) {
        List<Pattern> banner = new ArrayList<>();
        for (CompoundTag layer : tag) {
            PatternType patternType = PatternType.getByIdentifier(layer.getString("Pattern"));
            DyeColor color = DyeColor.getByDyeData((byte) layer.getInt("Color"));

            banner.add(new Pattern(color, patternType));
        }
        return banner;
    }

}
