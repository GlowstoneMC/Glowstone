package net.glowstone.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class CommandTarget {

    private final CommandSender sender;
    /**
     * The type of selector (target).
     *
     * @return the type of selector of this target
     */
    @Getter
    private final SelectorType selector;
    @NonNls
    private final HashMap<String, SelectorValue> arguments;

    /**
     * Parses the target of the command with the given argument. For example, a target could be
     * "@r[c=5]", which would get 5 random players.
     *
     * @param sender the sender that used this target selector
     * @param target the un-parsed command target
     */
    public CommandTarget(CommandSender sender, String target) {
        this.sender = sender;
        this.selector = SelectorType.get(target.charAt(1));
        this.arguments = new HashMap<>();
        if (target.length() > 2 && target.charAt(2) == '[' && target.endsWith("]")) {
            String[] args = target.substring(3, target.length() - 1).split(",");
            for (String arg : args) {
                String key = arg.split("=")[0];
                String valueRaw = "";
                if (arg.split("=").length > 1) {
                    valueRaw = arg.split("=")[1];
                }
                SelectorValue value = new SelectorValue(valueRaw);
                this.arguments.put(key, value);
            }
        }
    }

    /**
     * The arguments of the selector (target).
     *
     * @return the arguments of the selector of this target
     */
    public HashMap<String, SelectorValue> getArguments() {
        // TODO: Defensive copy
        return arguments;
    }

    /////////////////////////////////////////////////////////////////
    // Sample arguments

    private Integer getCount() {
        if (arguments.containsKey("c")) {
            return Integer.valueOf(arguments.get("c").getValue());
        }
        if (selector == SelectorType.RANDOM || selector == SelectorType.NEAREST_PLAYER) {
            return 1;
        }
        return null;
    }

    private Integer getX() {
        if (arguments.containsKey("x")) {
            return Integer.valueOf(arguments.get("x").getValue());
        }
        return null;
    }

    private Integer getY() {
        if (arguments.containsKey("y")) {
            return Integer.valueOf(arguments.get("y").getValue());
        }
        return null;
    }

    private Integer getZ() {
        if (arguments.containsKey("z")) {
            return Integer.valueOf(arguments.get("z").getValue());
        }
        return null;
    }

    private Integer getMaxLevel() {
        if (arguments.containsKey("l")) {
            return Integer.valueOf(arguments.get("l").getValue());
        }
        return null;
    }

    private Integer getMinLevel() {
        if (arguments.containsKey("lm")) {
            return Integer.valueOf(arguments.get("lm").getValue());
        }
        return null;
    }

    private List<GameMode> getGameModes() {
        if (arguments.containsKey("m")) {
            SelectorValue value = arguments.get("m");
            if (!value.isInverted() && !value.getValue().equals("-1")) {
                return Arrays.asList(GameMode.getByValue(Integer.parseInt(value.getValue())));
            } else {
                return Arrays.stream(GameMode.values())
                    .filter(mode -> mode.getValue() != Integer.parseInt(value.getValue()))
                    .collect(Collectors.toList());
            }
        }
        return null;
    }

    private int getMaxRange() {
        if (arguments.containsKey("r")) {
            return Integer.parseInt(arguments.get("r").getValue());
        }
        return 0;
    }

    private int getMinRange() {
        if (arguments.containsKey("rm")) {
            return Integer.parseInt(arguments.get("rm").getValue());
        }
        return 0;
    }

    private List<EntityType> getTypes() {
        if (arguments.containsKey("type")) {
            SelectorValue value = arguments.get("type");
            if (!value.isInverted()) {
                EntityType type = EntityType.fromName(value.getValue());
                if (type == null) {
                    try {
                        type = EntityType.valueOf(value.getValue().toUpperCase());
                    } catch (IllegalArgumentException ex) {
                        return Collections.emptyList();
                    }
                }
                return Arrays.asList(type);
            } else {
                return Arrays.stream(EntityType.values()).filter(mode -> {
                    if (mode.getName() == null) {
                        return !mode.name().equalsIgnoreCase(value.getValue());
                    }
                    return !mode.getName().equalsIgnoreCase(value.getValue());
                }).collect(Collectors.toList());
            }
        }
        if (selector == SelectorType.ALL_ENTITIES) {
            return Arrays.asList(EntityType.values());
        } else {
            return Arrays.asList(EntityType.PLAYER);
        }
    }

    /**
     * Gets all the matched entities from the target.
     *
     * @param source the location from which the targets should be found
     * @return the entities matching the query
     */
    public Entity[] getMatched(Location source) {
        if (selector == SelectorType.SENDER) {
            if (sender instanceof Entity) {
                return new Entity[]{(Entity) sender};
            } else {
                return new Entity[0];
            }
        }
        List<EntityType> types = getTypes();
        List<GameMode> gameModes = getGameModes();
        Integer count = getCount();
        int maxRadius = getMaxRange() * getMaxRange();
        int minRadius = getMinRange() * getMinRange();
        Integer minLevel = getMinLevel();
        Integer maxLevel = getMaxLevel();
        List<Entity> entities = new ArrayList<>();
        if (count == null) {
            if (selector == SelectorType.NEAREST_PLAYER || selector == SelectorType.RANDOM) {
                count = 1;
            } else {
                count = source.getWorld().getEntities().size();
            }
        }
        for (Entity entity : source.getWorld().getEntities()) {
            if (entity.getLocation().distanceSquared(source) < minRadius) {
                continue;
            }
            if (!(maxRadius == 0 || entity.getLocation().distanceSquared(source) < maxRadius)) {
                continue;
            }
            if (!types.contains(entity.getType())) {
                continue;
            }
            if (getX() != null && getX() != entity.getLocation().getBlockX()) {
                continue;
            }
            if (getY() != null && getY() != entity.getLocation().getBlockY()) {
                continue;
            }
            if (getZ() != null && getZ() != entity.getLocation().getBlockZ()) {
                continue;
            }
            if (gameModes != null && entity.getType() != EntityType.PLAYER) {
                continue;
            }
            if (gameModes != null && gameModes.contains(((Player) entity).getGameMode())) {
                continue;
            }
            if (maxLevel != null && entity.getType() != EntityType.PLAYER) {
                continue;
            }
            if (maxLevel != null && ((Player) entity).getLevel() > maxLevel) {
                continue;
            }
            if (minLevel != null && entity.getType() != EntityType.PLAYER) {
                continue;
            }
            if (minLevel != null && ((Player) entity).getLevel() < minLevel) {
                continue;
            }
            // TODO: Add more checks
            entities.add(entity);
        }
        Collections.sort(entities, new EntityDistanceComparator(count < 0, source));
        if (count > entities.size()) {
            count = entities.size();
        }
        List<Entity> matched = new ArrayList<>();
        List<Integer> used = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            if (selector == SelectorType.RANDOM) {
                while (true) {
                    int random = ThreadLocalRandom.current().nextInt(entities.size());
                    if (!used.contains(random)) {
                        matched.add(entities.get(random));
                        used.add(random);
                        break;
                    }
                }
            } else {
                matched.add(entities.get(i));
            }
        }
        return matched.toArray(new Entity[matched.size()]);
    }

    /**
     * Types of selectors, namely @p (closest player), @r (random player), @a (all players), @e
     * (all entities).
     */
    @RequiredArgsConstructor
    enum SelectorType {
        NEAREST_PLAYER('p'),
        RANDOM('r'),
        ALL_PLAYERS('a'),
        ALL_ENTITIES('e'),
        SENDER('s');

        @Getter
        private final char selector;

        public static SelectorType get(char selector) {
            for (SelectorType selectorType : values()) {
                if (selector == selectorType.getSelector()) {
                    return selectorType;
                }
            }
            return null;
        }
    }

    /**
     * Represents the value of a selector argument.
     */
    public static class SelectorValue {

        /** The value of the argument. */
        @Getter
        private String value;
        /** Whether the argument is inverted (functionality should be done in reverse). */
        @Getter
        private boolean inverted = false;

        /**
         * Parses the arguments value from the given string.
         *
         * @param value the un-parsed value
         */
        public SelectorValue(String value) {
            if (value.startsWith("!")) {
                value = value.substring(1);
                this.inverted = true;
            }
            this.value = value;
        }
    }

    /**
     * Compares the distance of two entities, in order to sort them.
     */
    private class EntityDistanceComparator implements Comparator<Entity> {

        private boolean reversed;
        private Location source;

        private EntityDistanceComparator(boolean reversed, Location source) {
            this.reversed = reversed;
            this.source = source;
        }

        @Override
        public int compare(Entity e1, Entity e2) {
            int r = ((Double) (e1.getLocation().distanceSquared(source)))
                .compareTo(e2.getLocation().distanceSquared(source));
            if (reversed) {
                r = -r;
            }
            return r;
        }
    }
}
