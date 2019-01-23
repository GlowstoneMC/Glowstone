package net.glowstone.parallelism;

import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

/**
 * Represents a {@link RecursiveAction} to be recursively pulse all entities
 * except players in parallel. For players check {@link ForkPulsePlayers}
 */
public class ForkPulse extends RecursiveAction {

    private ArrayList<GlowEntity> entities;
    private int left;
    private int right;

    /**
     * Creates a new ForkPulse to recursively pulse through current entity list
     *
     * @param entities The {@link ArrayList<GlowEntity>} to associate with entity list
     * @param left The left bound (0 to pulse from the start)
     * @param right The right bound (entities.size() - 1 to pulse to the end)
     * @return a new, rendered GlowMapCanvas
     */
    public ForkPulse(ArrayList<GlowEntity> entities, int left, int right) { // Creates a recursive
        this.entities = entities;                                      // action to pulse entities
        this.left = left;
        this.right = right;
    }

    @Override
    protected void compute() {
        if (entities.size() == 0) { // If for some reason the world has no entities,
            return;                 // this will prevent further execution.
        }
        if (right - left <= 20) { // TODO: Find a better reasoned threshold
            do { // If there are 20 or less entities in the range, pulse them sequentially
                GlowEntity entity = entities.get(left);
                if (!(entity instanceof GlowPlayer)) {
                    entity.pulse();
                }
            } while (++left <= right);
            return;
        }
        int mid = (right + left) / 2; // Otherwise split the work in two
        invokeAll(new ForkPulse(entities, left, mid),
                new ForkPulse(entities, mid + 1, right));
    }
}
