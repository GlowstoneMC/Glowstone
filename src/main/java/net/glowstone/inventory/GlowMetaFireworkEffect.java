package net.glowstone.inventory;

import com.google.common.primitives.Ints;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import net.glowstone.util.nbt.CompoundTag;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class GlowMetaFireworkEffect extends GlowMetaItem implements FireworkEffectMeta {

    @Getter
    @Setter
    private FireworkEffect effect;

    /**
     * Creates an instance by copying from the given {@link ItemMeta}. If that item is another
     * {@link FireworkEffectMeta}, it is copied fully; otherwise, the {@link FireworkEffect} is
     * null.
     * @param meta the {@link ItemMeta} to copy
     */
    public GlowMetaFireworkEffect(ItemMeta meta) {
        super(meta);

        if (meta instanceof FireworkEffectMeta) {
            effect = ((FireworkEffectMeta) meta).getEffect();
        }
    }

    static FireworkEffect toEffect(CompoundTag explosion) {
        boolean flicker = false;
        boolean trail = false;
        Type type;
        List<Color> colors = new ArrayList<>();
        List<Color> fadeColors = new ArrayList<>();

        int[] colorInts = explosion.getIntArray("Colors");
        for (int color : colorInts) {
            colors.add(Color.fromRGB(color));
        }

        type = Type.values()[explosion.getByte("Type")];

        if (explosion.isByte("Flicker")) {
            flicker = explosion.getBool("Flicker");
        }
        if (explosion.isByte("Trail")) {
            trail = explosion.getBool("Trail");
        }

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

        if (effect.hasFlicker()) {
            explosion.putBool("Flicker", true);
        }
        if (effect.hasTrail()) {
            explosion.putBool("Trail", true);
        }

        explosion.putByte("Type", effect.getType().ordinal());

        List<Color> colors = effect.getColors();
        List<Integer> colorInts = colors.stream().map(Color::asRGB).collect(Collectors.toList());
        explosion.putIntArray("Colors", Ints.toArray(colorInts));

        List<Color> fade = effect.getFadeColors();
        if (!fade.isEmpty()) {
            List<Integer> fadeInts = fade.stream().map(Color::asRGB).collect(Collectors.toList());
            explosion.putIntArray("FadeColors", Ints.toArray(fadeInts));
        }

        return explosion;
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
            effect = toEffect(tag.getCompound("Explosion"));
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
    public boolean hasEffect() {
        return effect != null;
    }
}
