package net.glowstone.entity.ai;

import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class EntityDirector {
    private static Map<EntityType, Map<MobState, String>> mobStates = new HashMap<>();
    private static Map<String, Class<? extends EntityTask>> tasks = new HashMap<>();

    public static void registerEntityMobState(EntityType entity, MobState state, String task) {
        if (!mobStates.containsKey(entity)) {
            mobStates.put(entity, new HashMap<>());
        }
        mobStates.get(entity).put(state, task);
    }

    public static String getEntityMobStateTask(EntityType entity, MobState state) {
        if (mobStates.containsKey(entity) && mobStates.get(entity).containsKey(state)) {
            return mobStates.get(entity).get(state);
        }
        return null;
    }

    public static void registerEntityTask(String name, Class<? extends EntityTask> task) {
        tasks.put(name, task);
    }

    public static Class<? extends EntityTask> getEntityTask(String name) {
        return tasks.get(name);
    }
}
