package net.glowstone.block.flattening.generated;

import com.google.common.collect.ImmutableBiMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class FlatteningUtil {
    private static final ImmutableBiMap<Material, Integer> MAT_TO_ID = GeneratedFlatteningData.MATERIAL_ID_MAP.inverse();

    public static List<BlockFace> getPossibleBlockFaces(Material material) {
        return new ArrayList<>(GeneratedFlatteningData.DIRECTIONAL_POSSIBLE_FACES.get(material));
    }

    public static int getStateBaseId(int stateId) {
        return GeneratedFlatteningData.STATE_BASE_IDS[stateId];
    }

    public static int getStateDefaultId(int stateId) {
        return GeneratedFlatteningData.STATE_DEFAULT_IDS[stateId];
    }

    public static int getMaterialDefaultId(Material material) {
        return getStateDefaultId(getMaterialBaseId(material));
    }

    public static Map<String, Object> getDefaultProperties(Material material) {
        return deserialize(material, getStateSerialFromStateId(getMaterialDefaultId(material)));
    }

    public static Map<String, Object> deserialize(Material material, int stateSubId) {
        return GeneratedFlatteningData.MATERIAL_PROPERTIES.get(material).deserialize(stateSubId);
    }

    public static int getMaterialBaseId(Material material) {
        return MAT_TO_ID.get(material);
    }

    public static Material getMaterialFromBaseId(int baseId) {
        return GeneratedFlatteningData.MATERIAL_ID_MAP.get(baseId);
    }

    public static Material getMaterialFromStateId(int stateId) {
        int baseId = getStateBaseId(stateId);
        return getMaterialFromBaseId(baseId);
    }

    public static int getStateSerialFromStateId(int stateId) {
        int baseId = getStateBaseId(stateId);
        return stateId - baseId;
    }
}
