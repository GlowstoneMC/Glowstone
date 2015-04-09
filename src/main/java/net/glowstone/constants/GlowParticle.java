package net.glowstone.constants;

import org.apache.commons.lang.Validate;
import org.bukkit.Effect;
import org.bukkit.material.MaterialData;

import java.util.Arrays;

/**
 * Id mappings for particles.
 */
public final class GlowParticle {

    private static final int[] EMPTY = new int[0];

    private GlowParticle() {
    }

    private static final int[] ids = new int[Effect.values().length];

    /**
     * Get the particle id for a specified Particle.
     * @param particle the Particle.
     * @return the particle id.
     */
    public static int getId(Effect particle) {
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
    public static int[] getData(Effect particle, MaterialData material) {
        switch (particle) {
            case ITEM_BREAK:
            case TILE_BREAK:
            case TILE_DUST:
                if (material == null) {
                    throw new IllegalArgumentException("Particle " + particle + " requires material, null provided");
                }
                if (particle == Effect.ITEM_BREAK) {
                    // http://wiki.vg/Protocol#Particle
                    // data "Length depends on particle. "iconcrack" [Effect.ITEM_BREAK] has length of 2, "blockcrack",
                    // and "blockdust" have lengths of 1, the rest have 0"
                    // iconcrack_(id)_(data) 36
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
    public static boolean isLongDistance(Effect particle) {
        return particle == Effect.EXPLOSION ||
                particle == Effect.EXPLOSION_LARGE ||
                particle == Effect.EXPLOSION_HUGE ||
                particle == Effect.MOB_APPEARANCE;
    }

    private static void set(Effect particle, int id) {
        ids[particle.ordinal()] = id;
    }

    static {
        Arrays.fill(ids, -1);
        // http://wiki.vg/Protocol#Effect Particle IDs, but keyed by API enum
        set(Effect.EXPLOSION, 0);       // explode
        set(Effect.EXPLOSION_LARGE, 1); // largeexplosion
        set(Effect.EXPLOSION_HUGE, 2);  // hugeexplosion
        set(Effect.FIREWORKS_SPARK, 3); // fireworksSpark
        set(Effect.BUBBLE, 4); // bubble
        set(Effect.WAKE, 5); // wake
        set(Effect.SPLASH, 6); // splash
        set(Effect.SUSPENDED, 7); // suspended
        set(Effect.SMALL_SMOKE, 8); // townaura
        set(Effect.CRIT, 9); // crit
        set(Effect.MAGIC_CRIT, 10); // magicCrit
        set(Effect.PARTICLE_SMOKE, 11); // smoke
        set(Effect.LARGE_SMOKE, 12); // largesmoke
        set(Effect.POTION_SWIRL, 13); // mobSpell?
        set(Effect.INSTANT_SPELL, 14); // instantSpell
        set(Effect.SPELL, 15); // spell
        //set(Effect., 16); // ?
        set(Effect.WITCH_MAGIC, 17); // witchMagic
        set(Effect.WATERDRIP, 18); // dripWater
        set(Effect.LAVADRIP, 19); // dripLava
        set(Effect.VILLAGER_THUNDERCLOUD, 20); // angryVillager
        set(Effect.HAPPY_VILLAGER, 21); // happyVillager
        set(Effect.VOID_FOG, 22); // depthsuspend
        set(Effect.NOTE, 23); // note
        set(Effect.PORTAL, 24); // portal
        set(Effect.FLYING_GLYPH, 25); // enchantmenttable
        set(Effect.FLAME, 26); // flame
        set(Effect.LAVA_POP, 27); // lava
        set(Effect.FOOTSTEP, 28); // footstep
        set(Effect.CLOUD, 29); // cloud
        set(Effect.COLOURED_DUST, 30); // reddust
        set(Effect.SNOWBALL_BREAK, 31); // snowballpoof
        set(Effect.SNOW_SHOVEL, 32); // snowshovel
        set(Effect.SLIME, 33); // slime
        set(Effect.HEART, 34); // heart
        set(Effect.BARRIER, 35); // barrier
        // below missing from wiki.vg
        set(Effect.ITEM_BREAK, 36); // iconcrack
        set(Effect.TILE_BREAK, 37); // blockcrack
        set(Effect.TILE_DUST, 38); // blockdust
        set(Effect.WATER_DROPLET, 39); // ?
        set(Effect.ITEM_TAKE, 40); // ?
        set(Effect.MOB_APPEARANCE, 41); // ?
    }
}
