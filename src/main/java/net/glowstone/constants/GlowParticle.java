package net.glowstone.constants;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Id mappings for particles.
 */
public final class GlowParticle {

    private static final int[] EMPTY = new int[0];
    private static final int[] ONE_EMPTY = new int[]{0};
    private static final int[] TWO_EMPTY = new int[]{0, 0};

    private static final Map<Object, Integer> ids = new HashMap<>();

    static {
        // TODO: update those IDs with http://wiki.vg/Pre-release_protocol#Particle (1.13)
        // http://wiki.vg/Protocol#Particle IDs, but keyed by API enum
        set(Particle.EXPLOSION_NORMAL, Effect.EXPLOSION, 0);       // explode
        set(Particle.EXPLOSION_LARGE, Effect.EXPLOSION_LARGE, 1); // largeexplode
        set(Particle.EXPLOSION_HUGE, Effect.EXPLOSION_HUGE, 2);  // hugeexplosion
        set(Particle.FIREWORKS_SPARK, Effect.FIREWORKS_SPARK, 3); // fireworksSpark
        set(Particle.WATER_BUBBLE, 4); // bubble
        set(Particle.WATER_SPLASH, 5); // splash
        set(Particle.WATER_WAKE, 6); // wake
        set(Particle.SUSPENDED, 7); // suspended
        set(Particle.SUSPENDED_DEPTH, Effect.VOID_FOG, 8); // depthsuspend
        set(Particle.CRIT, Effect.CRIT, 9); // crit
        set(Particle.CRIT_MAGIC, Effect.MAGIC_CRIT, 10); // magicCrit
        set(Particle.SMOKE_NORMAL, Effect.PARTICLE_SMOKE, 11); // smoke
        set(Particle.SMOKE_LARGE, Effect.LARGE_SMOKE, 12); // largesmoke
        set(Particle.SPELL, Effect.POTION_SWIRL, 13); // spell
        set(Particle.SPELL_INSTANT, Effect.INSTANT_SPELL, 14); // instantSpell
        set(Particle.SPELL, Effect.SPELL, 15); // spell
        set(Particle.SPELL_MOB_AMBIENT, Effect.POTION_SWIRL_TRANSPARENT, 16); // mobSpellAmbient
        set(Particle.SPELL_WITCH, Effect.WITCH_MAGIC, 17); // witchMagic
        set(Particle.DRIP_WATER, Effect.WATERDRIP, 18); // dripWater
        set(Particle.DRIP_LAVA, Effect.LAVADRIP, 19); // dripLava
        set(Particle.VILLAGER_ANGRY, Effect.VILLAGER_THUNDERCLOUD, 20); // angryVillager
        set(Particle.VILLAGER_HAPPY, Effect.HAPPY_VILLAGER, 21); // happyVillager
        set(Particle.TOWN_AURA, Effect.SMALL_SMOKE, 22); // townaura
        set(Particle.NOTE, Effect.NOTE, 23); // note
        set(Particle.PORTAL, Effect.PORTAL, 24); // portal
        set(Particle.ENCHANTMENT_TABLE, Effect.FLYING_GLYPH, 25); // enchantmenttable
        set(Particle.FLAME, Effect.FLAME, 26); // flame
        set(Particle.LAVA, Effect.LAVA_POP, 27); // lava
        set(Particle.FOOTSTEP, Effect.FOOTSTEP, 28); // footstep
        set(Particle.CLOUD, Effect.CLOUD, 29); // cloud
        set(Particle.REDSTONE, Effect.COLOURED_DUST, 30); // reddust
        set(Particle.SNOWBALL, Effect.SNOWBALL_BREAK, 31); // snowballpoof
        set(Particle.SNOW_SHOVEL, Effect.SNOW_SHOVEL, 32); // snowshovel
        set(Particle.SLIME, Effect.SLIME, 33); // slime
        set(Particle.HEART, Effect.HEART, 34); // heart
        set(Particle.ITEM_CRACK, Effect.ITEM_BREAK, 36); // iconcrack_(id)_(data)
        set(Particle.BLOCK_CRACK, Effect.TILE_BREAK, 37); // blockcrack_(id+(data<<12))
        set(Particle.BLOCK_DUST, Effect.TILE_DUST, 38); // blockdust_(id)
    }

