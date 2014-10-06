package net.glowstone.constants;

import org.apache.commons.lang.Validate;
import org.bukkit.Particle;
import org.bukkit.material.MaterialData;

import java.util.Arrays;

import static org.bukkit.Particle.*;

/**
 * Id mappings for particles.
 */
public final class GlowParticle {

    private static final int[] EMPTY = new int[0];

    private GlowParticle() {
    }

    private static final int[] ids = new int[Particle.values().length];

    /**
     * Get the particle id for a specified Particle.
     * @param particle the Particle.
     * @return the particle id.
     */
    public static int getId(Particle particle) {
        Validate.notNull(particle, "particle cannot be null");
        return ids[particle.ordinal()];
    }

    /**
     * Convert a MaterialData to an extData array if possible for a particle.
     * @param particle the Particle to validate.
     * @param material the MaterialData to convert.
     * @return The extData array for the particle effect.
     * @throws IllegalArgumentException if data is provided incorrectly
     */
    public static int[] getData(Particle particle, MaterialData material) {
        switch (particle) {
            case ITEM_BREAK:
            case BLOCK_BREAK:
            case BLOCK_DUST:
                if (material == null) {
                    throw new IllegalArgumentException("Particle " + particle + " requires material, null provided");
                }
                if (particle == ITEM_BREAK) {
                    return new int[]{material.getItemTypeId(), material.getData()};
                }
                return new int[]{material.getItemTypeId()};
            default:
                if (material != null) {
                    throw new IllegalArgumentException("Particle " + particle + " does not use material, " + material + " provided");
                }
                return EMPTY;
        }
    }

    /**
     * Determine whether a particle type is considered long distance, meaning
     * it has a higher visible range than normal.
     * @param particle the Particle.
     * @return True if the particle is long distance.
     */
    public static boolean isLongDistance(Particle particle) {
        return particle == SMOKE_SMALL ||
                particle == EXPLOSION_LARGE ||
                particle == EXPLOSION_HUGE ||
                particle == MOB_APPEARANCE;
    }

    private static void set(Particle particle, int id) {
        ids[particle.ordinal()] = id;
    }

    static {
        Arrays.fill(ids, -1);
        set(SMOKE_SMALL, 0); // explosion normal
        set(EXPLOSION_LARGE, 1);
        set(EXPLOSION_HUGE, 2);
        set(FIREWORKS_SPARK, 3);
        set(BUBBLES, 4);
        set(WATER_SPLASH, 5);
        set(WATER_WAKE, 6);
        set(UNDERWATER, 7); // suspend
        set(VOID_FOG, 8); // depth suspend
        set(CRITICAL, 9);
        set(CRITICAL_MAGIC, 10);
        set(SMOKE, 11);
        set(SMOKE_LARGE, 12);
        set(SPELL, 13);
        set(SPELL_INSTANT, 14);
        set(SPELL_MOB, 15);
        set(SPELL_AMBIENT, 16);
        set(SPELL_WITCH, 17);
        set(DRIP_WATER, 18);
        set(DRIP_LAVA, 19);
        set(VILLAGER_ANGRY, 20);
        set(VILLAGER_HAPPY, 21);
        set(TOWN_AURA, 22);
        set(NOTE, 23);
        set(PORTAL, 24);
        set(ENCHANTMENT_TABLE, 25);
        set(FLAME, 26);
        set(LAVA_POP, 27);
        set(FOOTSTEP, 28);
        set(CLOUD, 29);
        set(REDSTONE, 30);
        set(SNOWBALL, 31);
        set(SNOW_SHOVEL, 32);
        set(SLIME, 33);
        set(HEART, 34);
        set(BARRIER, 35);
        set(ITEM_BREAK, 36);
        set(BLOCK_BREAK, 37);
        set(BLOCK_DUST, 38);
        set(WATER_DROPLET, 39);
        set(ITEM_TAKE, 40);
        set(MOB_APPEARANCE, 41);
    }
}
