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

}
