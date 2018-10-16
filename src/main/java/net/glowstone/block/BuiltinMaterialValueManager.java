package net.glowstone.block;

import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import net.glowstone.inventory.ToolType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NonNls;

public class BuiltinMaterialValueManager implements MaterialValueManager {

    private final Map<Material, BuiltinValueCollection> values;
    private BuiltinValueCollection defaultValue;

    /**
     * Creates a MaterialValueManager using the data from the resource file
     * {@code builtin/materialValues.yml} in the Glowstone jar.
     */
    public BuiltinMaterialValueManager() {
        values = new EnumMap<>(Material.class);

        YamlConfiguration builtinValues = YamlConfiguration.loadConfiguration(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("builtin/materialValues.yml")));

        defaultValue = new BuiltinValueCollection(
                builtinValues.getConfigurationSection("default")); // NON-NLS
        registerBuiltins(builtinValues);
    }

    private void registerBuiltins(ConfigurationSection mainSection) {
        ConfigurationSection valuesSection
                = mainSection.getConfigurationSection("values"); // NON-NLS
        Set<String> materials = valuesSection.getKeys(false);
        for (String strMaterial : materials) {
            Material material = Material.matchMaterial(strMaterial);
            if (material == null) {
                throw new RuntimeException(
                        "Invalid builtin/materialValues.yml: Couldn't find material: "
                        + strMaterial);
            }
            ConfigurationSection materialSection
                    = valuesSection.getConfigurationSection(strMaterial);
            values.put(material, new BuiltinValueCollection(materialSection));
        }
    }

    @Override
    public ValueCollection getValues(Material material) {
        if (values.containsKey(material)) {
            return values.get(material);
        }
        return defaultValue;
    }

    private final class BuiltinValueCollection implements ValueCollection {

        private final ConfigurationSection section;

        BuiltinValueCollection(ConfigurationSection section) {
            this.section = section;
        }

        private Object get(@NonNls String name) {
            Object got = section.get(name);
            if (got == null && this != defaultValue) {
                return defaultValue.get(name);
            }
            return got;
        }

        @Override
        public float getHardness() {
            float hardness = ((Number) get("hardness")).floatValue();
            return hardness == -1 ? Float.MAX_VALUE : hardness;
        }

        @Override
        public ToolType getTool() {
            String toolName = (String) get("tool");
            return toolName == null ? null : ToolType.valueOf(toolName);
        }

        @Override
        public float getBlastResistance() {
            return ((Number) get("blastResistance")).floatValue();
        }

        @Override
        public int getLightOpacity() {
            return ((Number) get("lightOpacity")).intValue();
        }

        @Override
        public int getFlameResistance() {
            return ((Number) get("flameResistance")).intValue();
        }

        @Override
        public int getFireResistance() {
            return ((Number) get("fireResistance")).intValue();
        }

        @Override
        public double getSlipperiness() {
            return 0.6;
        }

        @Override
        public byte getBaseMapColor() {
            return ((Number) get("baseMapColor")).byteValue();
        }
    }
}
