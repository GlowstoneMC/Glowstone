package net.glowstone.block.state;

import net.glowstone.block.state.impl.WoolStateDataReader;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import java.util.HashMap;
import java.util.Map;

public class StateSerialization {

    private static final Map<Material, BlockStateReader> READERS = new HashMap<>();

    public static BlockStateData parse(Material material, String state) throws InvalidBlockStateException {
        if (state == null || state.trim().isEmpty()) {
            return new BlockStateData();
        }
        if (material == null || getReader(material) == null) {
            throw new InvalidBlockStateException(material, state);
        }
        BlockStateData data = new BlockStateData();
        String[] split = state.trim().split(",");
        for (String section : split) {
            if (!section.contains("=") || section.indexOf('=') != section.lastIndexOf('=')) {
                throw new InvalidBlockStateException(material, state);
            }
            String[] keyVal = section.split("=");
            keyVal[0] = keyVal[0].trim().toLowerCase();
            keyVal[1] = keyVal[1].trim().toLowerCase();
            if (keyVal[0].isEmpty() || keyVal[1].isEmpty()) {
                throw new InvalidBlockStateException(material, state);
            }
            data.put(keyVal[0], keyVal[1]);
        }
        return data;
    }

    public static boolean matches(Material type, MaterialData data, BlockStateData state) throws InvalidBlockStateException {
        if (state == null || data == null || data.getItemType() != type) {
            return false;
        }
        BlockStateReader reader = getReader(type);
        if (reader == null) return false;
        return reader.matches(state, data);
    }

    public static MaterialData parseData(Material type, BlockStateData state) throws InvalidBlockStateException {
        if (type == null || state == null) {
            return null;
        }
        BlockStateReader reader = getReader(type);
        if (reader == null) throw new InvalidBlockStateException(type, state);
        return reader.read(type, state);
    }

    public static BlockStateReader getReader(Material material) {
        if (material == null) {
            return null;
        }
        return READERS.get(material);
    }

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

    static {
        READERS.put(Material.WOOL, new WoolStateDataReader());
    }
}
