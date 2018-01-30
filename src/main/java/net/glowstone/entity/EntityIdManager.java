package net.glowstone.entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Manager responsible for allocating all entity ids on the server.
 */
public class EntityIdManager {

    /**
     * A set containing all used entity ids.
     */
    private final Set<Integer> usedIds = new HashSet<>();

    /**
     * The last assigned id value.
     */
    private int lastId;

    /**
     * Allocates the id for an entity. This method performs synchronization as it might be accessed
     * by multiple world threads simultaneously.
     *
     * @param entity The entity.
     * @return The id.
     */
    synchronized int allocate(GlowEntity entity) {
        if (entity.entityId != 0) {
            throw new IllegalStateException("Entity already has an id assigned.");
        }

        int startedAt = lastId;
        // intentionally wraps around integer boundaries
        for (int id = lastId + 1; id != startedAt; ++id) {
            // skip special values
            if (id == -1 || id == 0) {
                continue;
            }

            if (usedIds.add(id)) {
                entity.entityId = id;
                lastId = id;
                return id;
            }
        }

        throw new IllegalStateException("No free entity ids");
    }

    /**
     * Deallocates the id for an entity.
     *
     * @param entity The entity.
     */
    synchronized void deallocate(GlowEntity entity) {
        if (entity.entityId == 0) {
            throw new IllegalStateException("Entity does not have an id assigned.");
        }
        usedIds.remove(entity.entityId);
    }

}
