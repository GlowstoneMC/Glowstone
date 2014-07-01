package net.glowstone.entity;

import net.glowstone.GlowChunk;
import org.bukkit.Chunk;
import org.bukkit.Location;

import java.util.*;

/**
 * A class which manages all of the entities within a world.
 * @author Graham Edgecombe
 */
public final class EntityManager implements Iterable<GlowEntity> {

    /**
     * A map of all the entity ids to the corresponding entities.
     */
    private final Map<Integer, GlowEntity> entities = new HashMap<>();

    /**
     * A map of entity types to a set containing all entities of that type.
     */
    private final Map<Class<? extends GlowEntity>, Set<? extends GlowEntity>> groupedEntities = new HashMap<>();

    /**
     * The last assigned id value.
     */
    private int lastId = 0;

    /**
     * Gets all entities with the specified type.
     * @param type The {@link Class} for the type.
     * @param <T> The type of entity.
     * @return A collection of entities with the specified type.
     */
    @SuppressWarnings("unchecked")
    public <T extends GlowEntity> Collection<T> getAll(Class<T> type) {
        Set<T> set = (Set<T>) groupedEntities.get(type);
        if (set == null) {
            set = new HashSet<>();
            groupedEntities.put(type, set);
        }
        return set;
    }

    /**
     * Gets all entities.
     * @return A collection of entities.
     */
    public Collection<GlowEntity> getAll() {
        return entities.values();
    }

    /**
     * Gets an entity by its id.
     * @param id The id.
     * @return The entity, or {@code null} if it could not be found.
     */
    public GlowEntity getEntity(int id) {
        return entities.get(id);
    }

    /**
     * Allocates the id for an entity.
     * @param entity The entity.
     * @return The id.
     */
    int allocate(GlowEntity entity) {
        int startedAt = lastId;
        // intentionally wraps around integer boundaries
        for (int id = lastId + 1; id != startedAt; ++id) {
            // skip special values
            if (id == -1 || id == 0) continue;

            if (!entities.containsKey(id)) {
                allocate(entity, id);
                return id;
            }
        }

        throw new IllegalStateException("No free entity ids");
    }

    @SuppressWarnings("unchecked")
    private void allocate(GlowEntity entity, int id) {
        entity.id = id;
        entities.put(id, entity);
        ((Collection<GlowEntity>) getAll(entity.getClass())).add(entity);
        ((GlowChunk) entity.location.getChunk()).getRawEntities().add(entity);
        lastId = id;
    }

    /**
     * Deallocates the id for an entity.
     * @param entity The entity.
     */
    void deallocate(GlowEntity entity) {
        entities.remove(entity.id);
        getAll(entity.getClass()).remove(entity);
        ((GlowChunk) entity.location.getChunk()).getRawEntities().remove(entity);
    }

    /**
     * Notes that an entity has moved from one location to another for
     * physics and storage purposes.
     * @param entity The entity.
     * @param newLocation The new location.
     */
    void move(GlowEntity entity, Location newLocation) {
        Chunk prevChunk = entity.location.getChunk();
        Chunk newChunk = newLocation.getChunk();
        if (prevChunk != newChunk) {
            ((GlowChunk) prevChunk).getRawEntities().remove(entity);
            ((GlowChunk) newChunk).getRawEntities().add(entity);
        }
    }

    @Override
    public Iterator<GlowEntity> iterator() {
        return entities.values().iterator();
    }

}
