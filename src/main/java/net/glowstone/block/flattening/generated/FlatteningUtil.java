package net.glowstone.block.flattening.generated;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class FlatteningUtil {
    public static List<BlockFace> getPossibleBlockFaces(Material material) {
        return new ArrayList<>(GeneratedFlatteningData_2.DIRECTIONAL_POSSIBLE_FACES.get(material));
    }
}
