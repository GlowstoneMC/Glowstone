package net.glowstone.block.flattening.generated;

import com.google.common.collect.Sets;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;

public class FlatteningUtil {
    public static Set<BlockFace> getPossibleBlockFaces(Material material) {
        return Sets.newHashSet(GeneratedFlatteningData.DIRECTIONAL_POSSIBLE_FACES.get(material));
    }
}
