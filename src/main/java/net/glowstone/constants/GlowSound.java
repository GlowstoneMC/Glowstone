package net.glowstone.constants;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

import java.util.HashMap;
import java.util.Map;

public class GlowSound {

    private static final Map<String, SoundCategory> SOUNDS = new HashMap<>();
    private static final ImmutableMap<String, Sound> VANILLA_SOUNDS;

    static {
        ImmutableMap.Builder<String, Sound> vanillaSounds = ImmutableMap.builder();
        // register vanilla sounds
        // as of 1.11, sounds do not have a default category
        // instead, the category of the sound playing is determined by the source of the sound
        for (Sound sound : Sound.values()) {
            String vanillaId = getVanillaId(sound);
            reg(vanillaId, SoundCategory.MASTER);
            vanillaSounds.put(vanillaId, sound);
        }
        VANILLA_SOUNDS = vanillaSounds.build();
    }

    public static void reg(String id, SoundCategory category) {
        SOUNDS.put(id, category);
    }

    public static SoundCategory getSoundCategory(String id) {
        return SOUNDS.get(id);
    }

    public static String getVanillaId(Sound sound) {
        return "minecraft:" + sound.name().toLowerCase().replaceAll("_", ".");
    }

    /**
     * Returns a vanilla Minecraft sound with the given ID, or null if none exists.
     *
     * @param id the id
     * @return the sound, or null if none match
     */
    public static Sound getVanillaSound(String id) {
        return VANILLA_SOUNDS.get(id);
    }

    public static SoundCategory getCategory(int category) {
        return SoundCategory.values()[category];
    }

    public static Map<String, SoundCategory> getSounds() {
        return ImmutableMap.<String, SoundCategory>builder().putAll(SOUNDS).build();
    }
}
