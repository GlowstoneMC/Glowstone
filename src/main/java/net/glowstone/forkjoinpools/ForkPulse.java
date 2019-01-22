package net.glowstone.forkjoinpools;

import net.glowstone.entity.GlowEntity;
import net.glowstone.entity.GlowPlayer;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

public class ForkPulse extends RecursiveAction {

    private ArrayList<GlowEntity> entities;
    private int left;
    private int right;

    public ForkPulse(ArrayList<GlowEntity> entities, int left, int right) { // Creates a recursive action
        this.entities = entities;                                           // to pulse entities
        this.left = left;
        this.right = right;
    }

    @Override
    protected void compute() {
        if (entities.size() == 0) { // If for some reason the world has no entities,
            return;                 // this will prevent further execution.
        }
        if (right - left <= 20) { // TODO: Find a more reasoned threshold
            do {    // If there are 20 or less entities in the range, pulse them sequentially
                GlowEntity entity = entities.get(left);
                if (!(entity instanceof GlowPlayer)) {
                    entity.pulse();
                }
            } while (++left <= right);
            return;
        }
        int mid = (right + left) / 2; // Otherwise split the work in two
        invokeAll(new ForkPulse(entities, left, mid), new ForkPulse(entities, mid + 1, right));
    }
}