    private GlowParticle() {
    }

    /**
     * Get the particle id for a specified Particle.
     *
     * @param particle the Particle.
     * @return the particle id.
     */
    public static int getId(Particle particle) {
        checkNotNull(particle, "particle cannot be null");
        return ids.get(particle);
    }

    /**
     * Get the particle id for a specified Particle.
     *
     * @param particle the Particle.
     * @return the particle id.
     */
    public static int getId(Effect particle) {
        checkNotNull(particle, "particle cannot be null");
        return ids.get(particle);
    }

    /**
     * Convert a MaterialData to an extData array if possible for a particle.
     *
     * @param particle the Particle to validate.
     * @param material the MaterialData to convert.
     * @return The extData array for the particle effect.
     */
    public static int[] getExtData(Effect particle, MaterialData material) {
        switch (particle) {
            case ITEM_BREAK:
                if (material == null) {
                    return TWO_EMPTY;
                }

                // http://wiki.vg/Protocol#Particle
                // data 'Length depends on particle. "iconcrack" [Effect.ITEM_BREAK] has length of
                // 2, "blockcrack" and "blockdust" have lengths of 1, the rest have 0'
                // iconcrack_(id)_(data) 36
                return new int[]{material.getItemTypeId(), material.getData()};
            case TILE_BREAK:
                if (material == null) {
                    return ONE_EMPTY;
                }

                return new int[]{material.getItemTypeId() + (material.getData() << 12)};
            case TILE_DUST:
                if (material == null) {
                    return ONE_EMPTY;
                }

                return new int[]{material.getItemTypeId()};
            default:
                return EMPTY;
        }
    }

    /**
     * Convert an object to an extData array if possible for a particle.
     *
     * @param particle the Particle to validate.
     * @param object the Object to convert.
     * @return The extData array for the particle effect.
     */
    public static int[] getExtData(Particle particle, Object object) {
        if (particle.getDataType() == Void.class) {
            return EMPTY;
        }

        if (particle.getDataType() == MaterialData.class) {
            if (object == null) {
                return ONE_EMPTY;
            }

            MaterialData material = (MaterialData) object;

            return new int[]{material.getItemTypeId() + (material.getData() << 12)};
        }

        if (particle.getDataType() == ItemStack.class) {
            if (object == null) {
                return TWO_EMPTY;
            }

            ItemStack item = (ItemStack) object;

            // http://wiki.vg/Protocol#Particle
            // data 'Length depends on particle. "iconcrack" [Effect.ITEM_BREAK] has length of 2,
            // "blockcrack" and "blockdust" have lengths of 1, the rest have 0'
            // iconcrack_(id)_(data) 36
            return new int[]{item.getTypeId(), item.getDurability()};
        }

        // doesn't make sense, there are only 3 particle data types
        return null;
    }


    /**
     * Determine whether a particle type is considered long distance, meaning it has a higher
     * visible range than normal.
     *
     * @param particle the Particle.
     * @return True if the particle is long distance.
     */
    public static boolean isLongDistance(Effect particle) {
        switch (particle) {
            case EXPLOSION:
            case EXPLOSION_LARGE:
            case EXPLOSION_HUGE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether a particle type is considered long distance, meaning it has a higher
     * visible range than normal.
     *
     * @param particle the Particle.
     * @return True if the particle is long distance.
     */
    public static boolean isLongDistance(Particle particle) {
        switch (particle) {
            case EXPLOSION_NORMAL:
            case EXPLOSION_LARGE:
            case EXPLOSION_HUGE:
            case MOB_APPEARANCE:
                return true;
            default:
                return false;
        }
    }

    private static void set(Particle particle, Effect effect, int id) {
        ids.put(particle, id);
        ids.put(effect, id);
    }

    private static void set(Particle particle, int id) {
        ids.put(particle, id);
    }
}
