package net.glowstone.block.blocktype;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.glowstone.block.GlowBlock;
import net.glowstone.entity.GlowPlayer;
import net.glowstone.inventory.GlowItemFactory;
import net.glowstone.inventory.MaterialMatcher;
import net.glowstone.inventory.ToolType;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;

public class BlockCauldron extends BlockNeedsTool {

    private static final Collection<ItemStack> DROP = Arrays
        .asList(new ItemStack(Material.CAULDRON_ITEM));

    @Override
    public boolean blockInteract(GlowPlayer player, GlowBlock block, BlockFace face,
        Vector clickedLoc) {
        if (player.getItemInHand() == null) {
            return super.blockInteract(player, block, face, clickedLoc);
        }

        switch (player.getItemInHand().getType()) {
            case WATER_BUCKET:
                fillCauldron(player, block);
                return true;

            case GLASS_BOTTLE:
                fillBottle(player, block);
                return true;

            case LEATHER_BOOTS:
            case LEATHER_LEGGINGS:
            case LEATHER_CHESTPLATE:
            case LEATHER_HELMET:
                return bleachLeatherArmor(player, block);

            case BANNER:
                return bleachBanner(player, block);

            default:
                return super.blockInteract(player, block, face, clickedLoc);
        }
    }

    private void fillCauldron(GlowPlayer player, GlowBlock block) {
        if (block.getData() < 3) {
            if (player.getGameMode() != GameMode.CREATIVE) {
                player.getItemInHand().setType(Material.BUCKET);
            }

            block.setData((byte) 3);
        }
    }

    private void fillBottle(GlowPlayer player, GlowBlock block) {
        if (block.getData() > 0) {
            block.setData((byte) (block.getData() - 1));

            if (player.getGameMode() != GameMode.CREATIVE) {
                Map<Integer, ItemStack> drops = player.getInventory()
                    .addItem(new ItemStack(Material.POTION));
                if (!drops.isEmpty()) {
                    player.getWorld()
                        .dropItemNaturally(player.getLocation(), new ItemStack(Material.POTION));
                }

                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            }
        }
    }

    private boolean bleachBanner(GlowPlayer player, GlowBlock block) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return false;
        }

        if (block.getData() > 0) {
            ItemStack inHand = player.getItemInHand();
            BannerMeta meta = (BannerMeta) inHand.getItemMeta();
            List<Pattern> layers = meta.getPatterns();
            if (layers == null || layers.isEmpty()) {
                return false;
            }

            meta.setPatterns(layers);
            inHand.setItemMeta(meta);

            block.setData((byte) (block.getData() - 1));
            return true;
        } else {
            return false;
        }
    }

    private boolean bleachLeatherArmor(GlowPlayer player, GlowBlock block) {
        if (block.getData() > 0) {
            ItemStack inHand = player.getItemInHand();
            LeatherArmorMeta im = (LeatherArmorMeta) inHand.getItemMeta();
            im.setColor(GlowItemFactory.instance().getDefaultLeatherColor());
            inHand.setItemMeta(im);
            block.setData((byte) (block.getData() - 1));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Collection<ItemStack> getMinedDrops(GlowBlock block) {
        return DROP;
    }

    @Override
    protected MaterialMatcher getNeededMiningTool(GlowBlock block) {
        return ToolType.PICKAXE;
    }
}
