package net.glowstone.inventory;

import com.google.common.collect.ImmutableList;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public class GlowMetaPotion extends GlowMetaItem implements PotionMeta {

    PotionData potion;
    List<PotionEffect> effects = new ArrayList<>();

    public GlowMetaPotion(GlowMetaItem meta) {
        super(meta);
        if (!(meta instanceof GlowMetaPotion)) return;

        GlowMetaPotion potion = (GlowMetaPotion) meta;
        effects.addAll(potion.effects);
        this.potion = potion.potion;
    }

    public static PotionEffect fromNBT(CompoundTag tag) {
        PotionEffectType type = PotionEffectType.getById(tag.getByte("Id"));
        int duration = tag.getInt("Duration");
        int amplifier = tag.getByte("Amplifier");
        boolean ambient = tag.isByte("Ambient") && tag.getBool("Ambient");
        boolean particles = !tag.isByte("ShowParticles") || tag.getBool("ShowParticles");

        return new PotionEffect(type, duration, amplifier, ambient, particles);
    }

    public static CompoundTag toNBT(PotionEffect effect) {
        CompoundTag tag = new CompoundTag();

        tag.putByte("Id", effect.getType().getId());
        tag.putInt("Duration", effect.getDuration());
        tag.putByte("Amplifier", effect.getAmplifier());
        tag.putBool("Ambient", effect.isAmbient());
        tag.putBool("ShowParticles", effect.hasParticles());

        return tag;
    }

    @Override
    public boolean isApplicable(Material material) {
        return material == Material.POTION || material == Material.SPLASH_POTION || material == Material.TIPPED_ARROW || material == Material.LINGERING_POTION;
    }

    @Override
    public GlowMetaPotion clone() {
        return new GlowMetaPotion(this);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = super.serialize();
        result.put("meta-type", "POTION");

        if (hasCustomEffects()) {
            result.put("custom-effects", getCustomEffects());
        }

        return result;
    }

    @Override
    void writeNbt(CompoundTag tag) {
        super.writeNbt(tag);

        if (hasCustomEffects()) {
            List<CompoundTag> customEffects = effects.stream().map(GlowMetaPotion::toNBT).collect(Collectors.toList());
            tag.putCompoundList("CustomEffects", customEffects);
        }
        tag.putString("Potion", dataToString());
    }

    @Override
    void readNbt(CompoundTag tag) {
        super.readNbt(tag);

        if (tag.isList("CustomEffects", TagType.COMPOUND)) {
            List<CompoundTag> customEffects = tag.getCompoundList("CustomEffects");
            for (CompoundTag effect : customEffects) {
                addCustomEffect(fromNBT(effect), true);
            }
        }
        if (tag.isString("Potion")) {
            this.potion = dataFromString(tag.getString("Potion"));
        }
    }

    @Override
    public void setBasePotionData(PotionData potionData) {
        this.potion = potionData;
    }

    @Override
    public PotionData getBasePotionData() {
        return this.potion;
    }

    @Override
    public boolean hasCustomEffects() {
        return !effects.isEmpty();
    }

    @Override
    public List<PotionEffect> getCustomEffects() {
        return ImmutableList.copyOf(effects);
    }

    @Override
    public boolean addCustomEffect(PotionEffect effect, boolean overwrite) {
        checkNotNull(effect, "PotionEffect cannot be null.");

        for (PotionEffect eff : effects) {
            if (eff.getType() == effect.getType() && !overwrite) return false;
        }

        effects.add(effect);
        return true;
    }

    @Override
    public boolean removeCustomEffect(PotionEffectType type) {
        Iterator<PotionEffect> it = effects.iterator();

        while (it.hasNext()) {
            PotionEffect effect = it.next();
            if (effect.getType() == type) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasCustomEffect(PotionEffectType type) {
        for (PotionEffect effect : effects) {
            if (effect.getType() == type) return true;
        }

        return false;
    }

    @Override
    public boolean setMainEffect(PotionEffectType type) {
        PotionEffect main = null;
        for (PotionEffect effect : effects) {
            if (effect.getType() == type) {
                if (effects.indexOf(effect) == 0) return false;
                main = effect;
                effects.remove(effect);
                break;
            }
        }
        if (main == null) return false;

        effects.add(0, main);
        return true;
    }

    @Override
    public boolean clearCustomEffects() {
        if (effects.isEmpty()) return false;
        effects.clear();
        return true;
    }

    @Override
    public boolean hasColor() {
        return false;
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public void setColor(Color color) {

    }

    /**
     * Converts the PotionData of this item meta to a Potion ID string
     *
     * @return the Potion ID string
     */
    private String dataToString() {
        String name = "minecraft:";
        if (potion.isExtended()) {
            name += "long_";
        } else if (potion.isUpgraded()) {
            name += "strong_";
        }
        return name + PotionTypeTable.toName(potion.getType());
    }

    /**
     * Converts a Potion ID string to the PotionData of this item meta.
     *
     * @param string the Potion ID string
     * @return the resultant PotionData
     */
    private PotionData dataFromString(String string) {
        PotionType type;
        boolean extended = false, upgraded = false;
        if (string.startsWith("minecraft:"))
            string = string.replace("minecraft:", "");
        if (string.startsWith("long_")) {
            string = string.replace("long_", "");
            extended = true;
        } else if (string.startsWith("strong_")) {
            string = string.replace("strong_", "");
            upgraded = true;
        }
        type = PotionTypeTable.fromName(string);
        return new PotionData(type, extended, upgraded);
    }

    /**
     * Conversion for Bukkit Potion names to Vanilla Potion names.
     */
    enum PotionTypeTable {
        EMPTY(PotionType.UNCRAFTABLE, "empty"),
        LEAPING(PotionType.JUMP, "leaping"),
        SWIFTNESS(PotionType.SPEED, "swiftness"),
        HEALING(PotionType.INSTANT_HEAL, "healing"),
        HARMING(PotionType.INSTANT_DAMAGE, "harming"),
        REGENERATION(PotionType.REGEN, "regeneration");

        PotionType type;
        String name;

        PotionTypeTable(PotionType type, String name) {
            this.type = type;
            this.name = name;
        }

        /**
         * Converts a Vanilla Potion ID to an equivalent Bukkit PotionType
         *
         * @param name the Vanilla Potion ID
         * @return the PotionType equivalent
         */
        static PotionType fromName(String name) {
            for (PotionTypeTable table : values()) {
                if (name.equalsIgnoreCase(table.name))
                    return table.type;
            }
            return PotionType.valueOf(name.toUpperCase());
        }

        /**
         * Converts a Bukkit PotionType to an equivalent Vanilla Potion ID
         *
         * @param type the Bukkit PotionType
         * @return the Vanilla Potion ID equivalent
         */
        static String toName(PotionType type) {
            for (PotionTypeTable table : values()) {
                if (type == table.type)
                    return table.name;
            }
            return type.name().toLowerCase();
        }
    }
}
