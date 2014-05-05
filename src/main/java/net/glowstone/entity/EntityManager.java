package net.glowstone.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
     * The next id to check.
     */
    private int nextId = 1;

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
    @SuppressWarnings("unchecked")
    int allocate(GlowEntity entity) {
        for (int id = nextId; id < Integer.MAX_VALUE; id++) {
            if (!entities.containsKey(id)) {
                entities.put(id, entity);
                entity.id = id;
                ((Collection<GlowEntity>) getAll(entity.getClass())).add(entity);
                nextId = id + 1;
                return id;
            }
        }

        for (int id = Integer.MIN_VALUE; id < -1; id++) { // as -1 is used as a special value
            if (!entities.containsKey(id)) {
                entities.put(id, entity);
                ((Collection<GlowEntity>) getAll(entity.getClass())).add(entity);
                nextId = id + 1;
                return id;
            }
        }

        throw new IllegalStateException("No free entity ids");
    }

    /**
     * Deallocates the id for an entity.
     * @param entity The entity.
     */
    void deallocate(GlowEntity entity) {
        entities.remove(entity.getEntityId());
        getAll(entity.getClass()).remove(entity);
    }

    @Override
    public Iterator<GlowEntity> iterator() {
        return entities.values().iterator();
    }

}
