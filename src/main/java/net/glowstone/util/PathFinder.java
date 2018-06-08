package net.glowstone.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.bukkit.util.Vector;

/**
 * General class for path finding.
 * TODO: Implement other path finding algorithms (BFS, DFS, D*, potentially more)
 */

public class PathFinder {

    private final Vector startNode;
    private final Vector endNode;
    private List<Vector> path;
    private Map<Vector, Vector> fromPath;


    /**
     * Creates a new PathFinder object.
     *
     * @param startNode Starting position
     * @param endNode Ending position
     */
    public PathFinder(final Vector startNode, final Vector endNode) {
        // Save our start and end points
        this.startNode = startNode;
        this.endNode = endNode;
        this.path = new ArrayList<Vector>();
    }


    /**
     * Gets the start (initial) node.
     *
     * @return Vector object
     */
    public Vector getStartNode() {
        return startNode;
    }


    /**
     * Gets the end (goal) node.
     *
     * @return Vector object
     */
    public Vector getEndNode() {
        return endNode;
    }


    /**
     * Gets the path once it has been constructed.
     *
     * @note This method should only be called AFTER AStarSearch has been called
     * @return List of Vector objects in order from start to end
     */
    public List<Vector> getPath() {
        return path;
    }


    /**
     * Tries to find a path between the start and end points. This method currently
     * does not handle cases where a path could not potentially exist.
     */
    public void findPathAStar() {
        Queue<Vector> frontier = new PriorityQueue<>();
        this.fromPath = new HashMap<>();
        Map<Vector, Double> costSoFar = new HashMap<>();
        // HashSet<Vector> visitedNodes = new HashSet<>();

        // Add the starting node to our frontier
        // TODO: Might have to make a wrapper class for Vectors and computed distances to compare in the priority queue
        frontier.add(startNode);

        // Add the starting node to our hash map
        this.fromPath.put(startNode, null);

        // Cost from the start is 0
        costSoFar.put(startNode, 0.0);

        // Go on forever until you reach your end node or queue becomes empty
        while (!frontier.isEmpty()) {
            // Get the head of the queue
            final Vector current = frontier.poll();

            // End condition
            if (current == endNode) {
                break;
            }

            // Get all the neighbors
            Set<Vector> neighbors = neighbors(current);

            // Compute the distance for all the neighbors.
            for (final Vector nextNode : neighbors) {
                double newCost = costSoFar.get(current) + current.distanceSquared(nextNode);

                // TODO: Verify that the following 4 lines of code are consistent with A*.
                if (!costSoFar.containsKey(nextNode) || newCost < costSoFar.get(nextNode)) {
                    costSoFar.put(nextNode, newCost);
                    frontier.add(nextNode);
                    this.fromPath.put(nextNode, current);
                }

            }
        }
        reconstructPath();
    }


    /**
     * Constructs the path once a valid path is found.
     */
    private void reconstructPath() {
        path.add(endNode);
        Vector current = fromPath.get(endNode);
        while (current != null) {
            path.add(current);
            current = fromPath.get(current);
        }

        // Make sure we start at startNode, not endNode
        Collections.reverse(path);
    }


    /**
     * Heuristic function to base path choosing from.
     *
     * @param v Vector that is used to calculate the heuristic function
     * @return Distance value of type Double
     */
    private Double euclideanHeuristic(final Vector v) {
        return endNode.distanceSquared(v);
    }


    /**
     * Gets every block that is touching the unit vector from v.
     *
     * @param v Reference vector to get neighbors from
     * @return Set of Vector objects
     */
    private Set<Vector> neighbors(final Vector v) {
        Set<Vector> ret = new HashSet<>();
        ret.addAll(faceNeighbors(v));
        ret.addAll(cornerNeighbors(v));
        ret.addAll(edgeNeighbors(v));
        return ret;
    }


