package net.glowstone.constants;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.potion.Potion;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Conversion of data classes to raw values for Effects.
 */
public final class GlowEffect {

    private GlowEffect() {
    }

    /**
     * Get the raw data value for an Effect and its data object.
     *
     * @param effect the Effect whose data to calculate
     * @param data the original data
     * @param <T> the type of data
     * @return the raw data value
     */
    public static <T> int getDataValue(Effect effect, T data) {
        int result;
        switch (effect) {
            case POTION_BREAK:
                // 0x3f - bits of the potion corresponding to its type
                result = ((Potion) data).toDamageValue() & 0x3f;
                break;
            case RECORD_PLAY:
                checkArgument(((Material) data).isRecord(), "Invalid record type!");
                result = ((Material) data).getId();
                break;
            case SMOKE:
                // block face to data value conversion information from
                // the protocol documentation
                switch ((BlockFace) data) {
                    case SOUTH_EAST:
                        result = 0;
                        break;
                    case SOUTH:
                        result = 1;
                        break;
                    case SOUTH_WEST:
                        result = 2;
                        break;
                    case EAST:
                        result = 3;
                        break;
                    case UP:
                    case SELF:
                        result = 4;
                        break;
                    case WEST:
                        result = 5;
                        break;
                    case NORTH_EAST:
                        result = 6;
                        break;
                    case NORTH:
                        result = 7;
                        break;
                    case NORTH_WEST:
                        result = 8;
                        break;
                    default:
                        throw new IllegalArgumentException("Bad smoke direction!");
                }
                break;
            case STEP_SOUND:
                checkArgument(((Material) data).isBlock(), "Material is not a block!");
                result = ((Material) data).getId();
                break;
            default:
                result = 0;
        }
        return result;
    }

}
