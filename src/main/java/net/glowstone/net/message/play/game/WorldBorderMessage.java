package net.glowstone.net.message.play.game;

import com.flowpowered.network.Message;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/** Documented at http://wiki.vg/Protocol#World_Border */
@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorldBorderMessage implements Message {

    private final Action action;
    private final double radius;
    private final double oldRadius;
    private final double newRadius;
    private final long speed;
    private final double x;
    private final double z;
    private final int portalTeleportBoundary;
    private final int warningTime;
    private final int warningBlocks;

    // SET_SIZE
    public WorldBorderMessage(Action action, double radius) {
        this(action, radius, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    // LERP_SIZE
    public WorldBorderMessage(Action action, double oldRadius, double newRadius, long speed) {
        this(action, 0, oldRadius, newRadius, speed, 0, 0, 0, 0, 0);
    }

    // SET_CENTER
    public WorldBorderMessage(Action action, double x, double z) {
        this(action, 0, 0, 0, 0, x, z, 0, 0, 0);
    }

    // INITIALIZE
    public WorldBorderMessage(Action action, double x, double z, double oldRadius, double newRadius,
        long speed, int portalTeleportBoundary, int warningTime, int warningBlocks) {
        this(action, 0, oldRadius, newRadius, speed, x, z, portalTeleportBoundary, warningTime,
            warningBlocks);
    }

    /**
     * Creates an instance with a warning threshold.
     *
     * @param action should be {@link Action#SET_WARNING_TIME} or {@link Action#SET_WARNING_BLOCKS}
     * @param warning the warning threshold, in blocks or seconds
     */
    public WorldBorderMessage(Action action, int warning) {
        if (action == Action.SET_WARNING_TIME) {
            warningTime = warning;
            warningBlocks = 0;
        } else if (action == Action.SET_WARNING_BLOCKS) {
            warningBlocks = warning;
            warningTime = 0;
        } else {
            warningTime = 0;
            warningBlocks = 0;
        }
        this.action = action;
        radius = 0;
        oldRadius = 0;
        newRadius = 0;
        speed = 0;
        x = 0;
        z = 0;
        portalTeleportBoundary = 0;
    }

    public enum Action {
        SET_SIZE,
        LERP_SIZE,
        SET_CENTER,
        INITIALIZE,
        SET_WARNING_TIME,
        SET_WARNING_BLOCKS;

        public static Action getAction(int id) {
            Action[] values = values();
            return id < 0 || id >= values.length ? null : values[id];
        }
    }

}
