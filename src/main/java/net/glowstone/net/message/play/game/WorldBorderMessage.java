package net.glowstone.net.message.play.game;

import com.flowpowered.networking.Message;

public final class WorldBorderMessage implements Message {

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

    private final Action action;
    private final double radius;
    private final double oldRadius, newRadius;
    private final long speed;
    private final double x, z;
    private final int portalTeleportBoundary, warningTime, warningBlocks;

    // base constructor
    private WorldBorderMessage(Action action, double radius, double oldRadius, double newRadius, long speed, double x, double z, int portalTeleportBoundary, int warningTime, int warningBlocks) {
        this.action = action;
        this.radius = radius;
        this.oldRadius = oldRadius;
        this.newRadius = newRadius;
        this.speed = speed;
        this.x = x;
        this.z = z;
        this.portalTeleportBoundary = portalTeleportBoundary;
        this.warningTime = warningTime;
        this.warningBlocks = warningBlocks;
    }

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

    public Action getAction() {
        return action;
    }

    public double getRadius() {
        return radius;
    }

    public double getOldRadius() {
        return oldRadius;
    }

    public double getNewRadius() {
        return newRadius;
    }

    public long getSpeed() {
        return speed;
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }


    public int getPortalTeleportBoundary() {
        return portalTeleportBoundary;
    }

    public int getWarningTime() {
        return warningTime;
    }

    public int getWarningBlocks() {
        return warningBlocks;
    }

    @Override
    public String toString() {
        return "WorldBorderMessage{" +
                "action=" + action +
                ", radius=" + radius +
                ", oldRadius=" + oldRadius +
                ", newRadius=" + newRadius +
                ", speed=" + speed +
                ", x=" + x +
                ", z=" + z +
                ", portalTeleportBoundary=" + portalTeleportBoundary +
                ", warningTime=" + warningTime +
                ", warningBlocks=" + warningBlocks +
                '}';
    }
}
