package net.glowstone;

import net.glowstone.entity.GlowPlayer;
import net.glowstone.net.message.play.game.WorldBorderMessage;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

public class GlowWorldBorder implements WorldBorder {

    private final World world;
    public double size, futureSize, step;
    private Location center;
    private double damageBuffer, damagePerBlock;
    private int warningTime, warningDistance;
    public long time;

    public GlowWorldBorder(World world) {
        this.world = world;
        size = 60000000;
        time = 0;
        futureSize = size;
        step = 0;
        center = new Location(world, 0, 0, 0);
        damageBuffer = 5;
        damagePerBlock = 0.2;
        warningTime = 15;
        warningDistance = 5;
    }

    public void broadcast(WorldBorderMessage message) {
        for (Player player : world.getPlayers()) {
            ((GlowPlayer) player).getSession().send(message);
        }
    }

    public WorldBorderMessage createMessage() {
        return new WorldBorderMessage(WorldBorderMessage.Action.INITIALIZE, center.getX(), center.getZ(), size, futureSize, time * 1000, 29999984, warningTime, warningDistance);
    }

    public void pulse() {
        if (step != 0) {
            size += step;
            if (Math.abs(size - futureSize) < 1) {
                // completed
                size = futureSize;
                time = 0;
                step = 0;
            }
        }
    }

    @Override
    public void reset() {
        setSize(60000000);
        time = 0;
        futureSize = size;
        step = 0;
        setCenter(new Location(world, 0, 0, 0));
        setDamageBuffer(5);
        setDamageAmount(0.2);
        setWarningTime(15);
        setWarningDistance(5);
    }

    @Override
    public double getSize() {
        return size;
    }

    @Override
    public void setSize(double size) {
        this.size = size;
        this.futureSize = size;
        broadcast(new WorldBorderMessage(WorldBorderMessage.Action.SET_SIZE, size));
    }

    @Override
    public void setSize(double size, long seconds) {
        if (seconds <= 0) {
            setSize(size);
            return;
        }
        long ticks = seconds * 20;
        step = (size - this.size) / (double) ticks;
        futureSize = size;
        time = seconds;
        broadcast(new WorldBorderMessage(WorldBorderMessage.Action.LERP_SIZE, this.size, futureSize, time * 1000));
    }

    @Override
    public Location getCenter() {
        return center;
    }

    @Override
    public void setCenter(double x, double y) {
        setCenter(new Location(world, 0, 0, 0));
    }

    @Override
    public void setCenter(Location location) {
        center = location.clone();
        broadcast(new WorldBorderMessage(WorldBorderMessage.Action.SET_CENTER, center.getX(), center.getZ()));
    }

    @Override
    public double getDamageBuffer() {
        return damageBuffer;
    }

    @Override
    public void setDamageBuffer(double blocks) {
        this.damageBuffer = blocks;
    }

    @Override
    public double getDamageAmount() {
        return damagePerBlock;
    }

    @Override
    public void setDamageAmount(double damage) {
        this.damagePerBlock = damage;
    }

    @Override
    public int getWarningTime() {
        return warningTime;
    }

    @Override
    public void setWarningTime(int seconds) {
        this.warningTime = seconds;
        broadcast(new WorldBorderMessage(WorldBorderMessage.Action.SET_WARNING_TIME, seconds));
    }

    @Override
    public int getWarningDistance() {
        return warningDistance;
    }

    @Override
    public void setWarningDistance(int distance) {
        this.warningDistance = distance;
        broadcast(new WorldBorderMessage(WorldBorderMessage.Action.SET_WARNING_BLOCKS, distance));
    }

    @Override
    public boolean isInside(Location location) {
        Location max = center.clone().add(size / 2, 0, size / 2), min = center.clone().subtract(size / 2, 0, size / 2);
        return location.getX() <= max.getX() && location.getZ() <= max.getZ() && location.getX() >= min.getX() && location.getZ() >= min.getZ();
    }
}
