package net.lightstone.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class EntityManager implements Iterable<Entity> {

	private final Map<Integer, Entity> entities = new HashMap<Integer, Entity>();
	private final Map<Class<? extends Entity>, Set<? extends Entity>> groupedEntities = new HashMap<Class<? extends Entity>, Set<? extends Entity>>();
	private int nextId = 1;

	@SuppressWarnings("unchecked")
	public <T extends Entity> Collection<T> getAll(Class<T> type) {
		Set<T> set = (Set<T>) groupedEntities.get(type);
		if (set == null) {
			set = new HashSet<T>();
			groupedEntities.put(type, set);
		}
		return set;
	}

	public Entity getEntity(int id) {
		return entities.get(id);
	}

	@SuppressWarnings("unchecked")
	int allocate(Entity entity) {
		for (int id = nextId; id < Integer.MAX_VALUE; id++) {
			if (!entities.containsKey(id)) {
				entities.put(id, entity);
				entity.id = id;
				((Collection<Entity>) getAll(entity.getClass())).add(entity);
				nextId = id + 1;
				return id;
			}
		}

		for (int id = Integer.MIN_VALUE; id < -1; id++) { // as -1 is used as a special value
			if (!entities.containsKey(id)) {
				entities.put(id, entity);
				((Collection<Entity>) getAll(entity.getClass())).add(entity);
				nextId = id + 1;
				return id;
			}
		}

		throw new IllegalStateException("No free entity ids");
	}

	void deallocate(Entity entity) {
		entities.remove(entity.getId());
		getAll(entity.getClass()).remove(entity);
	}

	@Override
	public Iterator<Entity> iterator() {
		return entities.values().iterator();
	}

}
