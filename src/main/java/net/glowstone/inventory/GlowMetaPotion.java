package net.glowstone.inventory;

import com.google.common.collect.ImmutableList;
import net.glowstone.util.nbt.CompoundTag;
import net.glowstone.util.nbt.TagType;
import org.apache.commons.lang3.Validate;
import org.bukkit.Material;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GlowMetaPotion extends GlowMetaItem implements PotionMeta {

    List<PotionEffect> effects = new ArrayList<>();

    public GlowMetaPotion(GlowMetaItem meta) {
        super(meta);
        if (meta == null || !(meta instanceof GlowMetaPotion)) return;

        GlowMetaPotion potion = (GlowMetaPotion) meta;
        effects.addAll(potion.effects);
    }

    @Override
    public boolean isApplicable(Material material) {
        return material == Material.POTION;
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
        Validate.notNull(effect, "PotionEffect cannot be null.");

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

    public static PotionEffect fromNBT(CompoundTag tag) {
        PotionEffectType type = PotionEffectType.getById(tag.getByte("Id"));
        int duration = tag.getInt("Duration");
        int amplifier = tag.getByte("Amplifier");
        boolean ambient = tag.isByte("Ambient") ? tag.getBool("Ambient") : false;
        boolean particles = tag.isByte("ShowParticles") ? tag.getBool("ShowParticles") : true;

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
}
