package net.glowstone.util;

import com.google.common.collect.Multimap;
import io.papermc.paper.inventory.ItemRarity;
import net.glowstone.GlowServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.RegionAccessor;
import org.bukkit.Registry;
import org.bukkit.Statistic;
import org.bukkit.UnsafeValues;
import org.bukkit.World;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.CreativeCategory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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
    public ComponentFlattener componentFlattener() {
        throw new UnsupportedOperationException("Adventure API is not yet supported.");
    }

    @Override
    public PlainComponentSerializer plainComponentSerializer() {
        return null;
    }

    @Override
    public PlainTextComponentSerializer plainTextSerializer() {
        return null;
    }

    @Override
    public GsonComponentSerializer gsonComponentSerializer() {
        return null;
    }

    @Override
    public GsonComponentSerializer colorDownsamplingGsonComponentSerializer() {
        return null;
    }

    @Override
    public LegacyComponentSerializer legacyComponentSerializer() {
        return LegacyComponentSerializer.builder().build();
    }

    @Override
    public Component resolveWithContext(Component component, CommandSender context, Entity scoreboardSubject, boolean bypassPermissions) throws IOException {
        return null;
    }

    @Override
    public void reportTimings() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Material toLegacy(Material material) {
        // TODO: 1.13
        GlowServer.logger.log(
                Level.WARNING,
                "Attempted to convert " + material + " from legacy. Not supported, ignoring.",
                new UnsupportedOperationException());
        return null;
    }

    @Override
    public Material fromLegacy(Material material) {
        // TODO: 1.13
        GlowServer.logger.log(
                Level.WARNING,
                "Attempted to convert " + material + " from legacy. Not supported, ignoring.",
                new UnsupportedOperationException());
        return null;
    }

    @Override
    public Material fromLegacy(MaterialData material) {
        // TODO: 1.13
        GlowServer.logger.log(
                Level.WARNING,
                "Attempted to convert " + material + " from legacy. Not supported, ignoring.",
                new UnsupportedOperationException());
        return null;
    }

    @Override
    public Material fromLegacy(MaterialData material, boolean itemPriority) {
        // TODO: 1.13
        GlowServer.logger.log(
                Level.WARNING,
                "Attempted to convert " + material + " from legacy. Not supported, ignoring.",
                new UnsupportedOperationException());
        return null;
    }

    @Override
    public BlockData fromLegacy(Material material, byte data) {
        // TODO: 1.13
        GlowServer.logger.log(
                Level.WARNING,
                "Attempted to convert " + material + ":" + data + " from legacy. Not supported, ignoring.",
                new UnsupportedOperationException());
        return null;
    }

    @Override
    public Material getMaterial(String material, int version) {
        if (version == getDataVersion()) {
            Material type = Material.getMaterial(material);
            if (type == null) {
                throw new RuntimeException("Couldn't find material: " + material);
            }
            return type;
        }
        // TODO: 1.13 support legacy materials
        throw new UnsupportedOperationException("Legacy material versions not supported!");
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

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(Material material, EquipmentSlot slot) {
        return null;
    }

    @Override
    public CreativeCategory getCreativeCategory(Material material) {
        return null;
    }

    @Override
    public String getTimingsServerName() {
        return "Glowstone";
    }

    @Override
    public boolean isSupportedApiVersion(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] serializeItem(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack deserializeItem(byte[] bytes) {
        throw new UnsupportedOperationException();
    }

    @Override
    public byte[] serializeEntity(Entity entity) {
        return new byte[0];
    }

    @Override
    public Entity deserializeEntity(byte[] data, World world, boolean preserveUUID) {
        return null;
    }

    @Override
    public String getTranslationKey(Material material) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTranslationKey(Block block) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTranslationKey(EntityType entityType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTranslationKey(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int nextEntityId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull <T extends Keyed> Registry<T> registryFor(Class<T> classOfT) {
        return null;
    }

    @Override
    public @NotNull String getMainLevelName() {
        return null;
    }

    @Override
    public ItemRarity getItemRarity(Material material) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemRarity getItemStackRarity(ItemStack itemStack) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValidRepairItemStack(@NotNull ItemStack itemStack,
                                          @NotNull ItemStack itemStack1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getItemAttributes(@NotNull Material material, @NotNull EquipmentSlot equipmentSlot) {
        return null;
    }

    @Override
    public int getProtocolVersion() {
        return GlowServer.PROTOCOL_VERSION;
    }

    @Override
    public boolean hasDefaultEntityAttributes(@NotNull NamespacedKey entityKey) {
        return false;
    }

    @Override
    public @NotNull Attributable getDefaultEntityAttributes(@NotNull NamespacedKey entityKey) {
        return null;
    }

    @Override
    public boolean isCollidable(@NotNull Material material) {
        return false;
    }

    @Override
    public @NotNull NamespacedKey getBiomeKey(RegionAccessor accessor, int x, int y, int z) {
        return null;
    }

    @Override
    public void setBiomeKey(RegionAccessor accessor, int x, int y, int z, NamespacedKey biomeKey) {

    }
}
