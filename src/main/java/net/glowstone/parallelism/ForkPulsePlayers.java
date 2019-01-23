package net.glowstone.parallelism;

import net.glowstone.entity.GlowPlayer;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

/**
 * Represents a {@link ForkPulse} counterpart to pulse players exclusively
 */
public class ForkPulsePlayers extends RecursiveAction {

    private ArrayList<GlowPlayer> players;
    private int left;
    private int right;

    /**
     * Creates a new ForkPulse to recursively pulse through current entity players.
     *
     * @param players The {@link ArrayList<GlowPlayer>} to associate with players list.
     * @param left The left bound (0 to pulse from the start).
     * @param right The right bound (entities.size() - 1 to pulse to the end).
     */
    public ForkPulsePlayers(ArrayList<GlowPlayer> players, int left, int right) {
        this.players = players;
        this.left = left;
        this.right = right;
    }

    @Override
    protected void compute() {
        if (players.size() == 0) {
            return;
        }
        if (right - left <= 5) { // TODO: Find a better reasoned threshold
            do {
                players.get(left).pulse();
            } while (++left <= right);
            return;
        }
        int mid = (right + left) / 2;
        invokeAll(new ForkPulsePlayers(players, left, mid),
                new ForkPulsePlayers(players, mid + 1, right));
    }
}