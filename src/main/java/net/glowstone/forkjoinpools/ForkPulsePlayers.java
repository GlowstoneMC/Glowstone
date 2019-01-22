package net.glowstone.forkjoinpools;

import net.glowstone.entity.GlowPlayer;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;

public class ForkPulsePlayers extends RecursiveAction { // Just like ForkPulse but for GlowPlayer entities only

    private ArrayList<GlowPlayer> list;
    private int left;
    private int right;

    public ForkPulsePlayers(ArrayList<GlowPlayer> list, int left, int right) {
        this.list = list;
        this.left = left;
        this.right = right;
    }

    @Override
    protected void compute() {
        if (list.size() == 0) {
            return;
        }
        if (right - left <= 5) { // TODO: Find a more reasoned threshold
            do {
                list.get(left).pulse();
            } while (++left <= right);
            return;
        }
        int mid = (right + left) / 2;
        invokeAll(new ForkPulsePlayers(list, left, mid), new ForkPulsePlayers(list, mid + 1, right));
    }
}
