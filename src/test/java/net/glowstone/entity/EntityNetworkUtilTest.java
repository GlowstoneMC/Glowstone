package net.glowstone.entity;

import static org.junit.Assert.assertFalse;

import com.google.common.collect.Sets;
import java.util.Set;
import org.bukkit.entity.EntityType;
import org.junit.Test;

public class EntityNetworkUtilTest {

    // Entities that have no network IDs
    private static final Set<EntityType> EXCLUSIONS = Sets.immutableEnumSet(
            EntityType.PAINTING,            // Paintings are spawned using SpawnPaintingMessage
            EntityType.EXPERIENCE_ORB,      // Exp Orbs are spawned using SpawnXpOrb
            EntityType.ARROW,               // Arrows use the same ID as TIPPED_ARROW
            EntityType.MINECART_CHEST,      // Minecarts all use the same ID (MINECART)
            EntityType.MINECART_COMMAND,
            EntityType.MINECART_FURNACE,
            EntityType.MINECART_HOPPER,
            EntityType.MINECART_MOB_SPAWNER,
            EntityType.MINECART_TNT,
            EntityType.LINGERING_POTION,    // Lingering Potions have no ID (???)
            EntityType.LIGHTNING,           // Lightning is spawned using SpawnLightningMessage
            EntityType.PLAYER,              // Players are spawned using SpawnPlayerMessage
            EntityType.COMPLEX_PART,        // Legacy
            EntityType.WEATHER,             // Legacy
            EntityType.UNKNOWN
    );

    @Test
    public void testAllEntitiesCovered() {
        EntityType[] allTypes = EntityType.values();
        for (EntityType type : allTypes) {
            if (EXCLUSIONS.contains(type)) {
                continue;
            }

            assertFalse("Entity type '" + type + "' has no registered network ID.",
                    EntityNetworkUtil.getMobId(type) == -1
                            && EntityNetworkUtil.getObjectId(type) == -1);
        }
    }
}
