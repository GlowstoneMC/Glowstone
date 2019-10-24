package net.glowstone.entity.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NonNls;

public class EntityDirector {

    private static Map<EntityType, Map<MobState, List<String>>> mobStates = new EnumMap<>(EntityType.class);
    private static Map<String, Class<? extends EntityTask>> tasks = new HashMap<>();

    static {
        registerEntityTask("look_around", LookAroundTask.class);
        registerEntityTask("look_player", LookAtPlayerTask.class);
        registerEntityTask("follow_player", FollowPlayerTask.class);
    }

    /**
     * Registers a task name for the given entity and mob state.
     *
     * @param entity an entity type
     * @param state a mob state
     * @param task the name of a task
     */
    public static void registerEntityMobState(EntityType entity, MobState state,
            @NonNls String task) {
        Map<MobState, List<String>> states =
                mobStates.computeIfAbsent(entity, entity_ -> new HashMap<>());
        List<String> tasks = states.computeIfAbsent(state, state_ -> new ArrayList<>());
        tasks.add(task);
    }

    /**
     * Returns all registered task names for the given entity and mob state.
     *
     * @param entity an entity type
     * @param state a mob state
     * @return the names of the registered tasks
     */
    public static Collection<String> getEntityMobStateTask(EntityType entity, MobState state) {
        Map<MobState, List<String>> tasks = mobStates.get(entity);
        if (tasks != null && tasks.containsKey(state)) {
            return tasks.get(state);
        }
        return new ArrayList<>();
    }

    /**
     * Registers a class for a given task name.
     *
     * @param name the task name that can subsequently be passed to {@link #registerEntityMobState}
     *         to invoke this task
     * @param task the class that implements the task
     */
    public static void registerEntityTask(@NonNls String name, Class<? extends EntityTask> task) {
        tasks.put(name, task);
    }

    /**
     * Returns the class registered for a given task name.
     *
     * @param name the task name that can subsequently be passed to {@link #registerEntityMobState}
     *         to invoke this task
     * @return the class that implements the task, or null if none matches
     */
    public static Class<? extends EntityTask> getEntityTask(String name) {
        return tasks.get(name);
    }
}
