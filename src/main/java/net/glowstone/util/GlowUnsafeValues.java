package net.glowstone.util;

import java.util.ArrayList;
import java.util.List;
import net.glowstone.GlowServer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Statistic;
import org.bukkit.UnsafeValues;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;

/**
 * Implementation of Bukkit's internal-use UnsafeValues.
 *
 * <p>In CraftBukkit, this uses Mojang identifiers, but here we just stick to Bukkit's.
 *
 * <p>The implementation may be a bit sketchy but this isn't a problem since the behavior of this
 * class isn't strictly specified.
 */
@Deprecated
public final class GlowUnsafeValues implements UnsafeValues {

    @Deprecated
    public Material getMaterialFromInternalName(String name) {
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @Deprecated
    public List<String> tabCompleteInternalMaterialName(String token, List<String> completions) {
        List<String> materialNames = new ArrayList<>(Material.values().length);
        for (Material mat : Material.values()) {
            materialNames.add(mat.name().toLowerCase());
        }
        return StringUtil.copyPartialMatches(token, materialNames, completions);
    }

    @Override
    public Material toLegacy(Material material) {
        // TODO: 1.13
        return null;
    }

    @Override
    public Material fromLegacy(Material material) {
        // TODO: 1.13
        return null;
    }

    @Override
    public Material fromLegacy(MaterialData materialData) {
        // TODO: 1.13
        return null;
    }

    @Override
    public Material fromLegacy(MaterialData material, boolean itemPriority) {
        // TODO: 1.13
        return null;
    }

    @Override
    public BlockData fromLegacy(Material material, byte b) {
        // TODO: 1.13
        return null;
    }

    /**
     * Converts a numerical ID to a material type.
     *
     * <p>Should only be used for network protocol.</p>
     *
     * @param id the numerical ID of the material
     * @return the material
     */
    public Material fromId(int id) {
        // TODO: 1.13
        return null;
    }

    @Override
    public int getDataVersion() {
        return GlowServer.DATA_VERSION;
    }

    @Override
    public ItemStack modifyItemStack(ItemStack stack, String arguments) {
        return stack;
    }

    @Override
    public void checkSupported(PluginDescriptionFile pluginDescriptionFile) {
        // TODO: 1.13
    }

    @Override
    public byte[] processClass(PluginDescriptionFile pdf, String path, byte[] clazz) {
        // TODO: 1.13
        return new byte[0];
    }

    @Deprecated
    public Statistic getStatisticFromInternalName(String name) {
        try {
            return Statistic.valueOf(name);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @Deprecated
    public List<String> tabCompleteInternalStatistic(
            String token, List<String> completions) {
        Statistic[] stats = Statistic.values();
        List<String> names = new ArrayList<>(stats.length);
        for (Statistic stat : stats) {
            names.add(stat.name());
        }
        return StringUtil.copyPartialMatches(token, names, completions);
    }

    @Override
    public Advancement loadAdvancement(NamespacedKey key, String advancement) {
        return null;
    }

    @Override
    public boolean removeAdvancement(NamespacedKey key) {
        return false;
    }
}
