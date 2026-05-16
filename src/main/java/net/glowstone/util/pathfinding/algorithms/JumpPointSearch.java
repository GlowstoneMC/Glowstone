package net.glowstone.util.pathfinding.algorithms;

import com.google.common.collect.Sets;
import net.glowstone.block.GlowBlock;
import net.glowstone.util.pathfinding.IAlgorithm;
import net.glowstone.util.pathfinding.PathVector;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
 * Jump Point Search algorithm implementation for 3D voxel pathfinding.
 * JPS is an optimization over A* that reduces the search space by jumping
 * over intermediate nodes in uniform-cost regions.
 */
public class JumpPointSearch implements IAlgorithm {

    private static final int MAX_ITERATIONS = 2000;
    private static final int MAX_JUMP_DISTANCE = 16;
    private static final int MAX_PATH_LENGTH = 100;

    private World world;
    private Set<Material> blocked;
    private Map<Material, Double> weights;

    @Override
    public List<Vector> calculatePath(final GlowBlock startPoint, final GlowBlock endPoint,
                                      final Map<Material, Double> materialWeights,
                                      final Material... blockedMaterials) {

        this.world = startPoint.getWorld();
        this.weights = materialWeights;
        this.blocked = Sets.newHashSet(blockedMaterials);

        Map<Vector, Double> gScore = new HashMap<>();
        Map<Vector, Vector> parents = new HashMap<>();
        Queue<PathVector> open = new PriorityQueue<>();
        Set<Vector> closed = new HashSet<>();

        Vector start = toBlockVector(startPoint.getLocation().toVector());
        Vector end = toBlockVector(endPoint.getLocation().toVector());

        gScore.put(start, 0.0);
        open.add(new PathVector(heuristic(start, end), start));
        parents.put(start, null);

        int iterations = 0;
        while (!open.isEmpty() && iterations < MAX_ITERATIONS) {
            iterations++;

            Vector current = open.poll().getVector();

            if (isCloseEnough(current, end)) {
                return reconstructPath(parents, current, start);
            }

            if (closed.contains(current)) {
                continue;
            }
            closed.add(current);

            for (Vector neighbor : getSuccessors(current, parents.get(current), end)) {
                if (closed.contains(neighbor)) {
                    continue;
                }

                double tentativeG = gScore.getOrDefault(current, Double.MAX_VALUE)
                    + current.distance(neighbor);

                if (tentativeG < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    parents.put(neighbor, current);
                    gScore.put(neighbor, tentativeG);
                    double f = tentativeG + heuristic(neighbor, end);
                    open.add(new PathVector(f, neighbor));
                }
            }
        }

        return fallbackPath(start, end);
    }

    private Vector toBlockVector(Vector v) {
        return new Vector(v.getBlockX(), v.getBlockY(), v.getBlockZ());
    }

    private List<Vector> getSuccessors(Vector current, Vector parent, Vector goal) {
        List<Vector> successors = new ArrayList<>();
        List<Vector> neighbors = pruneNeighbors(current, parent);

        for (Vector neighbor : neighbors) {
            int dx = neighbor.getBlockX() - current.getBlockX();
            int dy = neighbor.getBlockY() - current.getBlockY();
            int dz = neighbor.getBlockZ() - current.getBlockZ();

            Vector jumpPoint = jump(current, dx, dy, dz, goal);
            if (jumpPoint != null) {
                successors.add(jumpPoint);
            }
        }

        return successors;
    }

