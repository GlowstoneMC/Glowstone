package net.glowstone.block;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
        registerDefaults(builtinValues);
        registerBuiltins(builtinValues);
    }

    @Override
    public Value getValue(Material material) {
        if (values.containsKey(material)) {
            return values.get(material);
        }
        return defaultValue;
    }

    private void registerDefaults(ConfigurationSection mainSection) {
        ConfigurationSection defSection = mainSection.getConfigurationSection("default");
        this.defaultValue = parseValue(new ValueBuilder(), defSection);
    }

    private void registerBuiltins(ConfigurationSection mainSection) {
        ValueBuilder builder = new ValueBuilder(defaultValue);

        ConfigurationSection valuesSection = mainSection.getConfigurationSection("values");
        Set<String> materials = valuesSection.getKeys(false);
        for (String strMaterial : materials) {
            Material material = Material.matchMaterial(strMaterial);
            if (material == null) {
                throw new RuntimeException("Invalid builtin/materialValues.yml: Couldn't found material: " + strMaterial);
            }
            ConfigurationSection materialSection = valuesSection.getConfigurationSection(strMaterial);
            values.put(material, parseValue(builder, materialSection));
        }
    }

    private Value parseValue(ValueBuilder value, ConfigurationSection c) {
        for (ValueKey key : ValueKey.values()) {
            if (key.isIn(c)) {
                key.update(value, c);
            }
        }
        return value.generate();
    }

    @RequiredArgsConstructor
    private static enum ValueKey {
        HARDNESS("hardness") {
            @Override
            public void update(ValueBuilder vb, ConfigurationSection cs) {
                float hardness = getNumber(cs).floatValue();
                vb.setHardness(hardness != -1 ? hardness : Float.MAX_VALUE);
            }
        },
        BLAST_RESISTANCE("blastResistance") {
            @Override
            public void update(ValueBuilder vb, ConfigurationSection cs) {
                vb.setBlastResistance(getNumber(cs).floatValue());
            }
        };

        // =========================================================
        private final String yamlName;

        public boolean isIn(ConfigurationSection s) {
            return s.get(yamlName) != null;
        }

        protected Number getNumber(ConfigurationSection section) {
            return (Number) section.get(yamlName);
        }

        protected String getStr(ConfigurationSection section) {
            return section.getString(yamlName);
        }

        @Override
        public String toString() {
            return yamlName;
        }

        public abstract void update(ValueBuilder vb, ConfigurationSection cs);
    }

    private static class ValueBuilder {
        @Setter
        private float hardness, blastResistance;

        private Value def;

        private ValueBuilder() {
            this.def = null;
        }

        private ValueBuilder(Value def) {
            this.def = def;
            clear();
        }

        private void clear() {
            if (def == null) return;

            this.hardness = def.getHardness();
            this.blastResistance = def.getBlastResistance();
        }

        private Value generate() {
            Value result = new Value(hardness, blastResistance);
            clear();
            return result;
        }
    }
}
