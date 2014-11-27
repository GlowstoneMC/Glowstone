package net.glowstone.inventory;

import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GlowMetaEnchantedBook extends GlowMetaItem implements EnchantmentStorageMeta {
    private Map<Enchantment, Integer> storedEnchants;

    public GlowMetaEnchantedBook(GlowMetaItem meta) {
        super(meta);

        if (meta == null || !(meta instanceof GlowMetaEnchantedBook)) return;

        GlowMetaEnchantedBook book = (GlowMetaEnchantedBook) meta;
        if (book.hasStoredEnchants()) {
            storedEnchants = new HashMap<>(book.storedEnchants);
        }
    }

    @Override
    public boolean isApplicable(Material material) {
        return material == Material.WRITTEN_BOOK;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = super.serialize();

        map.put("meta-type", "ENCHANTED");

        if (hasStoredEnchants()) {
            serializeEnchants("stored-enchants", map, storedEnchants);
        }

        return map;
    }

    @Override
    void writeNbt(CompoundTag tag) {
        super.writeNbt(tag);

        writeNbtEnchants("StoredEnchantments", tag, storedEnchants);
    }

    @Override
    void readNbt(CompoundTag tag) {
        super.readNbt(tag);

        //TODO currently ignoring level restriction, is that right?
        Map<Enchantment, Integer> enchants = readNbtEnchants("StoredEnchantments", tag);
        if (enchants != null) {
            if (storedEnchants == null)
                storedEnchants = enchants;
            else
                storedEnchants.putAll(enchants);
        }
    }

    @Override
    public boolean hasStoredEnchants() {
        return storedEnchants != null && !storedEnchants.isEmpty();
    }

    @Override
    public boolean hasStoredEnchant(Enchantment ench) {
        return hasStoredEnchants() && storedEnchants.containsKey(ench);
    }

    @Override
    public int getStoredEnchantLevel(Enchantment ench) {
        return hasStoredEnchant(ench) ? storedEnchants.get(ench) : 0;
    }

    @Override
    public Map<Enchantment, Integer> getStoredEnchants() {
        return hasStoredEnchants() ? Collections.unmodifiableMap(storedEnchants) : Collections.<Enchantment, Integer>emptyMap();
    }

    @Override
    public boolean addStoredEnchant(Enchantment ench, int level, boolean ignoreLevelRestriction) {
        if (storedEnchants == null) {
            storedEnchants = new HashMap<>(4);
        }

        if (ignoreLevelRestriction || level >= ench.getStartLevel() && level <= ench.getMaxLevel()) {
            Integer old = storedEnchants.put(ench, level);
            return old == null || old != level;
        }
        return false;
    }

    @Override
    public boolean removeStoredEnchant(Enchantment ench) throws IllegalArgumentException {
        return hasStoredEnchants() && storedEnchants.remove(ench) != null;
    }

    @Override
    public boolean hasConflictingStoredEnchant(Enchantment ench) {
        if (!hasStoredEnchants()) return false;

        for (Enchantment e : storedEnchants.keySet()) {
            if (e.conflictsWith(ench))
                return true;
        }

        return false;
    }

    @Override
    public GlowMetaEnchantedBook clone() {
        return (GlowMetaEnchantedBook) super.clone();
    }
}