    private List<Vector> pruneNeighbors(Vector current, Vector parent) {
        List<Vector> neighbors = new ArrayList<>();

        if (parent == null) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        if (dx == 0 && dy == 0 && dz == 0) {
                            continue;
                        }
                        Vector neighbor = current.clone().add(new Vector(dx, dy, dz));
                        if (isWalkable(neighbor)) {
                            neighbors.add(neighbor);
                        }
                    }
                }
            }
        } else {
            int dx = normalize(current.getBlockX() - parent.getBlockX());
            int dy = normalize(current.getBlockY() - parent.getBlockY());
            int dz = normalize(current.getBlockZ() - parent.getBlockZ());

            Vector natural = current.clone().add(new Vector(dx, dy, dz));
            if (isWalkable(natural)) {
                neighbors.add(natural);
            }

            if (dx != 0 && dz == 0) {
                addForcedNeighbors2D(current, dx, 0, 1, neighbors);
                addForcedNeighbors2D(current, dx, 0, -1, neighbors);
            } else if (dz != 0 && dx == 0) {
                addForcedNeighbors2D(current, 0, dz, 1, neighbors);
                addForcedNeighbors2D(current, 0, dz, -1, neighbors);
            }

            if (dy != 0) {
                for (int ddx = -1; ddx <= 1; ddx++) {
                    for (int ddz = -1; ddz <= 1; ddz++) {
                        if (ddx == 0 && ddz == 0) {
                            continue;
                        }
                        Vector v = current.clone().add(new Vector(ddx, dy, ddz));
                        if (isWalkable(v)) {
                            neighbors.add(v);
                        }
                    }
                }
            }
        }

        return neighbors;
    }

    private void addForcedNeighbors2D(Vector current, int dx, int dz, int perpDir, List<Vector> neighbors) {
        Vector perpCheck;
        Vector diagonal;

        if (dx != 0) {
            perpCheck = current.clone().add(new Vector(0, 0, perpDir));
            diagonal = current.clone().add(new Vector(dx, 0, perpDir));
        } else {
            perpCheck = current.clone().add(new Vector(perpDir, 0, 0));
            diagonal = current.clone().add(new Vector(perpDir, 0, dz));
        }

        if (!isWalkable(perpCheck) && isWalkable(diagonal)) {
            neighbors.add(diagonal);
        }
    }

    private Vector jump(Vector current, int dx, int dy, int dz, Vector goal) {
        Vector next = current.clone().add(new Vector(dx, dy, dz));

        if (!isWalkable(next)) {
            return null;
        }

        if (isCloseEnough(next, goal)) {
            return next;
        }

        if (hasForcedNeighbor(next, dx, dy, dz)) {
            return next;
        }

        if (current.distance(next) > MAX_JUMP_DISTANCE) {
            return next;
        }

        if (dx != 0 && dz != 0) {
            if (jump(next, dx, 0, 0, goal) != null || jump(next, 0, 0, dz, goal) != null) {
                return next;
            }
        }

        if (dy != 0) {
            if (jump(next, 1, 0, 0, goal) != null || jump(next, -1, 0, 0, goal) != null
                || jump(next, 0, 0, 1, goal) != null || jump(next, 0, 0, -1, goal) != null) {
                return next;
            }
        }

        return jump(next, dx, dy, dz, goal);
    }

    private boolean hasForcedNeighbor(Vector pos, int dx, int dy, int dz) {
        if (dx != 0 && dz == 0 && dy == 0) {
            Vector up = pos.clone().add(new Vector(0, 0, 1));
            Vector down = pos.clone().add(new Vector(0, 0, -1));
            Vector upDiag = pos.clone().add(new Vector(dx, 0, 1));
            Vector downDiag = pos.clone().add(new Vector(dx, 0, -1));

            if ((!isWalkable(up) && isWalkable(upDiag))
                || (!isWalkable(down) && isWalkable(downDiag))) {
                return true;
            }
        }

        if (dz != 0 && dx == 0 && dy == 0) {
            Vector left = pos.clone().add(new Vector(1, 0, 0));
            Vector right = pos.clone().add(new Vector(-1, 0, 0));
            Vector leftDiag = pos.clone().add(new Vector(1, 0, dz));
            Vector rightDiag = pos.clone().add(new Vector(-1, 0, dz));

            if ((!isWalkable(left) && isWalkable(leftDiag))
                || (!isWalkable(right) && isWalkable(rightDiag))) {
                return true;
            }
        }

        return false;
    }

    private boolean isWalkable(Vector pos) {
        if (world == null) {
            return false;
        }

        Location loc = pos.toLocation(world);
        Material blockType = loc.getBlock().getType();
        Material belowType = loc.clone().subtract(0, 1, 0).getBlock().getType();

        if (blocked.contains(blockType)) {
            return false;
        }

        if (blockType.isSolid()) {
            return false;
        }

        if (blockType == Material.AIR && belowType == Material.AIR) {
            return false;
        }

        return true;
    }

    private boolean isCloseEnough(Vector current, Vector target) {
        double dx = Math.abs(current.getX() - target.getX());
        double dy = Math.abs(current.getY() - target.getY());
        double dz = Math.abs(current.getZ() - target.getZ());
        return dx <= 1 && dy <= 1 && dz <= 1;
    }

    private double heuristic(Vector from, Vector to) {
        double dx = Math.abs(to.getX() - from.getX());
        double dy = Math.abs(to.getY() - from.getY());
        double dz = Math.abs(to.getZ() - from.getZ());
        return dx + dy + dz;
    }

    private int normalize(int val) {
        if (val > 0) return 1;
        if (val < 0) return -1;
        return 0;
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
        return interpolatePath(path);
    }

    private List<Vector> interpolatePath(List<Vector> jumpPoints) {
        if (jumpPoints.size() < 2) {
            return jumpPoints;
        }

        List<Vector> fullPath = new ArrayList<>();
        fullPath.add(jumpPoints.get(0));

        for (int i = 1; i < jumpPoints.size(); i++) {
            Vector from = jumpPoints.get(i - 1);
            Vector to = jumpPoints.get(i);
            interpolateSegment(from, to, fullPath);
        }

        return fullPath;
    }

    private void interpolateSegment(Vector from, Vector to, List<Vector> path) {
        int dx = to.getBlockX() - from.getBlockX();
        int dy = to.getBlockY() - from.getBlockY();
        int dz = to.getBlockZ() - from.getBlockZ();

        int steps = Math.max(Math.abs(dx), Math.max(Math.abs(dy), Math.abs(dz)));
        if (steps == 0) {
            return;
        }

        for (int i = 1; i <= steps; i++) {
            int x = from.getBlockX() + (dx * i / steps);
            int y = from.getBlockY() + (dy * i / steps);
            int z = from.getBlockZ() + (dz * i / steps);
            path.add(new Vector(x, y, z));
        }
    }

    private List<Vector> fallbackPath(Vector start, Vector end) {
        List<Vector> path = new ArrayList<>();
        path.add(start);
        path.add(end);
        return path;
    }
}
