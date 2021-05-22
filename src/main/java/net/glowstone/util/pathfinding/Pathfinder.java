package net.glowstone.util.pathfinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.glowstone.block.GlowBlock;
import org.bukkit.Material;
import org.bukkit.util.Vector;

/**
 * A class used for general pathfinding.
 */
public class Pathfinder {

    private final Map<Material, Double> materialWeights;
    private final Material[] blockedMaterials;
    private final GlowBlock startPoint;
    private final GlowBlock endPoint;


    /**
     * Creates a new pathfinder object using various options.
     *
     * @param startPoint The starting position.
     * @param endPoint   The ending position.
     */
    public Pathfinder(final GlowBlock startPoint, final GlowBlock endPoint) {
        this(startPoint, endPoint, new HashMap<>(), new Material[0]);
    }

    /**
     * Creates a new pathfinder object using various options.
     *
     * @param startPoint       The starting position.
     * @param endPoint         The ending position.
     * @param blockedMaterials A varargs of {@link Material materials} that should
     *                         be blocked from pathing. This is used during
     *                         {@link #getPath(IAlgorithm)}.
     */
    public Pathfinder(final GlowBlock startPoint, final GlowBlock endPoint,
                      final Material... blockedMaterials) {
        this(startPoint, endPoint, new HashMap<>(), blockedMaterials);
    }

    /**
     * Creates a new pathfinder object using various options.
     *
     * @param startPoint      The starting position.
     * @param endPoint        The ending position.
     * @param materialWeights A map containing a {@link Material material} key,
     *                        with a {@link Double cost} as the value. This is
     *                        used during {@link #getPath(IAlgorithm)}.
     */
    public Pathfinder(final GlowBlock startPoint, final GlowBlock endPoint,
                      final Map<Material, Double> materialWeights) {
        this(startPoint, endPoint, materialWeights, new Material[0]);
    }

    /**
     * Creates a new pathfinder object using various options.
     *
     * @param startPoint       The starting position.
     * @param endPoint         The ending position.
     * @param materialWeights  A map containing a {@link Material material} key,
     *                         with a {@link Double cost} as the value. This is
     *                         used during {@link #getPath(IAlgorithm)}.
     * @param blockedMaterials A varargs of {@link Material materials} that should
     *                         be blocked from pathing. This is used during
     *                         {@link #getPath(IAlgorithm)}.
     */
    public Pathfinder(final GlowBlock startPoint, final GlowBlock endPoint,
                      final Map<Material, Double> materialWeights,
                      final Material... blockedMaterials) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.materialWeights = materialWeights;
        this.blockedMaterials = blockedMaterials;
    }

    /**
     * Used to get the path from this {@link Pathfinder Pathfinder's} start to end points.
     *
     * @param algorithm The pathfinding {@link IAlgorithm algorithm} to use.
     * @return A list of vector's, representing block locations, of the resulting path.
     */
    public List<Vector> getPath(IAlgorithm algorithm) {
        return algorithm.calculatePath(startPoint, endPoint, materialWeights, blockedMaterials);
    }
}
