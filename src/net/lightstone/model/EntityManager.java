/*
 * Copyright (c) 2010-2011 Graham Edgecombe.
 *
 * This file is part of Lightstone.
 *
 * Lightstone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Lightstone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Lightstone.  If not, see <http://www.gnu.org/licenses/>.
 */

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
