package net.glowstone.inventory;

import com.google.common.base.Strings;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * An implementation of {@link ItemMeta}, created through {@link GlowItemFactory}.
 */
class GlowMetaItem implements ItemMeta {

    private String displayName;
    private List<String> lore;
    private Map<Enchantment, Integer> enchants;

    /**
     * Create a GlowMetaItem, copying from another if possible.
     * @param meta The meta to copy from, or null.
     */
    public GlowMetaItem(GlowMetaItem meta) {
        if (meta == null) {
            return;
        }

        displayName = meta.displayName;

        if (meta.hasLore()) {
            this.lore = new ArrayList<>(meta.lore);
        }
        if (meta.hasEnchants()) {
            this.enchants = new HashMap<>(meta.enchants);
        }
    }

    /**
     * Check whether this ItemMeta can be applied to the given material.
     * @param material The Material.
     * @return True if this ItemMeta is applicable.
     */
    public boolean isApplicable(Material material) {
        return material != Material.AIR;
    }

    @Override
    public ItemMeta clone() {
        return new GlowMetaItem(this);
    }

    protected static void serializeEnchants(String name, Map<String, Object> map, Map<Enchantment, Integer> enchants) {
        Map<String, Object> enchantList = new HashMap<>();

        for (Map.Entry<Enchantment, Integer> enchantment : enchants.entrySet()) {
            enchantList.put(enchantment.getKey().getName(), enchantment.getValue());
        }

        map.put(name, enchantList);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("meta-type", "UNSPECIFIC");

        if (hasDisplayName()) {
            result.put("display-name", getDisplayName());
        }
        if (hasLore()) {
            result.put("lore", getLore());
        }

        if (hasEnchants()) {
            serializeEnchants("enchants", result, getEnchants());
        }

        return result;
    }

    protected static void writeNbtEnchants(String name, CompoundTag to, Map<Enchantment, Integer> enchants) {
        List<CompoundTag> ench = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> enchantment : enchants.entrySet()) {
            CompoundTag enchantmentTag = new CompoundTag();
            enchantmentTag.putShort("id", enchantment.getKey().getId());
            enchantmentTag.putShort("lvl", enchantment.getValue());
            ench.add(enchantmentTag);
        }

        to.putCompoundList(name, ench);
    }

    void writeNbt(CompoundTag tag) {
        CompoundTag displayTags = new CompoundTag();
        if (hasDisplayName()) {
            displayTags.putString("Name", getDisplayName());
        }
        if (hasLore()) {
            displayTags.putList("Lore", TagType.STRING, getLore());
        }

        if (!displayTags.isEmpty()) {
            tag.putCompound("display", displayTags);
        }

        if (hasEnchants()) {
            writeNbtEnchants("ench", tag, enchants);
        }
    }

    protected static Map<Enchantment, Integer> readNbtEnchants(String name, CompoundTag tag) {
        Map<Enchantment, Integer> result = null;

        if (tag.isList(name, TagType.COMPOUND)) {
            Iterable<CompoundTag> enchs = tag.getCompoundList(name);
            for (CompoundTag enchantmentTag : enchs) {
                if (enchantmentTag.isShort("id") && enchantmentTag.isShort("lvl")) {
                    Enchantment enchantment = Enchantment.getById(enchantmentTag.getShort("id"));
                    if (result == null) result = new HashMap<>(4);
                    result.put(enchantment, (int) enchantmentTag.getShort("lvl"));
                }
            }
        }

        return result;
    }

    void readNbt(CompoundTag tag) {
        if (tag.isCompound("display")) {
            CompoundTag display = tag.getCompound("display");
            if (display.isString("Name")) {
                setDisplayName(display.getString("Name"));
            }
            if (display.isList("Lore", TagType.STRING)) {
                setLore(display.<String>getList("Lore", TagType.STRING));
            }
        }

        //TODO currently ignoring level restriction, is that right?
        Map<Enchantment, Integer> tagEnchants = readNbtEnchants("ench", tag);
        if (tagEnchants != null) {
            if (enchants == null)
                enchants = tagEnchants;
            else
                enchants.putAll(tagEnchants);
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

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String name) {
        displayName = name;
    }

    @Override
    public boolean hasLore() {
        return lore != null && !lore.isEmpty();
    }

    @Override
    public List<String> getLore() {
        return lore;
    }

    @Override
    public void setLore(List<String> lore) {
        // todo: fancy validation things
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
        return hasEnchants() ? Collections.unmodifiableMap(enchants) : Collections.<Enchantment, Integer>emptyMap();
    }

    @Override
    public boolean addEnchant(Enchantment ench, int level, boolean ignoreLevelRestriction) {
        if (enchants == null) {
            enchants = new HashMap<>(4);
        }

        if (ignoreLevelRestriction || level >= ench.getStartLevel() && level <= ench.getMaxLevel()) {
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
        if (!hasEnchants()) return false;

        for (Enchantment e : enchants.keySet()) {
            if (e.conflictsWith(ench))
                return true;
        }

        return false;
    }
}
