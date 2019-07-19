package net.glowstone.block.state;

import java.util.HashMap;
import java.util.Map;
import net.glowstone.block.state.impl.WoolStateDataReader;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class StateSerialization {

    private static final Map<Material, BlockStateReader> READERS = new HashMap<>();

    static {
        // TODO: 1.13 wool colors
        READERS.put(Material.LEGACY_WOOL, new WoolStateDataReader());
    }

    /**
     * Reads a {@link BlockStateData} instance from a string.
     *
     * @param material the block type
     * @param state the state as a string, or null
     * @return the default state if {@code state} is null, empty or "*" after stripping leading and
     *         trailing whitespace; otherwise, a state parsed from the string.
     * @throws InvalidBlockStateException if {@code type} isn't a block type with a
     *         {@link BlockStateReader}, or {@code state} is an invalid block-state string
     */
    public static BlockStateData parse(Material material, String state)
        throws InvalidBlockStateException {
        if (state == null || state.trim().isEmpty() || state.trim().equals("*")) {
            return new BlockStateData();
        }
        BlockStateReader reader = getReader(material);
        if (material == null || reader == null) {
            throw new InvalidBlockStateException(material, state);
        }
        BlockStateData data = new BlockStateData();
        String[] split = state.trim().split(",");
        for (String section : split) {
            if (!section.contains("=") || section.indexOf('=') != section.lastIndexOf('=')) {
                throw new InvalidBlockStateException(material, state);
            }
            String[] keyVal = section.split("=");
            if (keyVal.length < 2) {
                throw new InvalidBlockStateException(material, state);
            }
            keyVal[0] = keyVal[0].trim().toLowerCase();
            keyVal[1] = keyVal[1].trim().toLowerCase();
            if (keyVal[0].isEmpty() || keyVal[1].isEmpty()) {
                throw new InvalidBlockStateException(material, state);
            }
            if (!reader.getValidStates().contains(keyVal[0])) {
                throw new InvalidBlockStateException(material, state);
            }
            data.put(keyVal[0], keyVal[1]);
        }
        return data;
    }

    /**
     * Returns whether the given {@link MaterialData} and the given {@link BlockStateData} are valid
     * for the given block type and describe the same state.
     * @param type the block type, or null
     * @param data the block state that's a {@link MaterialData}, or null
     * @param state the block state that's a {@link BlockStateData}, or null
     * @return true if all parameters are non-null, {@code data} is valid for {@code type}, and
     *         {@code state} is empty or matches {@code data}; false otherwise
     * @throws InvalidBlockStateException if {@code type} is not null but isn't a block type with a
     *         {@link BlockStateReader}
     */
    public static boolean matches(Material type, MaterialData data, BlockStateData state)
        throws InvalidBlockStateException {
        if (state == null || data == null || data.getItemType() != type) {
            return false;
        }
        if (state.isEmpty()) {
            return true;
        }
        BlockStateReader reader = getReader(type);
        if (reader == null) {
            return false;
        }
        return reader.matches(state, data);
    }

    /**
     * Converts a {@link BlockStateData} instance to a {@link MaterialData} instance.
     *
     * @param type the block type, or null
     * @param state the block state, or null
     * @return the block state as a {@link MaterialData} instance, or null if either parameter is
     *         null
     * @throws InvalidBlockStateException if {@code type} is not null but isn't a block type with a
     *         {@link BlockStateReader}
     */
    public static MaterialData parseData(Material type, BlockStateData state)
        throws InvalidBlockStateException {
        if (type == null || state == null) {
            return null;
        }
        BlockStateReader reader = getReader(type);
        if (reader == null) {
            throw new InvalidBlockStateException(type, state);
        }
        return reader.read(type, state);
    }

    /**
     * Returns the {@link BlockStateReader} for a block type.
     *
     * @param material a material, or null
     * @return the {@link BlockStateReader} for {@code material}, or null if {@code material} is
     *         null or not a block type that has a {@link BlockStateReader}
     */
    public static BlockStateReader<?> getReader(Material material) {
        if (material == null) {
            return null;
        }
        return READERS.get(material);
    }

    /**
     * Returns the {@link DyeColor} with a given name (case-insensitive).
     *
     * @param color the name of a color, or null
     * @return the {@link DyeColor} with that name, or null if {@code color} is null or no colors
     *         match
     */
    public static DyeColor getColor(String color) {
        if (color == null) {
            return null;
        }
        try {
            return DyeColor.valueOf(color.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
