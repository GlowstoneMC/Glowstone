package net.glowstone.entity.ai;

import org.bukkit.entity.EntityType;

import java.util.*;

public class EntityDirector {
    private static Map<EntityType, Map<MobState, List<String>>> mobStates = new HashMap<>();
    private static Map<String, Class<? extends EntityTask>> tasks = new HashMap<>();

    static {
        registerEntityTask("look_around", LookAroundTask.class);
        registerEntityTask("look_player", LookAtPlayerTask.class);
    }

    public static void registerEntityMobState(EntityType entity, MobState state, String task) {
        if (!mobStates.containsKey(entity)) {
            mobStates.put(entity, new HashMap<>());
        }
        if (!mobStates.get(entity).containsKey(state)) {
            mobStates.get(entity).put(state, new ArrayList<>());
        }
        mobStates.get(entity).get(state).add(task);
    }

    public static Collection<String> getEntityMobStateTask(EntityType entity, MobState state) {
        if (mobStates.containsKey(entity) && mobStates.get(entity).containsKey(state)) {
            return mobStates.get(entity).get(state);
        }
        return new ArrayList<>();
    }

    public static void registerEntityTask(String name, Class<? extends EntityTask> task) {
        tasks.put(name, task);
    }

    public static Class<? extends EntityTask> getEntityTask(String name) {
        return tasks.get(name);
    }
}
