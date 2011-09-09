package net.glowstone.msg;

import org.bukkit.GameMode;

public final class IdentificationMessage extends Message {

    private final int id, dimension, mode, worldHeight, maxPlayers;
    private final String name;
    private final long seed;

    public IdentificationMessage(int id, String name, long seed, int mode, int dimension, int worldHeight, int maxPlayers) {
        this.id = id;
        this.name = name;
        this.seed = seed;
        this.mode = mode;
        this.dimension = dimension;
        this.worldHeight = worldHeight;
        this.maxPlayers = maxPlayers;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getSeed() {
        return seed;
    }

    public int getGameMode() {
        return mode;
    }

    public int getDimension() {
        return dimension;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public int getSomething() {
        return maxPlayers;
    }

}
