package net.glowstone.msg;

public final class IdentificationMessage extends Message {

    private final int id, dimension;
    private final String name;
    private final long seed;

    public IdentificationMessage(int id, String name, long seed, int dimension) {
        this.id = id;
        this.name = name;
        this.seed = seed;
        this.dimension = dimension;
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

    public int getDimension() {
        return dimension;
    }

}
