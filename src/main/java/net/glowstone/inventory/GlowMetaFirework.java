package net.glowstone.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.glowstone.util.nbt.CompoundTag;
import org.apache.commons.lang.Validate;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GlowMetaFirework extends GlowMetaItem implements FireworkMeta {

    private List<FireworkEffect> effects = new ArrayList<>();
    private int power;

    public GlowMetaFirework(GlowMetaItem meta) {
        super(meta);
        if (meta == null || !(meta instanceof GlowMetaFirework)) return;

        GlowMetaFirework firework = (GlowMetaFirework) meta;
        effects.addAll(firework.effects);
        power = firework.power;
    }

    @Override
    public boolean isApplicable(Material material) {
        return material == Material.FIREWORK;
    }

    @Override
    public GlowMetaFirework clone() {
        return new GlowMetaFirework(this);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = super.serialize();
        result.put("meta-type", "FIREWORK");

        result.put("power", power);

        if (hasEffects()) {
            List<Object> effects = new ArrayList<>();
            for (FireworkEffect effect : this.effects) {
                effects.add(effect.serialize());
            }
            result.put("effects", effects);
        }

        return result;
    }

    @Override
    void writeNbt(CompoundTag tag) {
        CompoundTag firework = new CompoundTag();
        tag.putCompound("Fireworks", firework);
        firework.putByte("Flight", power);

        List<CompoundTag> explosions = new ArrayList<>();
        if (hasEffects()) {
            for (FireworkEffect effect : effects) {
                explosions.add(GlowMetaFireworkEffect.toExplosion(effect));
            }
        }
        firework.putCompoundList("Explosions", explosions);
    }

    @Override
    void readNbt(CompoundTag tag) {
        CompoundTag firework = tag.getCompound("Fireworks");
        power = firework.getByte("Flight");

        List<CompoundTag> explosions = firework.getCompoundList("Explosions");
        for (CompoundTag explosion : explosions) {
            effects.add(GlowMetaFireworkEffect.toEffect(explosion));
        }
    }

    @Override
    public void addEffect(FireworkEffect effect) {
        Validate.notNull(effect, "Effect cannot be null.");

        effects.add(effect);
    }

    @Override
    public void addEffects(FireworkEffect... effects) {
        Validate.notNull(effects, "Effects cannot be null.");
        Validate.noNullElements(effects, "Null element in effects.");

        this.effects.addAll(Arrays.asList(effects));
    }

    @Override
    public void addEffects(Iterable<FireworkEffect> effects) {
        addEffects(Iterables.toArray(effects, FireworkEffect.class));
    }

    @Override
    public List<FireworkEffect> getEffects() {
        return ImmutableList.copyOf(effects);
    }

    @Override
    public int getEffectsSize() {
        return effects.size();
    }

    @Override
    public void removeEffect(int index) {
        effects.remove(index);
    }

    @Override
    public void clearEffects() {
        effects.clear();
    }

    @Override
    public boolean hasEffects() {
        return !effects.isEmpty();
    }

    @Override
    public int getPower() {
        return power;
    }

    @Override
    public void setPower(int power) {
        Validate.isTrue(power >= 0 && power <= 128, "Power must be 0-128, inclusive");

        this.power = power;
    }
}
