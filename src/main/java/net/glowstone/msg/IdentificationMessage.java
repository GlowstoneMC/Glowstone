package net.glowstone.msg;

public final class IdentificationMessage extends Message {

    private final int id, dimension, mode, difficulty, worldHeight, maxPlayers;
    private final String name;
    private final long seed;

    public IdentificationMessage(int id, String name, long seed, int mode, int dimension, int difficulty, int worldHeight, int maxPlayers) {
        this.id = id;
        this.name = name;
        this.seed = seed;
        this.mode = mode;
        this.dimension = dimension;
        this.difficulty = difficulty;
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
    
    public int getDifficulty() {
        return difficulty;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    @Override
    public String toString() {
        return "IdentificationMessage{id=" + id + ",name=" + name + ",seed=" + seed +
                ",gameMode=" + mode + ",dimension=" + dimension + ",difficulty=" +
                difficulty + ",worldHeight=" + worldHeight + ",maxPlayers=" + maxPlayers + "}";
    }
}
