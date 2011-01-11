package net.lightstone.model;

import java.util.HashMap;
import java.util.Map;


public final class EntityManager {

	private final Map<Integer, Entity> entities = new HashMap<Integer, Entity>();
	private int nextId = 1;

	public Entity getEntity(int id) {
		return entities.get(id);
	}

	int allocate(Entity entity) {
		for (int id = nextId; id < Integer.MAX_VALUE; id++) {
			if (!entities.containsKey(id)) {
				entities.put(id, entity);
				nextId = id + 1;
				return id;
			}
		}

		for (int id = Integer.MIN_VALUE; id < -1; id++) { // as -1 is used as a special value
			if (!entities.containsKey(id)) {
				entities.put(id, entity);
				nextId = id + 1;
				return id;
			}
		}

		throw new IllegalStateException("No free entity ids");
	}

	void deallocate(Entity entity) {
		entities.remove(entity.getId());
	}

}
