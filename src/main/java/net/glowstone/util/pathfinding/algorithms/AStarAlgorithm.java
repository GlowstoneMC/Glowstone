package net.glowstone.util.pathfinding.algorithms;

import com.google.common.collect.Sets;
import net.glowstone.block.GlowBlock;
import net.glowstone.util.pathfinding.IAlgorithm;
import net.glowstone.util.pathfinding.PathVector;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;


/**
 * An implementation of {@link IAlgorithm} for A* Pathfinding.
 */
public class AStarAlgorithm implements IAlgorithm {

    /**
     * Thanks to www.redblobgames.com/pathfinding/a-star/implementation.html
     *
     * @param startPoint       The starting position.
     * @param endPoint         The ending position.
     * @param materialWeights  A map containing a {@link Material material} key, with
     *                         a {@link Double cost} as the value.
     * @param blockedMaterials A varargs of {@link Material materials} that should be
     *                         blocked from pathing.
     * @return A list of {@link Vector Vectors} that make up the path found during calculation.
     */
    @Override
    public List<Vector> calculatePath(final GlowBlock startPoint, final GlowBlock endPoint,
                                      final Map<Material, Double> materialWeights,
                                      final Material... blockedMaterials) {

        Map<Vector, Double> costs = new HashMap<>();
        Map<Vector, Vector> parents = new HashMap<>();
        Queue<PathVector> open = new PriorityQueue<>();
        Set<Vector> passed = new HashSet<>();
        final Vector startVector = startPoint.getLocation().toVector();
        final Vector endVector = endPoint.getLocation().toVector();

        open.add(new PathVector(0.0, startVector));
        parents.put(startVector, startVector);
        costs.put(startVector, materialWeights.getOrDefault(startPoint.getType(), 0.0));

        while (open.size() > 0) {
            final Vector current = open.poll().getVector();

            if (current.equals(endVector)) {
                break;
            }

            passed.add(current);

            for (Map.Entry<Vector, Double> neighbor : getNeighbors(current.toLocation(
                startPoint.getWorld()), materialWeights,
                Sets.newHashSet(blockedMaterials)).entrySet()) {
                if (passed.contains(neighbor.getKey())) {
                    continue;
                }

                double cost = costs.get(current) + neighbor.getValue()
                    + current.distanceSquared(neighbor.getKey());
                if (!costs.containsKey(neighbor.getKey()) || cost < costs.get(neighbor.getKey())) {
                    costs.put(neighbor.getKey(), cost);
                    open.add(new PathVector(cost, neighbor.getKey()));
                    parents.put(neighbor.getKey(), current);
                }
            }
        }

        List<Vector> path = new ArrayList<>();
        Vector current = endVector;
        while (current != null) {
            current = parents.get(current);
            path.add(0, current);
        }
        path.add(path.size(), endVector);
        return path;
    }
}
