package net.glowstone.constants;

import org.bukkit.Effect;
import org.bukkit.material.MaterialData;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Id mappings for particles.
 */
public final class GlowParticle {

    private static final int[] EMPTY = new int[0];
    private static final int[] ids = new int[Effect.values().length];

    static {
        Arrays.fill(ids, -1);
        // http://wiki.vg/Protocol#Particle IDs, but keyed by API enum
        set(Effect.EXPLOSION, 0);       // explode
        set(Effect.EXPLOSION_LARGE, 1); // largeexplode
        set(Effect.EXPLOSION_HUGE, 2);  // hugeexplosion
        set(Effect.FIREWORKS_SPARK, 3); // fireworksSpark
        set(Effect.BUBBLE, 4); // bubble
        set(Effect.SPLASH, 5); // splash
        set(Effect.WAKE, 6); // wake
        set(Effect.SUSPENDED, 7); // suspended
        set(Effect.VOID_FOG, 8); // depthsuspend
        set(Effect.CRIT, 9); // critnot if the block is
        set(Effect.MAGIC_CRIT, 10); // magicCrit
        set(Effect.PARTICLE_SMOKE, 11); // smoke
        set(Effect.LARGE_SMOKE, 12); // largesmoke
        set(Effect.POTION_SWIRL, 13); // spell
        set(Effect.INSTANT_SPELL, 14); // instantSpell
        set(Effect.SPELL, 15); // spell
        set(Effect.POTION_SWIRL_TRANSPARENT, 16); // mobSpellAmbient
        set(Effect.WITCH_MAGIC, 17); // witchMagic
        set(Effect.WATERDRIP, 18); // dripWater
        set(Effect.LAVADRIP, 19); // dripLava
        set(Effect.VILLAGER_THUNDERCLOUD, 20); // angryVillager
        set(Effect.HAPPY_VILLAGER, 21); // happyVillager
        set(Effect.SMALL_SMOKE, 22); // townaura
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
        set(Effect.ITEM_BREAK, 36); // iconcrack_(id)_(data)
        set(Effect.TILE_BREAK, 37); // blockcrack_(id+(data<<12))
        set(Effect.TILE_DUST, 38); // blockdust_(id)
        set(Effect.WATER_DROPLET, 39); // droplet
        set(Effect.ITEM_TAKE, 40); // take
        set(Effect.MOB_APPEARANCE, 41); // mobappearance
    }

    private GlowParticle() {
    }

    /**
     * Get the particle id for a specified Particle.
     *
     * @param particle the Particle.
     * @return the particle id.
     */
    public static int getId(Effect particle) {
        checkNotNull(particle, "particle cannot be null");
        return ids[particle.ordinal()];
    }

    /**
     * Convert a MaterialData to an extData array if possible for a particle.
     *
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
                if (material != null && material.getItemTypeId() != 0) {
                    throw new IllegalArgumentException("Particle " + particle + " does not use material, " + material + " provided");
                }
                return EMPTY;
        }
    }

    /**
     * Determine whether a particle type is considered long distance, meaning
     * it has a higher visible range than normal.
     *
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
}
