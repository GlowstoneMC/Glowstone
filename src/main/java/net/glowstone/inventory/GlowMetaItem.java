package net.glowstone.inventory;

import com.google.common.base.Strings;
import net.glowstone.util.nbt.*;
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
            this.lore = new ArrayList<String>(meta.lore);
        }
        if (meta.hasEnchants()) {
            this.enchants = new HashMap<Enchantment, Integer>(meta.enchants);
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

    public ItemMeta clone() {
        try {
            GlowMetaItem clone = (GlowMetaItem) super.clone();
            if (this.lore != null) {
                clone.lore = new ArrayList<String>(this.lore);
            }
            if (this.enchants != null) {
                clone.enchants = new HashMap<Enchantment, Integer>(this.enchants);
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("meta-type", "UNSPECIFIC");

        if (hasDisplayName()) {
            result.put("display-name", getDisplayName());
        }
        if (hasLore()) {
            result.put("lore", getLore());
        }
        // todo: enchantments

        return result;
    }

    List<Tag> writeNbt() {
        List<Tag> tags = new LinkedList<>();

        List<Tag> displayTags = new LinkedList<>();
        if (hasDisplayName()) {
            displayTags.add(new StringTag("Name", getDisplayName()));
        }
        if (hasLore()) {
            List<String> lore = getLore();
            List<StringTag> loreList = new ArrayList<StringTag>(lore.size());
            for (String line : lore) {
                loreList.add(new StringTag("", line));
            }
            displayTags.add(new ListTag<>("Lore", TagType.STRING, loreList));
        }

        if (displayTags.size() > 0) {
            tags.add(new CompoundTag("display", displayTags));
        }

        // todo: enchantments
        return tags;
    }

    void readNbt(CompoundTag tag) {
        if (tag.is("display", CompoundTag.class)) {
            CompoundTag display = tag.getTag("display", CompoundTag.class);
            if (display.is("Name", StringTag.class)) {
                setDisplayName(display.get("Name", StringTag.class));
            }
            if (display.is("Lore", ListTag.class)) {
                List<StringTag> loreList = display.getList("Lore", StringTag.class);
                List<String> lore = new ArrayList<>();
                for (StringTag line : loreList) {
                    lore.add(line.getValue());
                }
                setLore(lore);
            }
        }
        // todo: enchantments
    }

    @Override
    public String toString() {
        Map<String, Object> map = serialize();
        return map.get("meta-type") + "_META:" + map;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Basic properties

    public boolean hasDisplayName() {
        return !Strings.isNullOrEmpty(displayName);
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String name) {
        displayName = name;
    }

    public boolean hasLore() {
        return lore != null && !lore.isEmpty();
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        // in the future, fancy validation things
        this.lore = lore;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Enchants

    public boolean hasEnchants() {
        return enchants != null && !enchants.isEmpty();
    }

    public boolean hasEnchant(Enchantment ench) {
        return hasEnchants() && enchants.containsKey(ench);
    }

    public int getEnchantLevel(Enchantment ench) {
        return hasEnchant(ench) ? enchants.get(ench) : 0;
    }

    public Map<Enchantment, Integer> getEnchants() {
        return hasEnchants() ? Collections.unmodifiableMap(enchants) : Collections.<Enchantment, Integer>emptyMap();
    }

    public boolean addEnchant(Enchantment ench, int level, boolean ignoreLevelRestriction) {
        if (enchants == null) {
            enchants = new HashMap<Enchantment, Integer>(4);
        }

        if (ignoreLevelRestriction || level >= ench.getStartLevel() && level <= ench.getMaxLevel()) {
            Integer old = enchants.put(ench, level);
            return old == null || old != level;
        }
        return false;
    }

    public boolean removeEnchant(Enchantment ench) {
        return hasEnchants() && enchants.remove(ench) != null;
    }

    public boolean hasConflictingEnchant(Enchantment ench) {
        return false;
    }
}
