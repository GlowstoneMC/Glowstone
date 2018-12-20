package net.glowstone.util.pathfinding;

import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * A vector contained in part of a path, which is comparable based on its
 * pathfinding cost.
 */
public class PathVector implements Comparable<PathVector> {
    private final double cost;
    private final Vector vector;

    /**
     * Creates a new PathVector object.
     * @param cost The cost of this {@link PathVector} as determined during calculation.
     * @param vector The vector of this {@link PathVector}.
     */
    public PathVector(double cost, Vector vector) {
        this.cost = cost;
        this.vector = vector;
    }

    /**
     * Gets the cost of this {@link PathVector}.
     *
     * @return The cost.
     */
    public double getCost() {
        return cost;
    }

    /**
     * Gets the {@link Vector} of this {@link PathVector}.
     *
     * @return The {@link Vector}.
     */
    public Vector getVector() {
        return vector;
    }

    /**
     * Compares this {@link PathVector} to another. This is based on cost.
     * @param vector The {@link PathVector} we're comparing this one to.
     *
     * @return -1, 0, 1 based on less than, equal to, or greater than
     */
    @Override
    public int compareTo(@NotNull PathVector vector) {
        return Double.compare(cost, vector.getCost());
    }
}
