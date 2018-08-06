package net.glowstone.block.flattening.generated;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class FlatteningUtil {
    public static List<BlockFace> getPossibleBlockFaces(Material material) {
        return new ArrayList<>(GeneratedFlatteningData.DIRECTIONAL_POSSIBLE_FACES.get(material));
    }

    public static int getStateBaseId(int stateId) {
        return GeneratedFlatteningData.STATE_BASE_IDS[stateId];
    }
}
