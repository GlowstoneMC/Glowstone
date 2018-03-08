package net.glowstone.inventory;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.StringTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * An implementation of {@link ItemMeta}, created through {@link GlowItemFactory}.
 */
public class GlowMetaItem implements ItemMeta {

    @Getter
    @Setter
    private String displayName;
    private List<String> lore;
    private Map<Enchantment, Integer> enchants;
    private int hideFlag;
    @Getter
    @Setter
    private boolean unbreakable;

    /**
     * Create a GlowMetaItem, copying from another if possible.
     *
     * @param meta The meta to copy from, or null.
     */
    public GlowMetaItem(ItemMeta meta) {
        if (meta == null) {
            return;
        }

        displayName = meta.getDisplayName();

        if (meta.hasLore()) {
            lore = new ArrayList<>(meta.getLore());
        }
        if (meta.hasEnchants()) {
            enchants = new HashMap<>(meta.getEnchants());
        }
        if (meta instanceof GlowMetaItem) {
            hideFlag = ((GlowMetaItem) meta).hideFlag;
        } else {
            for (ItemFlag flag : meta.getItemFlags()) {
                addItemFlags(flag);
            }
        }
    }

    protected static void serializeEnchants(String name, Map<String, Object> map,
        Map<Enchantment, Integer> enchants) {
        Map<String, Object> enchantList = new HashMap<>();

        for (Entry<Enchantment, Integer> enchantment : enchants.entrySet()) {
            enchantList.put(enchantment.getKey().getName(), enchantment.getValue());
        }

        map.put(name, enchantList);
    }

    protected static void writeNbtEnchants(String name, CompoundTag to,
        Map<Enchantment, Integer> enchants) {
        List<CompoundTag> ench = new ArrayList<>();

        for (Entry<Enchantment, Integer> enchantment : enchants.entrySet()) {
            CompoundTag enchantmentTag = new CompoundTag();
            enchantmentTag.putShort("id", enchantment.getKey().getId());
            enchantmentTag.putShort("lvl", enchantment.getValue());
            ench.add(enchantmentTag);
        }

        to.putCompoundList(name, ench);
    }

    protected static Map<Enchantment, Integer> readNbtEnchants(String name, CompoundTag tag) {
        Map<Enchantment, Integer> result = null;

        if (tag.isList(name, TagType.COMPOUND)) {
            Iterable<CompoundTag> enchs = tag.getCompoundList(name);
            for (CompoundTag enchantmentTag : enchs) {
                if (enchantmentTag.isShort("id") && enchantmentTag.isShort("lvl")) {
                    Enchantment enchantment = Enchantment.getById(enchantmentTag.getShort("id"));
                    if (result == null) {
                        result = new HashMap<>(4);
                    }
                    result.put(enchantment, (int) enchantmentTag.getShort("lvl"));
                }
            }
        }

        return result;
    }

    /**
     * Check whether this ItemMeta can be applied to the given material.
     *
     * @param material The Material.
     * @return True if this ItemMeta is applicable.
     */
    public boolean isApplicable(Material material) {
        return material != Material.AIR;
    }

    @Override
    public Spigot spigot() {
        return new Spigot() {
            @Override
            public boolean isUnbreakable() {
                return GlowMetaItem.this.isUnbreakable();
            }

            @Override
            public void setUnbreakable(boolean unbreakable) {
                GlowMetaItem.this.setUnbreakable(unbreakable);
            }
        };
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("meta-type", "UNSPECIFIC");
        result.put("unbreakable", isUnbreakable());

        if (hasDisplayName()) {
            result.put("display-name", getDisplayName());
        }
        if (hasLore()) {
            result.put("lore", getLore());
        }

        if (hasEnchants()) {
            serializeEnchants("enchants", result, getEnchants());
        }

        if (hideFlag != 0) {
            Set<String> hideFlags = getItemFlags().stream().map(Enum::name)
                .collect(Collectors.toSet());
            if (hideFlags.isEmpty()) {
                result.put("ItemFlags", hideFlags);
            }
        }

        return result;
    }

    void writeNbt(CompoundTag tag) {
        CompoundTag displayTags = new CompoundTag();
        if (hasDisplayName()) {
            displayTags.putString("Name", getDisplayName());
        }
        if (hasLore()) {
            displayTags.putList("Lore", TagType.STRING, getLore(), StringTag::new);
        }

        if (!displayTags.isEmpty()) {
            tag.putCompound("display", displayTags);
        }

        if (hasEnchants()) {
            writeNbtEnchants("ench", tag, enchants);
        }

        if (hideFlag != 0) {
            tag.putInt("HideFlags", hideFlag);
        }
        tag.putBool("Unbreakable", isUnbreakable());
    }

