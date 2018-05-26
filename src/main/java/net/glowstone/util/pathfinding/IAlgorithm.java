package net.glowstone.util.pathfinding;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.glowstone.block.GlowBlock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

/**
 * The base interface for all Pathfinding algorithms to implement from.
 */
public interface IAlgorithm {

    /**
     * Thanks to www.redblobgames.com/pathfinding/a-star/implementation.html
     *
     * @param startPoint The starting position.
     * @param endPoint The ending position.
     * @param materialWeights A map containing a {@link Material material} key, with
     *                        a {@link Double cost} as the value.
     * @param blockedMaterials A varargs of {@link Material materials} that should be
     *                         blocked from pathing.
     *
     * @return A list of {@link Vector Vectors} that make up the path found during calculation.
     */
    List<Vector> calculatePath(final GlowBlock startPoint, final GlowBlock endPoint,
                               final Map<Material, Double> materialWeights,
                               Material... blockedMaterials);

    /**
     * Used to get the locations, as vectors, of neighboring blocks.
     * @param location The location used in the calculation of neighbors.
     * @param blockedMaterials A set of materials that should be ignored.
     *
     * @return A list of locations, after removing ignored materials, that neighbor
     *         the specified block.
     */
    default List<Vector> getNeighbors(final Location location,
                                      final Set<Material> blockedMaterials) {
        List<Vector> neighbors = new ArrayList<>();
        final Vector start = location.toVector();
        neighbors.addAll(getFaceNeighbors(start));
        neighbors.addAll(getCornerNeighbors(start));

        Iterator<Vector> it  = neighbors.iterator();

        it.forEachRemaining((vector) -> {
            final Material materialAt = location.getWorld().getBlockAt(
                  vector.toLocation(location.getWorld())).getType();
            if (!materialAt.isSolid() || blockedMaterials.contains(materialAt)) {
                it.remove();
            }
        });

        return neighbors;
    }


    /**
     * Used to get the locations that neighbor a vector's faces.
     * @param vector The vector we're grabbing the face neighbors of.
     *
     * @return A set of vector's that neighbor the specified vector's faces.
     */
    default Set<Vector> getFaceNeighbors(final Vector vector) {
        Vector[] faces = new Vector[] {
            new Vector(vector.getBlockX() + 1, vector.getBlockY(), vector.getBlockZ()),
            new Vector(vector.getBlockX() - 1, vector.getBlockY(), vector.getBlockZ()),
            new Vector(vector.getBlockX(), vector.getBlockY() + 1, vector.getBlockZ()),
            new Vector(vector.getBlockX(), vector.getBlockY() - 1, vector.getBlockZ()),
            new Vector(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ() + 1),
            new Vector(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ() - 1)
        };

        return Sets.newHashSet(faces);
    }

    /**
     * Used to get the locations that neighbor a vector's corners.
     * @param vector The vector we're grabbing the corner neighbors of.
     *
     * @return A set of vector's that neighbor the specified vector's corners.
     */
    default Set<Vector> getCornerNeighbors(final Vector vector) {
        Vector[] faces = new Vector[] {
            new Vector(vector.getBlockX() + 1, vector.getBlockY() - 1, vector.getBlockZ() + 1),
            new Vector(vector.getBlockX() + 1, vector.getBlockY() - 1, vector.getBlockZ() - 1),
            new Vector(vector.getBlockX() + 1, vector.getBlockY() + 1, vector.getBlockZ() + 1),
            new Vector(vector.getBlockX() + 1, vector.getBlockY() + 1, vector.getBlockZ() - 1),
            new Vector(vector.getBlockX() - 1, vector.getBlockY() + 1, vector.getBlockZ() + 1),
            new Vector(vector.getBlockX() - 1, vector.getBlockY() + 1, vector.getBlockZ() - 1),
            new Vector(vector.getBlockX() - 1, vector.getBlockY() - 1, vector.getBlockZ() + 1),
            new Vector(vector.getBlockX() - 1, vector.getBlockY() - 1, vector.getBlockZ() - 1)
        };

        return Sets.newHashSet(faces);
    }
}