    /**
     * Gets the neighbors that are touching the edges of the current vector.
     *
     * @param v The vector in which all adjacent neighbors will be generated with respect to
     * @return Set of Vector objects
     */
    private Set<Vector> edgeNeighbors(final Vector v) {
        // These make a "+" symbol if drawn on paper
        Vector[] edgeVectors = new Vector[] {
            // One level (y-direction) UP and everything BUT the 4 corners, + shape
            new Vector(v.getBlockX(), v.getBlockY() + 1, v.getBlockZ() + 1),
            new Vector(v.getBlockX(), v.getBlockY() + 1, v.getBlockZ() - 1),
            new Vector(v.getBlockX() + 1, v.getBlockY() + 1, v.getBlockZ()),
            new Vector(v.getBlockX() - 1, v.getBlockY() + 1, v.getBlockZ()),

            // Same level (y-direction) but THESE ARE THE 4 corners
            new Vector(v.getBlockX() - 1, v.getBlockY(), v.getBlockZ() - 1),
            new Vector(v.getBlockX() - 1, v.getBlockY(), v.getBlockZ() + 1),
            new Vector(v.getBlockX() + 1, v.getBlockY(), v.getBlockZ() - 1),
            new Vector(v.getBlockX() + 1, v.getBlockY(), v.getBlockZ() + 1),

            // One level (y-direction) BELOW and everything BUT the 4 corners, + shape
            new Vector(v.getBlockX(), v.getBlockY() - 1, v.getBlockZ() + 1),
            new Vector(v.getBlockX(), v.getBlockY() - 1, v.getBlockZ() - 1),
            new Vector(v.getBlockX() + 1, v.getBlockY() - 1, v.getBlockZ()),
            new Vector(v.getBlockX() - 1, v.getBlockY() - 1, v.getBlockZ())
        };
        return new HashSet<>(Arrays.asList(edgeVectors));
    }


    /**
     * Gets the neighbors that are touching the faces of the current vector.
     *
     * @param v The vector in which all adjacent neighbors will be generated with respect to
     * @return Set of Vector objects
     */
    private Set<Vector> faceNeighbors(final Vector v) {
        Vector[] adjacentVectors = new Vector[] {
            // The faces if moving along the y-axis
            new Vector(v.getBlockX(), v.getBlockY() + 1, v.getBlockZ()),
            new Vector(v.getBlockX(), v.getBlockY() - 1, v.getBlockZ()),

            // The faces if moving along the x-axis
            new Vector(v.getBlockX() + 1, v.getBlockY(), v.getBlockZ()),
            new Vector(v.getBlockX() - 1, v.getBlockY(), v.getBlockZ()),

            // The faces if moving along the z-axis
            new Vector(v.getBlockX(), v.getBlockY(), v.getBlockZ() + 1),
            new Vector(v.getBlockX(), v.getBlockY(), v.getBlockZ() - 1)
        };
        return new HashSet<>(Arrays.asList(adjacentVectors));
    }


    /**
     * Gets the neighbors that are touching the corners of the current vector.
     *
     * @param v The vector in which all diagnol neighbors will be generated with respect to
     * @return Set of Vector objects
     */
    private Set<Vector> cornerNeighbors(final Vector v) {
        Vector[] cornerVectors = new Vector[] {
            // Corners one level ABOVE the current vector (in y-direction)
            new Vector(v.getBlockX() + 1, v.getBlockY() + 1, v.getBlockZ() + 1),
            new Vector(v.getBlockX() + 1, v.getBlockY() + 1, v.getBlockZ() - 1),
            new Vector(v.getBlockX() - 1, v.getBlockY() + 1, v.getBlockZ() + 1),
            new Vector(v.getBlockX() - 1, v.getBlockY() + 1, v.getBlockZ() - 1),

            // Corners one level BELOW the current vector (in y-direction)
            new Vector(v.getBlockX() + 1, v.getBlockY() - 1, v.getBlockZ() + 1),
            new Vector(v.getBlockX() + 1, v.getBlockY() - 1, v.getBlockZ() - 1),
            new Vector(v.getBlockX() - 1, v.getBlockY() - 1, v.getBlockZ() + 1),
            new Vector(v.getBlockX() - 1, v.getBlockY() - 1, v.getBlockZ() - 1)
        };
        return new HashSet<>(Arrays.asList(cornerVectors));
    }
}