    void readNbt(CompoundTag tag) {
        if (tag.isCompound("display")) {
            CompoundTag display = tag.getCompound("display");
            if (display.isString("Name")) {
                setDisplayName(display.getString("Name"));
            }
            if (display.isList("Lore", TagType.STRING)) {
                setLore(display.getList("Lore", TagType.STRING));
            }
        }

        //TODO currently ignoring level restriction, is that right?
        Map<Enchantment, Integer> tagEnchants = readNbtEnchants("ench", tag);
        if (tagEnchants != null) {
            if (enchants == null) {
                enchants = tagEnchants;
            } else {
                enchants.putAll(tagEnchants);
            }
        }

        if (tag.isInt("HideFlags")) {
            hideFlag = tag.getInt("HideFlags");
        }
        if (tag.isByte("Unbreakable")) {
            unbreakable = tag.getBool("Unbreakable");
        }
    }

    @Override
    public String toString() {
        Map<String, Object> map = serialize();
        return map.get("meta-type") + "_META:" + map;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Basic properties

    @Override
    public boolean hasDisplayName() {
        return !Strings.isNullOrEmpty(displayName);
    }

    // TODO: support localization

    @Override
    public boolean hasLocalizedName() {
        return hasDisplayName();
    }

    @Override
    public String getLocalizedName() {
        return getDisplayName();
    }

    @Override
    public void setLocalizedName(String name) {
        displayName = name;
    }

    @Override
    public boolean hasLore() {
        return lore != null && !lore.isEmpty();
    }

    @Override
    public List<String> getLore() {
        // TODO: Defensive copy
        return lore;
    }

    @Override
    public void setLore(List<String> lore) {
        // todo: fancy validation things
        // TODO: Defensive copy
        this.lore = lore;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Enchants

    @Override
    public boolean hasEnchants() {
        return enchants != null && !enchants.isEmpty();
    }

    @Override
    public boolean hasEnchant(Enchantment ench) {
        return hasEnchants() && enchants.containsKey(ench);
    }

    @Override
    public int getEnchantLevel(Enchantment ench) {
        return hasEnchant(ench) ? enchants.get(ench) : 0;
    }

    @Override
    public Map<Enchantment, Integer> getEnchants() {
        return hasEnchants() ? Collections.unmodifiableMap(enchants) : Collections.emptyMap();
    }

    @Override
    public boolean addEnchant(Enchantment ench, int level, boolean ignoreLevelRestriction) {
        if (enchants == null) {
            enchants = new HashMap<>(4);
        }

        if (ignoreLevelRestriction || level >= ench.getStartLevel() && level <= ench
            .getMaxLevel()) {
            Integer old = enchants.put(ench, level);
            return old == null || old != level;
        }
        return false;
    }

    @Override
    public boolean removeEnchant(Enchantment ench) {
        return hasEnchants() && enchants.remove(ench) != null;
    }

    @Override
    public boolean hasConflictingEnchant(Enchantment ench) {
        if (!hasEnchants()) {
            return false;
        }

        for (Enchantment e : enchants.keySet()) {
            if (e.conflictsWith(ench)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ItemMeta clone() {
        return new GlowMetaItem(this);
    }

    @Override
    public void addItemFlags(ItemFlag... itemFlags) {
        for (ItemFlag itemFlag : itemFlags) {
            hideFlag |= getBitModifier(itemFlag);
        }
    }

    @Override
    public void removeItemFlags(ItemFlag... itemFlags) {
        for (ItemFlag itemFlag : itemFlags) {
            hideFlag &= ~getBitModifier(itemFlag);
        }
    }

    @Override
    public Set<ItemFlag> getItemFlags() {
        Set<ItemFlag> currentFlags = EnumSet.noneOf(ItemFlag.class);
        ItemFlag[] values;
        for (int length = (values = ItemFlag.values()).length, i = 0; i < length; ++i) {
            ItemFlag f = values[i];
            if (hasItemFlag(f)) {
                currentFlags.add(f);
            }
        }
        return currentFlags;
    }

    @Override
    public boolean hasItemFlag(ItemFlag itemFlag) {
        int bitModifier = getBitModifier(itemFlag);
        return (hideFlag & bitModifier) == bitModifier;
    }

    private byte getBitModifier(ItemFlag hideFlag) {
        return (byte) (1 << hideFlag.ordinal());
    }
}
