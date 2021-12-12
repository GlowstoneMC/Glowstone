package net.glowstone.entity;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;
import net.glowstone.EventFactory;
import net.glowstone.chunk.GlowChunk;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A class which manages all of the entities within a world.
 *
 * @author Graham Edgecombe
 */
public class EntityManager implements Iterable<GlowEntity> {

    /**
     * A map of all the entity ids to the corresponding entities.
     */
    private final Map<Integer, GlowEntity> entities = new ConcurrentHashMap<>();

    /**
     * A map of entity types to a set containing all entities of that type.
     */
    private final Multimap<Class<? extends GlowEntity>, GlowEntity> groupedEntities
            = Multimaps.synchronizedMultimap(MultimapBuilder.hashKeys().hashSetValues().build());

    /**
     * Returns all entities with the specified type.
     *
     * @param type The {@link Class} for the type.
     * @param <T>  The type of entity.
     * @return A collection of entities with the specified type.
     */
    @SuppressWarnings("unchecked")
    public <T extends GlowEntity> Collection<T> getAll(Class<T> type) {
        if (GlowEntity.class.isAssignableFrom(type) && groupedEntities.containsKey(type)) {
            return (Collection<T>) groupedEntities.get(type);
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Gets all entities.
     *
     * @return A collection of entities.
     */
    public Collection<GlowEntity> getAll() {
        return entities.values();
    }

    /**
     * Gets an entity by its id.
     *
     * @param id The id.
     * @return The entity, or {@code null} if it could not be found.
     */
    public GlowEntity getEntity(int id) {
        return entities.get(id);
    }

    /**
     * Registers the entity to this world.
     *
     * @param entity The entity.
     */
    @SuppressWarnings("unchecked")
    void register(GlowEntity entity) {
        if (entity.entityId == 0) {
            throw new IllegalStateException("Entity has not been assigned an id.");
        }
        entities.put(entity.entityId, entity);
        groupedEntities.put(entity.getClass(), entity);
        ((GlowChunk) entity.location.getChunk()).getRawEntities().add(entity);
        EventFactory.getInstance().callEvent(
                new EntityAddToWorldEvent(entity)
        );
    }

    /**
     * Unregister the entity to this world.
     *
     * @param entity The entity.
     */
    void unregister(GlowEntity entity) {
        EventFactory.getInstance().callEvent(new EntityRemoveFromWorldEvent(entity));
        entities.remove(entity.entityId);
        groupedEntities.remove(entity.getClass(), entity);
        ((GlowChunk) entity.location.getChunk()).getRawEntities().remove(entity);
    }

    /**
     * Notes that an entity has moved from one location to another for physics and storage purposes.
     *
     * @param entity      The entity.
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

    /**
     * Returns all entities that are inside or partly inside the given bounding box, with optionally
     * one exception.
     *
     * @param searchBox the bounding box to search inside
     * @param except    the entity to exclude, or null to include all
     * @return the entities contained in or touching {@code searchBox}, other than {@code except}
     */
    public List<Entity> getEntitiesInside(BoundingBox searchBox, GlowEntity except) {
        // todo: narrow search based on the box's corners
        return entities.values().stream()
                .filter(entity -> entity != except && entity.overlaps(searchBox))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
