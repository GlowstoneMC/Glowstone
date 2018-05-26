package net.glowstone.util.pathfinding.algorithms;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import net.glowstone.block.GlowBlock;
import net.glowstone.util.pathfinding.IAlgorithm;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.util.Vector;


/**
 * An implementation of {@link IAlgorithm} for A* Pathfinding.
 */
public class AStarAlgorithm implements IAlgorithm {

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
    @Override
    public List<Vector> calculatePath(final GlowBlock startPoint, final GlowBlock endPoint,
                                      final Map<Material, Double> materialWeights,
                                      final Material... blockedMaterials) {

        Map<Vector, Double> costs = new HashMap<>();
        Map<Vector, Vector> parents = new HashMap<>();
        Queue<Vector> open = new PriorityQueue<>();
        Set<Vector> passed = new HashSet<>();
        final Vector  startVector = startPoint.getLocation().toVector();
        final Vector  endVector = endPoint.getLocation().toVector();

        open.add(startVector);
        parents.put(startVector, startVector);
        costs.put(startVector, materialWeights.getOrDefault(startPoint.getType(), 0.0));

        while (open.size() > 0) {
            final Vector current = open.poll();

            if (current.equals(endVector)) {
                break;
            }

            passed.add(current);

            for (Vector neighbor : getNeighbors(current.toLocation(startPoint.getWorld()),
                  Sets.newHashSet(blockedMaterials))) {
                if (passed.contains(neighbor)) {
                    continue;
                }

                double cost = costs.get(current) + current.distanceSquared(neighbor);
                if (materialWeights.size() > 0) {
                    final Material materialAt = getMaterialAt(startPoint.getWorld(), neighbor);
                    cost += materialWeights.getOrDefault(materialAt, 0.0);
                }

                if (!costs.containsKey(neighbor) || cost < costs.get(neighbor)) {
                    costs.put(neighbor, cost);
                    open.add(neighbor);
                    parents.put(neighbor, current);
                }
            }
        }

        List<Vector> path = new ArrayList<>();
        path.add(endVector);
        for (Vector current = endVector; current != null; current = parents.get(current)) {
            path.add(current);
        }
        Collections.reverse(path);
        return path;
    }

    private Material getMaterialAt(final World world, final Vector vector) {
        return world.getBlockAt(vector.getBlockX(), vector.getBlockY(),
                                vector.getBlockZ()).getType();
    }
}
