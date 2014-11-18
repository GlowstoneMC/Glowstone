package net.glowstone.block;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class BuiltinMaterialValueManager implements MaterialValueManager {
    private final Map<Material, Value> values;
    private Value defaultValue;

    public BuiltinMaterialValueManager() {
        values = new EnumMap<>(Material.class);

        YamlConfiguration builtinValues = YamlConfiguration.loadConfiguration(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("builtin/materialValues.yml")));

        this.defaultValue = new Value(builtinValues.getConfigurationSection("default"));
        registerBuiltins(builtinValues);
    }

    private void registerBuiltins(ConfigurationSection mainSection) {
        ConfigurationSection valuesSection = mainSection.getConfigurationSection("values");
        Set<String> materials = valuesSection.getKeys(false);
        for (String strMaterial : materials) {
            Material material = Material.matchMaterial(strMaterial);
            if (material == null) {
                throw new RuntimeException("Invalid builtin/materialValues.yml: Couldn't found material: " + strMaterial);
            }
            ConfigurationSection materialSection = valuesSection.getConfigurationSection(strMaterial);
            values.put(material, new Value(materialSection));
        }
    }

    @Override
    public MaterialValueManager.Value getValue(Material material) {
        if (values.containsKey(material))
            return values.get(material);
        return defaultValue;
    }

    private final class Value implements MaterialValueManager.Value {
        private final ConfigurationSection section;

        Value(ConfigurationSection section) {
            this.section = section;
        }

        private Object get(String name) {
            Object got = section.get(name);
            if (got == null)
                return defaultValue.get(name);
            return got;
        }

        @Override
        public float getHardness() {
            float hardness = ((Number) get("hardness")).floatValue();
            return hardness == -1 ? Float.MAX_VALUE : hardness;
        }

        @Override
        public float getBlastResistance() {
            return ((Number) get("blastResistance")).floatValue();
        }
    }
}
