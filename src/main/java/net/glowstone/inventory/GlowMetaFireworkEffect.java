package net.glowstone.inventory;

import com.google.common.primitives.Ints;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.meta.FireworkEffectMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GlowMetaFireworkEffect extends GlowMetaItem implements FireworkEffectMeta {

    private FireworkEffect effect;

    public GlowMetaFireworkEffect(GlowMetaItem meta) {
        super(meta);

        if (meta == null || !(meta instanceof GlowMetaFireworkEffect)) return;

        GlowMetaFireworkEffect effect = (GlowMetaFireworkEffect) meta;
        this.effect = effect.effect;
    }

    @Override
    public boolean isApplicable(Material material) {
        return material == Material.FIREWORK_CHARGE;
    }

    @Override
    public GlowMetaFireworkEffect clone() {
        return new GlowMetaFireworkEffect(this);
    }

    @Override
    void writeNbt(CompoundTag tag) {
        super.writeNbt(tag);

        if (hasEffect()) {
            tag.putCompound("Explosion", toExplosion(effect));
        }
    }

    @Override
    void readNbt(CompoundTag tag) {
        super.readNbt(tag);

        if (tag.isCompound("Explosion")) {
            this.effect = toEffect(tag.getCompound("Explosion"));
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = super.serialize();
        result.put("meta-type", "CHARGE");

        if (hasEffect()) {
            result.put("effect", effect.serialize());
        }

        return result;
    }

    @Override
    public void setEffect(FireworkEffect effect) {
        this.effect = effect;
    }

    @Override
    public boolean hasEffect() {
        return effect != null;
    }

    @Override
    public FireworkEffect getEffect() {
        return effect;
    }

    static FireworkEffect toEffect(CompoundTag explosion) {
        boolean flicker = false;
        boolean trail = false;
        FireworkEffect.Type type;
        List<Color> colors = new ArrayList<>();
        List<Color> fadeColors = new ArrayList<>();

        int[] colorInts = explosion.getIntArray("Colors");
        for (int color : colorInts) {
            colors.add(Color.fromRGB(color));
        }

        type = FireworkEffect.Type.values()[explosion.getByte("Type")];

        if (explosion.isByte("Flicker")) flicker = explosion.getBool("Flicker");
        if (explosion.isByte("Trail")) trail = explosion.getBool("Trail");

        if (explosion.isIntArray("FadeColors")) {
            int[] fadeInts = explosion.getIntArray("FadeColors");
            for (int fade : fadeInts) {
                fadeColors.add(Color.fromRGB(fade));
            }
        }

        return FireworkEffect.builder()
                .flicker(flicker)
                .trail(trail)
                .with(type)
                .withColor(colors)
                .withFade(fadeColors)
                .build();
    }

    static CompoundTag toExplosion(FireworkEffect effect) {
        CompoundTag explosion = new CompoundTag();

        if (effect.hasFlicker()) explosion.putBool("Flicker", true);
        if (effect.hasTrail()) explosion.putBool("Trail", true);

        explosion.putByte("Type", effect.getType().ordinal());

        List<Color> colors = effect.getColors();
        List<Integer> colorInts = new ArrayList<>();
        for (Color color : colors) {
            colorInts.add(color.asRGB());
        }
        explosion.putIntArray("Colors", Ints.toArray(colorInts));

        List<Color> fade = effect.getFadeColors();
        if (!fade.isEmpty()) {
            List<Integer> fadeInts = new ArrayList<>();
            for (Color color : colors) {
                fadeInts.add(color.asRGB());
            }
            explosion.putIntArray("FadeColors", Ints.toArray(fadeInts));
        }

        return explosion;
    }
}
