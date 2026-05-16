package net.glowstone.util.pathfinding.algorithms;

import com.google.common.collect.Sets;
import net.glowstone.block.GlowBlock;
import net.glowstone.util.pathfinding.IAlgorithm;
import net.glowstone.util.pathfinding.PathVector;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;


/**
 * An implementation of {@link IAlgorithm} for A* Pathfinding.
 * Optimized for 3D voxel worlds with proper heuristics and iteration limits.
 */
public class AStarAlgorithm implements IAlgorithm {

    private static final int MAX_ITERATIONS = 1000;
    private static final int MAX_PATH_LENGTH = 100;

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

        Map<Vector, Double> gScore = new HashMap<>();
        Map<Vector, Double> fScore = new HashMap<>();
        Map<Vector, Vector> parents = new HashMap<>();
        Queue<PathVector> open = new PriorityQueue<>();
        Set<Vector> closed = new HashSet<>();
        final Vector startVector = toBlockVector(startPoint.getLocation().toVector());
        final Vector endVector = toBlockVector(endPoint.getLocation().toVector());

        double initialCost = materialWeights.getOrDefault(startPoint.getType(), 0.0);
        gScore.put(startVector, initialCost);
        fScore.put(startVector, initialCost + heuristic(startVector, endVector));
        open.add(new PathVector(fScore.get(startVector), startVector));
        parents.put(startVector, null);

        int iterations = 0;
        while (!open.isEmpty() && iterations < MAX_ITERATIONS) {
            iterations++;
            final Vector current = open.poll().getVector();

            if (isCloseEnough(current, endVector)) {
                return reconstructPath(parents, current, startVector);
            }

            if (closed.contains(current)) {
                continue;
            }
            closed.add(current);

            Set<Material> blockedSet = Sets.newHashSet(blockedMaterials);
            for (Map.Entry<Vector, Double> neighbor : getNeighbors(current.toLocation(
                startPoint.getWorld()), materialWeights, blockedSet).entrySet()) {

                Vector neighborVec = toBlockVector(neighbor.getKey());
                if (closed.contains(neighborVec)) {
                    continue;
                }

                double moveCost = neighbor.getValue() + getMovementCost(current, neighborVec);
                double tentativeG = gScore.getOrDefault(current, Double.MAX_VALUE) + moveCost;

                if (tentativeG < gScore.getOrDefault(neighborVec, Double.MAX_VALUE)) {
                    parents.put(neighborVec, current);
                    gScore.put(neighborVec, tentativeG);
                    double f = tentativeG + heuristic(neighborVec, endVector);
                    fScore.put(neighborVec, f);
                    open.add(new PathVector(f, neighborVec));
                }
            }
        }

        return fallbackPath(startVector, endVector);
    }

    private Vector toBlockVector(Vector v) {
        return new Vector(v.getBlockX(), v.getBlockY(), v.getBlockZ());
    }

    private double heuristic(Vector from, Vector to) {
        double dx = Math.abs(to.getX() - from.getX());
        double dy = Math.abs(to.getY() - from.getY());
        double dz = Math.abs(to.getZ() - from.getZ());
        return dx + dy + dz + (Math.sqrt(2) - 2) * Math.min(dx, Math.min(dy, dz));
    }

    private double getMovementCost(Vector from, Vector to) {
        double dx = Math.abs(to.getX() - from.getX());
        double dy = Math.abs(to.getY() - from.getY());
        double dz = Math.abs(to.getZ() - from.getZ());
        double verticalPenalty = dy > 0 ? 0.5 : 0;
        return Math.sqrt(dx * dx + dy * dy + dz * dz) + verticalPenalty;
    }

    private boolean isCloseEnough(Vector current, Vector target) {
        double dx = Math.abs(current.getX() - target.getX());
        double dy = Math.abs(current.getY() - target.getY());
        double dz = Math.abs(current.getZ() - target.getZ());
        return dx <= 1 && dy <= 1 && dz <= 1;
    }

    private List<Vector> reconstructPath(Map<Vector, Vector> parents, Vector end, Vector start) {
        List<Vector> path = new ArrayList<>();
        Vector current = end;
        int safety = 0;

        while (current != null && safety < MAX_PATH_LENGTH) {
            path.add(current);
            if (current.equals(start)) {
                break;
            }
            current = parents.get(current);
            safety++;
        }

        Collections.reverse(path);
        return path;
    }

    private List<Vector> fallbackPath(Vector start, Vector end) {
        List<Vector> path = new ArrayList<>();
        path.add(start);
        path.add(end);
        return path;
    }
}
