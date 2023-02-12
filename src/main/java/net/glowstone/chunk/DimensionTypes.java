package net.glowstone.chunk;

import org.bukkit.NamespacedKey;

import java.util.Optional;
import java.util.OptionalLong;

public class DimensionTypes {

    public static final DimensionType OVERWORLD = new DimensionType(
            false,
            true,
            7,
            0,
            true,
            0.0F,
            OptionalLong.empty(),
            Optional.empty(),
            false,
            true,
            true,
            NamespacedKey.minecraft("overworld"),
            -64,
            384,
            384,
            1.0D,
            false,
            false
    );

    public static final DimensionType NETHER = new DimensionType(
            true,
            false,
            15,
            0,
            false,
            0.1f,
            OptionalLong.of(18000L),
            Optional.of(NamespacedKey.minecraft("infiniburn_nether")),
            true,
            false,
            false,
            NamespacedKey.minecraft("the_nether"),
            0,
            256,
            256,
            8.0D,
            true,
            true
    );

    public static final DimensionType THE_END = new DimensionType(
            false,
            true,
            15,
            0,
            false,
            0.0f,
            OptionalLong.of(600L),
            Optional.of(NamespacedKey.minecraft("infiniburn_end")),
            false,
            false,
            false,
            NamespacedKey.minecraft("the_end"),
            0,
            256,
            256,
            1.0D,
            false,
            false
    );

    public static DimensionType getByEnvironmentId(int id) {
        switch (id) {
            case 0:
                return OVERWORLD;
            case -1:
                return NETHER;
            case 1:
                return THE_END;
            default:
                throw new IllegalStateException("Dimension id " + id + " does not match a dimension type");
        }
    }
}
