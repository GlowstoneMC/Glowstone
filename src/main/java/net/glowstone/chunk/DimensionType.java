package net.glowstone.chunk;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.NamespacedKey;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.OptionalLong;

@Data
@AllArgsConstructor
public final class DimensionType {

    private final boolean piglinSafe;

    private final boolean hasRaids;

    private final int monsterSpawnLightLevel;

    private final int monsterSpawnBlockLightLimit;

    private final boolean natural;

    private final float ambientLight;

    private final OptionalLong fixedTime;

    @Nullable
    private final Optional<NamespacedKey> infiniburn;

    private final boolean respawnAnchorWorks;

    private final boolean skyLight;

    private final boolean bedWorks;

    private final NamespacedKey effects;

    private final int minY;

    private final int height;

    private final int logicalHeight;

    private final double coordinateScale;

    private final boolean ultraWarm;

    private final boolean hasCeiling;

}
