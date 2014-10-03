package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class WorldBorderMessage implements Message {

    private final Action action;
    private final double radius;
    private final double oldRadius, newRadius;
    private final long speed;
    private final double x, z;
    private final int portalTeleportBoundary, warningTime, warningBlocks;

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
    public WorldBorderMessage(Action action, double x, double z, double oldRadius, double newRadius, long speed, int portalTeleportBoundary, int warningTime, int warningBlocks) {
        this(action, 0, oldRadius, newRadius, speed, x, z, portalTeleportBoundary, warningTime, warningBlocks);
    }

    // SET_WARNING_TIME, SET_WARNING_BLOCKS
    public WorldBorderMessage(Action action, int warning) {
        if (action == Action.SET_WARNING_TIME) {
            this.warningTime = warning;
            this.warningBlocks = 0;
        } else if (action == Action.SET_WARNING_BLOCKS) {
            this.warningBlocks = warning;
            this.warningTime = 0;
        } else {
            this.warningTime = 0;
            this.warningBlocks = 0;
        }
        this.action = action;
        this.radius = 0;
        this.oldRadius = 0;
        this.newRadius = 0;
        this.speed = 0;
        this.x = 0;
        this.z = 0;
        this.portalTeleportBoundary = 0;
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
